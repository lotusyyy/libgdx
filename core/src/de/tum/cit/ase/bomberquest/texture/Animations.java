package de.tum.cit.ase.bomberquest.texture;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains all animation constants used in the game.
 * It is good practice to keep all textures and animations in constants to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Animations {
    
    /**
     * The animation for the character walking down.
     */
    //上下左右移动player时的动画：
    public static final Animation<TextureRegion> CHARACTER_WALK_DOWN = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(1, 1),
            SpriteSheet.CHARACTER.at(1, 2),
            SpriteSheet.CHARACTER.at(1, 3),
            SpriteSheet.CHARACTER.at(1, 4)
    );
   //新添加：这里的row和column根据”character“图像里来引用, player:
    public static final Animation<TextureRegion> CHARACTER_WALK_UP = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(3, 1),
            SpriteSheet.CHARACTER.at(3, 2),
            SpriteSheet.CHARACTER.at(3, 3),
            SpriteSheet.CHARACTER.at(3, 4));

    public static final Animation<TextureRegion> CHARACTER_WALK_LEFT = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(4, 1),
            SpriteSheet.CHARACTER.at(4, 2),
            SpriteSheet.CHARACTER.at(4, 3),
            SpriteSheet.CHARACTER.at(4, 4));

    public static final Animation<TextureRegion> CHARACTER_WALK_RIGHT = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(2, 1),
            SpriteSheet.CHARACTER.at(2, 2),
            SpriteSheet.CHARACTER.at(2, 3),
            SpriteSheet.CHARACTER.at(2, 4));

   //enemy:
   public static final Animation<TextureRegion> ENEMY_WALK_DOWN = new Animation<>(0.1f,
           SpriteSheet.ENEMY.at(5, 7),
           SpriteSheet.ENEMY.at(5, 8),
           SpriteSheet.ENEMY.at(5, 9)
   );

    public static final Animation<TextureRegion> ENEMY_WALK_UP = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(8, 7),
            SpriteSheet.ENEMY.at(8, 8),
            SpriteSheet.ENEMY.at(8, 9)
    );

    public static final Animation<TextureRegion> ENEMY_WALK_LEFT = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(6, 7),
            SpriteSheet.ENEMY.at(6, 8),
            SpriteSheet.ENEMY.at(6, 9)
    );

    public static final Animation<TextureRegion> ENEMY_WALK_RIGHT = new Animation<>(0.1f,
            SpriteSheet.ENEMY.at(7, 7),
            SpriteSheet.ENEMY.at(7, 8),
            SpriteSheet.ENEMY.at(7, 9)
    );
    public static final Animation<TextureRegion> BOMB_EXPLOSION = new Animation<>(0.1f,
            SpriteSheet.BOMB.at(5, 1),
            SpriteSheet.BOMB.at(5, 2),
            SpriteSheet.BOMB.at(5, 3),
            SpriteSheet.BOMB.at(5, 4)
    );


}
