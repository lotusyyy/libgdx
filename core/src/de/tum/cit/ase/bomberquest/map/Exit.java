package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * Flowers are a static object without any special properties.
 * They do not have a hitbox, so the player does not collide with them.
 * They are purely decorative and serve as a nice floor decoration.
 */
public class Exit implements Drawable {

    private final int x;
    private final int y;
    private boolean unlocked;

    public Exit(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    @Override
    public float getWidth() {
        return 1;
    }

    @Override
    public float getHeight() {
        return 1;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        if(unlocked){
            return Textures.EXIT_UNLOCKED;
        }
        return Textures.EXIT;
    }

    public boolean isUnlocked() {
        return unlocked;
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
