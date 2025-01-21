package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.screen.PowerUp;
import de.tum.cit.ase.bomberquest.screen.PowerUpType;
import de.tum.cit.ase.bomberquest.texture.Drawable;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Represents the game map.
 * Holds all the objects and entities in the game.
 */
public class GameMap {
    
    // A static block is executed once when the class is referenced for the first time.
    static {
        // Initialize the Box2D physics engine.
        com.badlogic.gdx.physics.box2d.Box2D.init();
    }
    
    // Box2D physics simulation parameters (you can experiment with these if you want, but they work well as they are)
    /**
     * The time step for the physics simulation.
     * This is the amount of time that the physics simulation advances by in each frame.
     * It is set to 1/refreshRate, where refreshRate is the refresh rate of the monitor, e.g., 1/60 for 60 Hz.
     */
    private static final float TIME_STEP = 1f / Gdx.graphics.getDisplayMode().refreshRate;
    /** The number of velocity iterations for the physics simulation. */
    private static final int VELOCITY_ITERATIONS = 6;
    /** The number of position iterations for the physics simulation. */
    private static final int POSITION_ITERATIONS = 2;
    /**
     * The accumulated time since the last physics step.
     * We use this to keep the physics simulation at a constant rate even if the frame rate is variable.
     */
    private float physicsTime = 0;
    
    /** The game, in case the map needs to access it. */
    private final BomberQuestGame game;
    /** The Box2D world for physics simulation. */
    private final World world;
    
    // Game objects
   // private final Player player;
    private Player player;
    
    private final Chest chest;
    
    private final Flowers[][] flowers;
    private final Wall[][] walls;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Bomb> bombs = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>();

    private Vector2 entrance;//Vector2是二维向量
    private Exit exit;
    private boolean exitRevealed = false;

    
    public GameMap(BomberQuestGame game, String mapFilePath) throws IOException {
        this.game = game;
        this.world = new World( new Vector2(0,0), true);

        //this.entrance = new Vector2(1,3);//保存入口位置
        //this.exit = new Vector2(5,5);//暂定，需要destructive wall设置
        this.exitRevealed = false;
        // Create a chest in the middle of the map
        this.chest = new Chest(world, 3, 3);
        // Create flowers in a 7x7 grid
        this.flowers = new Flowers[Gdx.graphics.getWidth()][Gdx.graphics.getHeight()];
        for (int i = 0; i < flowers.length; i++) {
            for (int j = 0; j < flowers[i].length; j++) {
                this.flowers[i][j] = new Flowers(i, j);
            }
        }
        //读取地图文件
        Properties properties = new Properties();
        properties.load(Gdx.files.internal(mapFilePath).reader());
        // 计算地图宽高（假设边界包含在内）
        int maxX = 0, maxY = 0;

        // 解析地图文件中的每一行
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("#")) continue; // 跳过注释行

            String[] coordinates = key.split(",");//key被用，分割成两个String
            //将String变成integer
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            //更新maxX和maxY
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }
        //create walls，根据地图最大坐标initialize
        this.walls = new Wall[maxY + 1][maxX + 1];
        //根据地图内容，initialize walls
        for (String key : properties.stringPropertyNames()) {//遍历所有keys
            if (key.startsWith("#")) continue;
            String[] coordinates = key.split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int type = Integer.parseInt(properties.getProperty(key));
            switch (type) {//根据type值，执行不同逻辑
                case 0: // indestructible walls
                    walls[y][x] = new Wall(x, y, false);
                    break;
                case 1: // 可破坏墙
                    walls[y][x] = new Wall(x, y, true);
                    break;
                case 2: // 入口
                    this.entrance = new Vector2(x, y);
                    this.player = new Player(this.world, entrance.x, entrance.y, this);//在player里完成了
                    break;
                case 3: // 敌人
                    // 初始化敌人列表并添加
                    Enemy enemy = new Enemy(world,x,y,this);
                    enemies.add(enemy);
                    break;
                case 4: // 出口
                    this.exit = new Exit(x, y);
                    walls[y][x] = new Wall(x, y, true); // 需要破坏墙才能获取
                    break;
                case 5: // 增加炸弹数量道具
                    powerUps.add(new PowerUp(x, y, PowerUpType.CONCURRENT_BOMBS));
                    walls[y][x] = new Wall(x, y, true); // 需要破坏墙才能获取
                    break;
                case 6: // 增加爆炸范围道具
                    powerUps.add(new PowerUp(x, y, PowerUpType.BLAST_RADIUS));
                    walls[y][x] = new Wall(x, y, true); // 需要破坏墙才能获取
                    break;
            }
        }

        // Create a player with initial position (1, 3)
        this.player = new Player(this.world, entrance.x, entrance.y, this);//入口位置

        // 如果没有出口，随机设置一个
        if (this.exit == null) {
            System.out.println("No Exit");
            addRandomExit();
        }
    }
    private void addRandomExit(){
        List<Vector2> destructibleWalls = new ArrayList<>();
        for (int y = 0; y < walls.length; y++) {
            for (int x = 0; x < walls[y].length; x++) {
                Wall wall = walls[y][x];
                if (wall != null && wall.isDestructible()) {
                    destructibleWalls.add(new Vector2(x, y));//destructible walls 加入Vector2 destructibleWalls列表
                }
            }
        }
        if (!destructibleWalls.isEmpty()) {
            Vector2 randomWall = destructibleWalls.get((int) (Math.random() * destructibleWalls.size()));
            this.exit = new Exit((int)randomWall.x, (int)randomWall.y);
        }
    }

    public boolean isReveal(Drawable d){
        return walls[(int)d.getY()][(int)d.getX()] == null;
    }

    /**
     * Updates the game state. This is called once per frame.
     * Every dynamic object in the game should update its state here.
     * @param frameTime the time that has passed since the last update
     */


    public void tick(float frameTime) {
        this.player.tick(frameTime);
        doPhysicsStep(frameTime);
        updateBombs(frameTime);
        updateEnemies(frameTime);
    }
    
    /**
     * Performs as many physics steps as necessary to catch up to the given frame time.
     * This will update the Box2D world by the given time step.
     * @param frameTime Time since last frame in seconds
     */
    private void doPhysicsStep(float frameTime) {
        this.physicsTime += frameTime;
        while (this.physicsTime >= TIME_STEP) {
            this.world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            this.physicsTime -= TIME_STEP;
        }
    }

    //检查出口是否被揭示
    public void revealExitIfNecessary(int x, int y){
        if(!exitRevealed && exit.getX() == x && exit.getY() ==y){
            exitRevealed = true;
        }
    }

    //检查指定位置是否可以通行
    public boolean isPassable(int x, int y){
        // 检查坐标是否在地图范围内
        if (x < 0 || y < 0 || y >= walls.length || x >= walls[0].length) {
            return false; // 超出地图范围不可通行
        }
        Wall wall = walls[y][x];
        if (wall == null) {
            return true; // 如果没有墙，可以通行
        }
        return wall.isDestructible() && wall.isDestroyed(); // 可破坏墙且已被摧毁则可通行
    }

    public boolean isCollision(Drawable a, Drawable b){
        float x1 = a.getX();
        float y1 = a.getY();
        float w1 = a.getWidth();
        float h1 = a.getHeight() ;

        float x2 = b.getX();
        float y2 = b.getY();
        float w2 = b.getWidth();
        float h2 = b.getHeight();


        Rectangle r1 = new Rectangle((int)(x1*64), (int)(y1*64)-(int)   64, (int)(w1 * 64),(int)(h1 * 64));
        Rectangle r2 = new Rectangle((int)(x2*64), (int)(y2*64)-(int)   64, (int)(w2 * 64),(int)(h2 * 64));

        System.out.println(r1 + " " + r2);
        return r1.intersects(r2);
    }

    //检查指定位置是否可以通行
    public boolean isPassablePlayer(float targetX, float targetY){
        for(Wall[] row : walls){
            for(Wall wall : row){
                if(wall != null && !wall.isDestroyed()){
                    Vector2 p1 = new Vector2(targetX, targetY);
                    Vector2 p2 = wall.getPosition();

                    Rectangle r1 = new Rectangle((int)(p1.x*64), (int)(p1.y*64-player.getHeight()*64), (int)(player.getWidth()*64),(int)(player.getHeight()*64));
                    Rectangle r2 = new Rectangle((int)(p2.x*64), (int)(p2.y*64)-64, 64,64);

                    if(r1.intersects(r2)){
                        //System.out.println(r1 + " " + r2);
                        return false;
                    }
                }
            }
        }

        return true;
    }


    /** Returns the player on the map. */
    public Player getPlayer() {
        return this.player;
    }
    
    /** Returns the chest on the map. */
    public Chest getChest() {
        return chest;
    }
    
    /** Returns the flowers on the map. */
    public List<Flowers> getFlowers() {
        return Arrays.stream(flowers).flatMap(Arrays::stream).toList();
    }

    public List<PowerUp> getPowerUps() {
        return new ArrayList<>(powerUps);
    }

    public Wall[][] getWalls() {
        return walls;
    }
    public Wall getWallAt(int x, int y) {
        // 检查坐标是否超出范围
        if (x < 0 || y < 0 || y >= walls.length || x >= walls[y].length) {
            return null; // 坐标超出范围
        }
        return walls[y][x]; // 返回对应位置的墙壁
    }

    public Wall getWallContains(int x, int y) {
        for(int i=0;i<walls.length;i++){
            for(int j=0;j<walls[i].length;j++){
                Wall wall = walls[i][j];
                if(i == 10 && j== 0){
                    //System.out.println(x + " " + y);
                    //System.out.println(wall.getX()*64 );
                    //System.out.println(wall.getX()*64 + wall.getWidth()*64 );
                    //System.out.println(wall.getY()*64 );
                    //System.out.println(wall.getY()*64 + wall.getHeight()*64);
                    //System.out.println(contains(wall, x, y));
                }
                if(wall != null && !wall.isDestroyed() && wall.isDestructible() && contains(wall, x, y)){
                    return wall;
                }
            }
        }
        return null;
    }

    public boolean contains(Drawable wall, float x, float y){
        return x >= wall.getX()*64 && x < wall.getX()*64 + wall.getWidth()*64 && y >= wall.getY()*64 && y < wall.getY()*64 + wall.getHeight()*64;
    }

    public void destroyWallAt(int x, int y) {
        walls[y][x] = null;//清除对象
    }

    public void destroyWall(Wall wall) {
        System.out.println("destroyWall: " + wall.getPosition());

        for(int i=0;i<walls.length;i++){
            for(int j=0;j<walls[i].length;j++){
                if(walls[i][j] == wall){
                    walls[i][j].destroy();
                    walls[i][j] = null;
                }
            }
        }
    }

    public int getWidth() {
        if (walls == null || walls.length == 0) return 0; // 防止空指针异常
        return walls[0].length; // 返回第一行的列数
    }
    public int getHeight() {
        if (walls == null) return 0; // 防止空指针异常
        return walls.length; // 返回总行数
    }

    public Vector2 getEntrance() {
        return entrance;
    }

    public Exit getExit() {
        return exit;
    }

    public boolean isExitRevealed() {
        return exitRevealed;
    }

    public World getWorld() {
        return world;
    }

    public BomberQuestGame getGame() {
        return game;
    }

    public float getPhysicsTime() {
        return physicsTime;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }


    // 判断是否有敌人
    public boolean hasEnemyAt(int x, int y) {
        // 遍历敌人列表并检查位置是否匹配
        for (Enemy enemy : enemies) {
            if (contains(enemy, x, y)) {
                return true;
            }
        }
        return false;
    }
    // 判断是否有敌人
    public Enemy getEnemyAt(int x, int y) {
        // 遍历敌人列表并检查位置是否匹配
        for (Enemy enemy : enemies) {
            if (contains(enemy, x, y)) {
                return enemy;
            }
        }
        return null;
    }

    //杀死某个位置的敌人
    public void killEnemyAt(int x, int y) {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.getX() == x && enemy.getY() == y) {
                iterator.remove(); // 从列表中移除敌人
                break;
            }
        }
    }
    public void killEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }
    public void updateEnemies(float delta) {
        for (Enemy enemy : enemies) {
            enemy.update(delta); // 假设 Enemy 类有 update 方法
        }
    }

    public void renderBombs(SpriteBatch spriteBatch) {
        for (Bomb bomb : bombs) {
            bomb.render(spriteBatch); // 调用每个炸弹的 render 方法
        }
    }

    public void addBomb(Bomb bomb){
        bombs.add(bomb);
        //setBombAt((int)bomb.getX(), (int)bomb.getY());
        System.out.println("Bomb added to map at: " + bomb.getX() + ", " + bomb.getY());
    }

    public void updateBombs(float delta) {
        Iterator<Bomb> iterator = bombs.iterator();
        while (iterator.hasNext()) {
            Bomb bomb = iterator.next();
            bomb.update(delta); // 更新炸弹状态

            if (bomb.isExploded()) {
                iterator.remove(); // 移除爆炸的炸弹
                getPlayer().bombExploded(); // 通知玩家释放一个炸弹位
            }
        }
    }

    // 检查某个位置是否有炸弹
    public boolean hasBombAt(int x, int y) {
        for(Bomb bomb: new ArrayList<>(bombs)){
            if(bomb.getX() == x && bomb.getY() == y){
                return true;
            }
        }

        return false;
    }



}
