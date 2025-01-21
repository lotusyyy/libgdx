package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.tum.cit.ase.bomberquest.map.GameMap;

import static com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable.draw;

/**
 * A Heads-Up Display (HUD) that displays information on the screen.
 * It uses a separate camera so that it is always fixed on the screen.
 */
public class Hud {
    
    /** The SpriteBatch used to draw the HUD. This is the same as the one used in the GameScreen. */
    private SpriteBatch spriteBatch;
    /** The font used to draw text on the screen. */
    private BitmapFont font;
    /** The camera used to render the HUD. */
    private OrthographicCamera camera;
    private CountdownTimer timer;
    private GameMap map;

    public Hud(SpriteBatch spriteBatch, BitmapFont font, CountdownTimer timer, GameMap map) {
        this.map = map;
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.timer = timer;
    }


    public Hud() {
    }

    /**
     * Renders the HUD on the screen.
     * This uses a different OrthographicCamera so that the HUD is always fixed on the screen.
     */
    public void render(float playerX, float playerY) {
        // Render from the camera's perspective
        spriteBatch.setProjectionMatrix(camera.combined);

        float hudX = playerX;
        float hudY = playerY + 30;
        // 转换到相对于摄像机的坐标
        hudX -= camera.position.x - camera.viewportWidth / 2;
        hudY -= camera.position.y - camera.viewportHeight / 2;

        // Start drawing
        spriteBatch.begin();

        // Draw the HUD elements
        font.draw(spriteBatch, "Press Esc to Pause!", 10, Gdx.graphics.getHeight() - 10);
        //draw 剩余时间
        font.draw(spriteBatch, "Time left: " + (int)timer.getTimeLeft() + " second(s)", 10, Gdx.graphics.getHeight() - 30);

        int x = 10;
        int y = Gdx.graphics.getHeight() - 50;

        font.draw(spriteBatch, "Bomb blast radius: " + map.getPlayer().getBlastRadius(), x, y);
        y -= 20;
        font.draw(spriteBatch, "Concurrent bomb limit: " + map.getPlayer().getBombLimit(), x, y);
        y -= 20;
        font.draw(spriteBatch, "Remaining enemies: " + map.getEnemies().size(), x, y);
        y -= 20;
        font.draw(spriteBatch, "Exit unlocked: " + (map.getEnemies().size() == 0?"Yes":"No"), x, y);

        // Finish drawing
        spriteBatch.end();
    }

    public void dispose(){
        font.dispose();
    }
    /**
     * Resizes the HUD when the screen size changes.
     * This is called when the window is resized.
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }
    
}
