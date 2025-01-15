package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

import static com.badlogic.gdx.Input.Keys.*;

/**
 * Represents the player character in the game.
 * The player has a hitbox, so it can collide with other objects in the game.
 */
public class Player implements Drawable {
    
    /** Total time elapsed since the game started. We use this for calculating the player movement and animating it. */
    private float elapsedTime;
    
    /** The Box2D hitbox of the player, used for position and collision detection. */
    private final Body hitbox;
    private final GameMap map;

    //新添加两个变量
    float yVelocity = 0.0f;
    float xVelocity = 0.0f;

    public Player(World world, float x, float y, GameMap map) {

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
        // Give the circle a radius of 0.3 tiles (the player is 0.6 tiles wide).
        circle.setRadius(0.3f);
        // Attach the shape to the body as a fixture.
        // Bodies can have multiple fixtures, but we only need one for the player.
        body.createFixture(circle, 1.0f);
        // We're done with the shape, so we should dispose of it to free up memory.
        circle.dispose();
        // Set the player as the user data of the body so we can look up the player from the body later.
        body.setUserData(this);
        return body;
    }
    
    /**
     * Move the player around in a circle by updating the linear velocity of its hitbox every frame.
     * This doesn't actually move the player, but it tells the physics engine how the player should move next frame.
     * @param frameTime the time since the last frame.
     */
    public void tick(float frameTime) {//更新玩家状态，移动和动画
        this.elapsedTime += frameTime;
        float inputSpeed = 2.0f;

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

        //计算目标位置
        float targetX = this.getX() + xVelocity * frameTime;
        float targetY = this.getY() + yVelocity * frameTime;
        // 检查目标位置是否可通行
        if (map.isPassable((int) Math.floor(targetX), (int) Math.floor(targetY))) {
            // 更新玩家的速度（允许移动）
            this.hitbox.setLinearVelocity(xVelocity, yVelocity);
        } else {
            // 如果目标位置不可通行，停止移动
            this.hitbox.setLinearVelocity(0, 0);
        }

        TextureRegion textureRegion = getCurrentAppearance();
        System.out.println(hitbox.getPosition() + " " + hitbox.getFixtureList());
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
}
