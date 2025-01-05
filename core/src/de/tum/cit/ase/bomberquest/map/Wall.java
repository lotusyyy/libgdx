package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

public class Wall implements Drawable {

    private  int x;
    private  int y;

    protected TextureRegion texture;
    protected boolean destructible;

    public boolean isDestructible() {
        return destructible;
    }

    public void setDestructible(boolean destructible) {
        this.destructible = destructible;
    }

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

    //destructible walls are destructed
    public void destroy(){
        if(destructible){
            texture = null;//墙被摧毁后，从画面中消失
        }
    }
    //check whether the destructible wall is destroyed or not
    public boolean isDestroyed(){
        return this.destructible && this.texture == null;
    }

}
