package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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



    private Vector2 entrance;//Vector2是二维向量
    private Vector2 exit;
    private boolean exitRevealed = false;
    
    public GameMap(BomberQuestGame game, String mapFilePath) throws IOException {
        this.game = game;
        this.world = new World(Vector2.Zero, true);
        // Create a player with initial position (1, 3)
        this.player = new Player(this.world, 1, 3, this);//入口位置
        //this.entrance = new Vector2(1,3);//保存入口位置
        //this.exit = new Vector2(5,5);//暂定，需要destructive wall设置
        this.exitRevealed = false;
        // Create a chest in the middle of the map
        this.chest = new Chest(world, 3, 3);
        // Create flowers in a 7x7 grid
        this.flowers = new Flowers[7][7];
        for (int i = 0; i < flowers.length; i++) {
            for (int j = 0; j < flowers[i].length; j++) {
                this.flowers[i][j] = new Flowers(i, j);
            }
        }
        //读取地图文件
        Properties properties = new Properties();
        properties.load(new FileReader(mapFilePath));
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
                    this.player = new Player(this.world,x ,y ,this);
                    break;
                case 3: // 敌人
                    // 初始化敌人列表并添加
                    break;
                case 4: // 出口
                    this.exit = new Vector2(x, y);
                    break;
                case 5: // 增加炸弹数量道具
                case 6: // 增加爆炸范围道具
                    walls[y][x] = new Wall(x, y, true); // 需要破坏墙才能获取
                    break;
            }
        }

        // 如果没有出口，随机设置一个
        if (this.exit == null) {
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
            this.exit = randomWall; // 设置随机出口
        }
    }

    
    /**
     * Updates the game state. This is called once per frame.
     * Every dynamic object in the game should update its state here.
     * @param frameTime the time that has passed since the last update
     */
    public void tick(float frameTime) {
        this.player.tick(frameTime);
        doPhysicsStep(frameTime);
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
        if(!exitRevealed && exit.x == x && exit.y ==y){
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
    public int getWidth() {
        if (walls == null || walls.length == 0) return 0; // 防止空指针异常
        return walls[0].length; // 返回第一行的列数
    }
    public int getHeight() {
        if (walls == null) return 0; // 防止空指针异常
        return walls.length; // 返回总行数
    }

}
