package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

public abstract class Wall extends GameObject{

    public abstract boolean isDestructible();

    public Wall(int x, int y) {
        super(x, y);
    }

    //destructible walls are destructed
    public abstract void destroy();

    //check whether the destructible wall is destroyed or not
    public abstract boolean isDestroyed();
}
