package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.screen.VictoryAndGameOverScreen;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Textures;

import java.util.HashMap;
import java.util.Map;

public class Bomb extends GameObject {

    private float bombTimer = 3.0f;
    private final float explosionDuration = 0.5f;
    private float explosionTimer;
    private Animation<TextureRegion> explosionAnimation;
    private boolean exploded;
    private final TextureRegion bombTexture;
    //private final Sound explosionSound;
    private final GameMap map;
    public static VictoryAndGameOverScreen victoryAndGameOverScreen;
    private Player player;

    public float getExplosionTimer() {
        return bombTimer;
    }

    public GameMap getMap() {
        return map;
    }

    public Bomb(float x, float y, TextureRegion bombTexture, GameMap map, int explosionRadius) {
        super(x, y);
        this.map = map;
        this.bombTexture = Textures.BOMB;
        //this.explosionSound = Gdx.audio.newSound(Gdx.files.internal("bomb_explosion.mp3"));

        this.exploded = false;
        player = map.getPlayer();
    }

    public void render(SpriteBatch spriteBatch) {
        float x = getX();
        float y = getY();

        if (bombTimer < 0.8f) {
            TextureRegion frame = explosionAnimation.getKeyFrame(explosionDuration - explosionTimer, false);
            spriteBatch.draw(frame, x - 16 * 2.5f, y, 1, 1); // 根据炸弹大小设置宽高
        } else {
            spriteBatch.draw(bombTexture, x, y, 1, 1); // 炸弹图像
        }
    }

    public void update(float delta) {
        stateTime += delta;

        if (!exploded) {
            bombTimer -= delta;
            if (bombTimer <= 0.0f) {
                explode();
            }
        } else {
            explosionTimer -= delta;
            if (explosionTimer <= 0.0f) {
                setExploded(true);
            }
        }
    }

    private void explode() {
        if (!exploded) {

            MusicTrack.EXPLODE.play();

            this.exploded = true;
            playExplosionAniation();
            generateBlast();//触发爆炸波
            setExploded(true);
            // explosionSound.play();
        }
    }

    private void playExplosionAniation() {
        explosionAnimation = Animations.BOMB_EXPLOSION;
        explosionTimer = explosionDuration;
    }

    private void generateBlast() {
        for (Direction direction : Direction.values()) {
            propagateBlast(direction);
        }
    }

    public Map<Direction, Integer> getBlastRadius() {
        Map<Direction, Integer> directionIntegerMap = new HashMap<>();

        for (Direction direction : Direction.values()) {
            directionIntegerMap.put(direction, propagateBlast2(direction));
        }

        return directionIntegerMap;
    }

    private int propagateBlast(Direction direction) {
        float x = getX();
        float y = getY();

        for (int i = 0; i <= player.getBlastRadius(); i++) {
            int targetX = (int) (x + direction.getOffsetX() * i);
            int targetY = (int) (y + direction.getOffsetY() * i);
            //System.out.println("Target: " + targetX + " " + targetY);

            //边界和indestructible wall不能被炸
            if (!map.isPassablePlayer(targetX, targetY)) {
                //break; // 超出范围，停止传播
            }

            // 检查并摧毁可摧毁墙
            Wall wall = map.getWallContains(targetX * 64, targetY * 64);

            if (wall != null) {
                if (!wall.isDestructible()) {
                    return i;
                    //break; // 墙阻挡了爆炸波
                }

                if (wall.isDestructible() && !wall.isDestroyed()) {
                    map.destroyWall(wall); // 移除墙
                }
            }

            //检查是否有玩家
            map.getPlayer();//玩家被炸死
            if (map.isCollision(map.getPlayer(), new Bomb(targetX, targetY, Textures.BOMB, map, 1))) {
                map.getPlayer().kill();//玩家死亡
                map.getGame().goToVictoryAndGameOver(false);
            }

            //检查是否有敌人
            Enemy enemy = map.getEnemyAt2(targetX, targetY);
            if (enemy != null) {
                map.killEnemy(enemy);
            }
        }

        return player.getBlastRadius();
    }

    private int propagateBlast2(Direction direction) {
        float x = getX();
        float y = getY();

        for (int i = 0; i <= player.getBlastRadius(); i++) {
            int targetX = (int) (x + direction.getOffsetX() * i);
            int targetY = (int) (y + direction.getOffsetY() * i);
            //System.out.println("Target: " + targetX + " " + targetY);

            //边界和indestructible wall不能被炸
            if (!map.isPassablePlayer(targetX, targetY)) {
                //break; // 超出范围，停止传播
            }

            // 检查并摧毁可摧毁墙
            Wall wall = map.getWallContains(targetX * 64, targetY * 64);

            if (wall != null) {
                if (!wall.isDestructible()) {
                    return i-1;
                    //break; // 墙阻挡了爆炸波
                }
            }
        }

        return player.getBlastRadius();
    }


    private float stateTime = 0;

    @Override
    public TextureRegion getCurrentAppearance() {
        return bombTimer < 0.8f ? Animations.BOMB_EXPLOSION.getKeyFrame(stateTime, true) : Animations.BOMB_DISPLAY.getKeyFrame(stateTime, true);
    }

    public boolean isExploded() {
        return exploded;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }
}
