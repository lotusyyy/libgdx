package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

public class Wall implements Drawable {

    private  int x;
    private  int y;

    protected TextureRegion texture;
    protected boolean destructible;

    public Wall(int x, int y, boolean destructible) {
        this.x = x;
        this.y = y;
        this.destructible = destructible;
        this.texture = destructible ? Textures.DESTRUCTIBLE_WALL : Textures.INDESTRUCTIBLE_WALL;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return texture;
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
