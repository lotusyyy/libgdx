package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;


public class Enemy extends GameObject {

    private Body hitbox;
    private GameMap map;
    private float speed = 1f;  // 可调整
    private boolean alive = true;
    private Random random = new Random(); //用于生成随机数来控制敌人的随机移动。
    private Animation<TextureRegion> currentAnimation;
    private float stateTime = 0;
    private Direction currentDirection = Direction.DOWN;  // 默认方向为向下
    private Vector2 velocity = new Vector2();

    public Enemy(World world, float x, float y, GameMap map) {
        super(x, y);
        this.map = map;
        this.hitbox = createHitbox(world, x, y); //方法用于在指定位置创建敌人的物理体。
        randomVelocity();
    }

    private Body createHitbox(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f);
        body.createFixture(circle, 1.0f); //定义了物理体的物理属性，如密度、摩擦力和恢复力（弹性）。
        circle.dispose();
        body.setUserData(this);
        return body;
    }

    //敌人随机移动：
    public void update(float deltaTime) {
        if (!alive) return;

        stateTime += deltaTime;

        float targetX = getX() + velocity.x;
        float targetY = getY() + velocity.y;

        if(map.isPassableEnemy(this, targetX, targetY)) {
            // 应用速度向量更新物理体位置
            hitbox.setLinearVelocity(velocity);
        }else {
            randomVelocity();
        }
    }

    public void randomVelocity(){
        // 随机选择新的移动方向
        int moveDirection = random.nextInt(4);
        updateDirection(moveDirection);

        // 更新速度向量和动画
        switch (currentDirection) {
            case DOWN:
                velocity.set(0, -speed);
                currentAnimation = Animations.ENEMY_WALK_DOWN;
                break;
            case UP:
                velocity.set(0, speed);
                currentAnimation = Animations.ENEMY_WALK_UP;
                break;
            case LEFT:
                velocity.set(-speed, 0);
                currentAnimation = Animations.ENEMY_WALK_LEFT;
                break;
            case RIGHT:
                velocity.set(speed, 0);
                currentAnimation = Animations.ENEMY_WALK_RIGHT;
                break;
        }
    }

    private void updateDirection(int moveDirection) {
        // 将随机整数映射到枚举值
        switch (moveDirection) {
            case 0:
                currentDirection = Direction.DOWN;
                break;
            case 1:
                currentDirection = Direction.RIGHT;
                break;
            case 2:
                currentDirection = Direction.UP;
                break;
            case 3:
                currentDirection = Direction.LEFT;
                break;
        }
    }


    public boolean isAlive() {
        return alive;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return currentAnimation.getKeyFrame(stateTime, true);
    }

    @Override
    public float getX() {
        return  hitbox.getPosition().x;
    }

    @Override
    public float getY() {
        return hitbox.getPosition().y;
    }
}
