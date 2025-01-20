package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.screen.VictoryAndGameOverScreen;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

public class Bomb implements Drawable {
    private final float x;
    private final float y;
    private int explosionRadius = 1;
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

    public Bomb(float x, float y, TextureRegion bombTexture, GameMap map, int explosionRadius) {
        this.x = x;
        this.y = y;
        this.map = map;
        this.bombTexture = Textures.BOMB;
        //this.explosionSound = Gdx.audio.newSound(Gdx.files.internal("bomb_explosion.mp3"));
        this.explosionRadius = explosionRadius;
        this.exploded = false;
    }

    public void render(SpriteBatch spriteBatch) {
        if (exploded) {
            TextureRegion frame = explosionAnimation.getKeyFrame(explosionDuration - explosionTimer, false);
            spriteBatch.draw(frame, x, y, 1, 1); // 根据炸弹大小设置宽高
        } else {
            spriteBatch.draw(bombTexture, x, y, 1, 1); // 炸弹图像
        }
    }

    public void update(float delta) {
        if(!exploded){
            bombTimer -= delta;
            if(bombTimer <= 0.0f){
                explode();
            }
        }else{
            explosionTimer -= delta;
            if(explosionTimer <= 0.0f){
                setExploded(true);
            }
        }
    }

    private void explode(){
        if(!exploded){
            this.exploded = true;
            playExplosionAniation();
            generateBlast();//触发爆炸波
            setExploded(true);
           // explosionSound.play();
        }
    }

    private void playExplosionAniation(){
        explosionAnimation = Animations.BOMB_EXPLOSION;
        explosionTimer = explosionDuration;
    }

    private void generateBlast(){
        for(Direction direction : Direction.values()){
            propagateBlast(direction);
        }
    }

    private void propagateBlast(Direction direction){
        for(int i = 0; i <= explosionRadius; i++){
            int targetX = (int) (x + direction.getOffsetX()* i);
            int targetY = (int) (y + direction.getOffsetY()* i);
            System.out.println("Target: " + targetX + " " + targetY);

            //边界和indestructible wall不能被炸
            if (!map.isPassablePlayer(targetX, targetY)) {
               // break; // 超出范围，停止传播
            }

            // 检查并摧毁可摧毁墙
            Wall wall = map.getWallContains(targetX*64, targetY*64);

            if (wall != null) {
                if (wall.isDestructible() && !wall.isDestroyed()) {
                    map.destroyWall (wall); // 移除墙
                }
                break; // 墙阻挡了爆炸波
            }

            //检查是否有玩家
            map.getPlayer();//玩家被炸死
            if(map.isCollision(map.getPlayer(),this)) {
                map.getPlayer().kill();//玩家死亡
                map.getGame().goToVictoryAndGameOver(false);
            }

            //检查是否有敌人
            Enemy enemy = map.getEnemyAt(targetX*64, targetY*64);
            if(enemy != null){
                map.killEnemy(enemy);
            }
        }
    }


    @Override
    public TextureRegion getCurrentAppearance() {
        return exploded ? Textures.EXPLOSION : Textures.BOMB;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {return y;}

    @Override
    public float getWidth() {
        return 1;
    }

    @Override
    public float getHeight() {
        return 1;
    }

    public boolean isExploded() {
        return exploded;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }
}
