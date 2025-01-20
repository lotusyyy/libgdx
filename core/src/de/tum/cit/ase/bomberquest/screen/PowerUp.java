package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.map.Player;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

public class PowerUp implements Drawable{
    private final float x;
    private final float y;
    private PowerUpType type; //类型

    public PowerUp(float x, float y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    @Override
    public float getWidth() {
        return 1;
    }

    @Override
    public float getHeight() {
        return 1;
    }
    /**
     * Create a Box2D body for the chest.
     * @param world The Box2D world to add the body to.
     */

    //隐藏在wall下方时用staticBody
    private Body createHitbox(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        // 初始设置为StaticBody，等destructible wall被炸开后变成DynamicBody
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);
        PolygonShape box = new PolygonShape();
        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f); // 设置半径小于玩家的一半，确保玩家可以容易接触到PowerUp

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.0f; // 密度为0，避免影响玩家移动
        fixtureDef.isSensor = true; // 设置为传感器，仅用于检测碰撞，不会有物理反应

        body.createFixture(fixtureDef);
        circle.dispose(); // 释放圆形资源
        body.setUserData(this); // 将PowerUp对象与Body关联，方便在碰撞检测时获取

        return body;
    }

    //wall被炸开后与player交互时用dynamicBody

    public void makeDynamic(Body body) {
        // 更改body类型
        body.setType(BodyDef.BodyType.DynamicBody);
        // 必要时重新设置fixture属性
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setDensity(1.0f);
            fixture.setRestitution(0.5f); // 如果需要弹性
        }
        body.resetMassData(); // 更新质量数据以反映新的密度和其他属性
    }


    public void applyEffect(Player player) {
        switch (type) {
            case BLAST_RADIUS:
                player.increaseBlastRadius();
                break;
            case CONCURRENT_BOMBS:
                player.increaseConcurrentBombs();
                break;
        }
    }

    public PowerUpType getType() {
        return type;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        switch (this.type) {
            case BLAST_RADIUS:
                return Textures.INCREASEDRADIUS;
            case CONCURRENT_BOMBS:
                return Textures.ADDITIONALBOMB;
            default:
                return null; // or some default texture
        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}
