package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * Flowers are a static object without any special properties.
 * They do not have a hitbox, so the player does not collide with them.
 * They are purely decorative and serve as a nice floor decoration.
 */
public class Exit extends GameObject {

    private boolean unlocked;

    public Exit(int x, int y) {
        super(x, y);
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
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

}
