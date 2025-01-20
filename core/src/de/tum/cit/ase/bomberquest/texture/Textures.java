package de.tum.cit.ase.bomberquest.texture;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains all texture constants used in the game.
 * It is good practice to keep all textures and animations in constants to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Textures {
    
    public static final TextureRegion FLOWERS = SpriteSheet.BASIC_TILES.at(2, 5);

    public static final TextureRegion CHEST = SpriteSheet.BASIC_TILES.at(5, 5);

    //新添加 walls
    public static final TextureRegion DESTRUCTIBLE_WALL = SpriteSheet.BASIC_TILES.at(1,2);
    public static final TextureRegion INDESTRUCTIBLE_WALL = SpriteSheet.BASIC_TILES.at(2,7);
    //bombs
    public static final TextureRegion EXPLOSION = SpriteSheet.BOMB.at(5,1);
    public static final TextureRegion BOMB = SpriteSheet.BOMB.at(4,2);

    //powerUp
    public static final TextureRegion INCREASEDRADIUS = SpriteSheet.Power_Up.at(4,5);
    public static final TextureRegion ADDITIONALBOMB = SpriteSheet.Power_Up.at(4,1);
}
