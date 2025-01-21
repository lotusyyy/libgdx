package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.screen.VictoryAndGameOverScreen;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * Represents the player character in the game.
 * The player has a hitbox, so it can collide with other objects in the game.
 */
public class Player  extends  GameObject {
    
    /** Total time elapsed since the game started. We use this for calculating the player movement and animating it. */
    private float elapsedTime;
    
    /** The Box2D hitbox of the player, used for position and collision detection. */
    private Body hitbox;
    private final GameMap map;
    private int bombLimit = 1; //玩家初始放置炸弹量为1
    private int bombsPlaced = 0;//当前已放置的炸弹
    private boolean alive = true;
    private VictoryAndGameOverScreen victoryAndGameOverScreen;
    private boolean bombKeyPressed = false;//放置连续触发
    int explosionRadius = 1;

    //新添加两个变量
    float yVelocity = 0.0f;
    float xVelocity = 0.0f;
    World world;
    public Player(World world, float x, float y, GameMap map) {
        super(x, y);
        Vector2 entrance = map.getEntrance();

        this.world = world;
        this.hitbox = createHitbox(world, x, y);
        this.map = map;
    }
    
    /**
     * Creates a Box2D body for the player.
     * This is what the physics engine uses to move the player around and detect collisions with other bodies.
     * @param world The Box2D world to add the body to.
     * @param startX The initial X position.
     * @param startY The initial Y position.
     * @return The created body.
     */
    private Body createHitbox(World world, float startX, float startY) {
        // BodyDef is like a blueprint for the movement properties of the body.
        BodyDef bodyDef = new BodyDef();
        // Dynamic bodies are affected by forces and collisions.
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set the initial position of the body.
        bodyDef.position.set(startX, startY);
        // Create the body in the world using the body definition.
        Body body = world.createBody(bodyDef);
        // Now we need to give the body a shape so the physics engine knows how to collide with it.
        // We'll use a circle shape for the player.

        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f);
        body.createFixture(circle, 1.0f);

        //EdgeShape edgeShape = new EdgeShape();
        //edgeShape.set(startX, startY, startX + getWidth(), startY + getHeight());
        //body.createFixture(edgeShape, 1.0f);

        // We're done with the shape, so we should dispose of it to free up memory.
        //circle.dispose();
        // Set the player as the user data of the body so we can look up the player from the body later.
        body.setUserData(this);
        return body;
    }

    public Vector2 getPosition() {
        return hitbox.getPosition();
    }

    /**
     * Move the player around in a circle by updating the linear velocity of its hitbox every frame.
     * This doesn't actually move the player, but it tells the physics engine how the player should move next frame.
     * @param frameTime the time since the last frame.
     */
    public void tick(float frameTime) {//更新玩家状态，移动和动画
        this.elapsedTime += frameTime;
        float inputSpeed = 1.5f;

        // 重置速度
        xVelocity = 0;
        yVelocity = 0;

        // 更新速度基于键盘输入
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            xVelocity = -inputSpeed;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            xVelocity = inputSpeed;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            yVelocity = inputSpeed;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            yVelocity = -inputSpeed;
        }

        // 只有当至少有一个方向键被按下时才更新速度
        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) &&
                !Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            xVelocity = 0;
            yVelocity = 0;
        }

        //计算目标位置\
        frameTime = 0.2f;
        float targetX = this.getX() + xVelocity * frameTime;
        float targetY = this.getY() + yVelocity * frameTime;

        // 检查目标位置是否可通行
        if (map.isPassablePlayer(targetX, targetY)) {
            // 更新玩家的速度（允许移动）
            hitbox.setLinearVelocity(xVelocity, yVelocity);
        } else {
            // 如果目标位置不可通行，停止移动
            this.hitbox.setLinearVelocity(0, 0);
        }
    }

    //改动：
    @Override
    public TextureRegion getCurrentAppearance() {
        TextureRegion textureRegion = null;

        if (Math.abs(yVelocity) > Math.abs(xVelocity)) {
            if (yVelocity > 0) {
                textureRegion = Animations.CHARACTER_WALK_UP.getKeyFrame(this.elapsedTime, true);
            } else {
                textureRegion = Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, true);
            }
        } else {
            if (xVelocity > 0) {
                textureRegion = Animations.CHARACTER_WALK_RIGHT.getKeyFrame(this.elapsedTime, true);
            } else if (xVelocity < 0) {
                textureRegion = Animations.CHARACTER_WALK_LEFT.getKeyFrame(this.elapsedTime, true);
            } else {
                // Option for when the player is standing still
                textureRegion = Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, true);
            }
        }

        TextureRegion changed = new TextureRegion();
        changed.setRegion(textureRegion, 0, 6, 16, 20);

        return  changed;							
    }
    
    @Override
    public float getX() {
        // The x-coordinate of the player is the x-coordinate of the hitbox (this can change every frame).
        return hitbox.getPosition().x;
    }
    
    @Override
    public float getY() {
        // The y-coordinate of the player is the y-coordinate of the hitbox (this can change every frame).
        return hitbox.getPosition().y;
    }

    public int getBombLimit() {
        return bombLimit;
    }

    public int getBlastRadius() {
        return blastRadius;
    }

    public int getBombsPlaced() {
        return bombsPlaced;
    }

    public boolean isAlive() {
        return alive;
    }

    public void increaseBombLimit(){
        bombLimit ++;//这里没有设置炸弹最多能放几枚
    }

    public void placeBomb() {
       // if (Textures.BOMB == null) {
       //     throw new RuntimeException("Textures.BOMB is not loaded or is null.");
       // }

        try {
            int bombX = (int)  Math.floor(hitbox.getPosition().x);
            int bombY = (int) Math.floor (hitbox.getPosition().y);

            System.out.println("Attempting to place bomb at: " + bombX + ", " + bombY + ", bombsPlaced is " + bombsPlaced);

            if (bombsPlaced < bombLimit && !map.hasBombAt(bombX, bombY)) { // 检查是否超过炸弹限制,检查当前格子是否已有炸弹。
                // 创建炸弹实例
                //System.out.println("Conditions met, placing bomb...");
                Bomb bomb = new Bomb(
                        bombX, // 玩家当前的X坐标
                        bombY, // 玩家当前的Y坐标
                        Textures.BOMB, // 炸弹的纹理
                        //Gdx.audio.newSound(Gdx.files.internal("bomb_explosion.mp3")), // 爆炸音效
                        map, // 地图引用
                        blastRadius
                );

                // 将炸弹添加到地图中
                map.addBomb(bomb);

                // 增加当前放置的炸弹数量
                bombsPlaced++;
                System.out.println("Bomb placed successfully!");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    //移除炸弹（炸弹爆炸后调用）
    public void bombExploded(){
        if(bombsPlaced > 0){
            bombsPlaced--;
        }

        System.out.println("Attempting to remove bomb, bombsPlaced is " + bombsPlaced);
    }

    public void kill(){
        if(alive){
            alive = false;
            //victoryAndGameOverScreen.setWon(false);
        }
    }

    public void handleInput() {
        // 放置炸弹的按键（Space键）
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            System.out.println("SPACE key pressed!");
            placeBomb(); // 放置炸弹
        }
    }

    //PowerUp:
    private int blastRadius = 1;
    private int concurrentBombs = 1;

    public void increaseBlastRadius() { //该方法需要与bomb连接
        if (blastRadius < 8) blastRadius++;
    }

    public void increaseConcurrentBombs() { //该方法需要与bomb连接
        if (bombLimit < 8) bombLimit++;
    }


}
