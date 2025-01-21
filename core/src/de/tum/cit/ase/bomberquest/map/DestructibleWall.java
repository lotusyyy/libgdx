package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

public class DestructibleWall extends Wall {

    protected boolean destroyed;

    public boolean isDestructible() {
        return true;
    }

    public DestructibleWall(int x, int y ) {
        super(x, y);
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        if(destroyed){
            return null;
        }
        return Textures.DESTRUCTIBLE_WALL;
    }

    //destructible walls are destructed
    public void destroy(){
        destroyed = true;
    }
    //check whether the destructible wall is destroyed or not
    public boolean isDestroyed(){
        return destroyed;
    }

}
