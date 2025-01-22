package de.tum.cit.ase.bomberquest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.map.Bomb;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.screen.CountdownTimer;
import de.tum.cit.ase.bomberquest.screen.GameScreen;
import de.tum.cit.ase.bomberquest.screen.MenuScreen;
import de.tum.cit.ase.bomberquest.screen.VictoryAndGameOverScreen;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import static de.tum.cit.ase.bomberquest.screen.GameScreen.SCALE;
import static de.tum.cit.ase.bomberquest.screen.GameScreen.TILE_SIZE_PX;

/**
 * The BomberQuestGame class represents the core of the Bomber Quest game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class BomberQuestGame extends Game {

    /**
     * Sprite Batch for rendering game elements.
     * This eats a lot of memory, so we only want one of these.
     */
    private SpriteBatch spriteBatch;

    /** The game's UI skin. This is used to style the game's UI elements. */
    private Skin skin;
    
    /**
     * The file chooser for loading map files from the user's computer.
     * This will give you access to a {@link com.badlogic.gdx.files.FileHandle} object,
     * which you can use to read the contents of the map file as a String, and then parse it into a {@link GameMap}.
     */
    private final NativeFileChooser fileChooser;
    
    /**
     * The map. This is where all the game objects are stored.
     * This is owned by {@link BomberQuestGame} and not by {@link GameScreen}
     * because the map should not be destroyed if we temporarily switch to another screen.
     */
    private GameMap map;

    //新添加：
    private GameScreen currentGameScreen;

    public void continueGame() {
        if (currentGameScreen != null) {
            // 设置当前屏幕为游戏屏幕，恢复游戏
            setScreen(currentGameScreen);

            currentGameScreen.getTimer().setPause(false);

            MusicTrack.BACKGROUND_MENU.stop(); // Play some background music
            MusicTrack.BACKGROUND.play(); // Play some background music
        }
    }

    /**
     * Constructor for BomberQuestGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public BomberQuestGame(NativeFileChooser fileChooser) {
        this.fileChooser = fileChooser;


    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     * During the class constructor, libGDX is not fully initialized yet.
     * Therefore this method serves as a second constructor for the game,
     * and we can use libGDX resources here.
     */
    @Override
    public void create() {
        Bomb.victoryAndGameOverScreen = new VictoryAndGameOverScreen(this, true);

        this.spriteBatch = new SpriteBatch(); // Create SpriteBatch for rendering
        this.skin = new Skin(Gdx.files.internal("skin/craftacular/craftacular-ui.json")); // Load UI skin
        try {
            //默认加载第一个地图
            this.map = new GameMap(this, "maps/map-1.properties"); // Create a new game map (you should change this to load the map from a file instead)
        } catch (IOException e) {
            e.printStackTrace();
            Gdx.app.exit();//如果地图加载失败，退出游戏
        }
        BitmapFont font = new BitmapFont();

        goToMenu(); // Navigate to the menu screen
    }

    //加载指定地图，并切换到游戏界面
    public void loadMap(String mapFilePath) throws IOException {
        this.map = new GameMap(this, mapFilePath); // 加载地图
        this.currentGameScreen = new GameScreen(this, map); // 创建新的游戏屏幕
        setScreen(currentGameScreen); // 切换到新的游戏屏幕
    }

    public void loadNewMap() {
        System.out.println("loadNewMap"  );

        // Configure
        NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();

        // Starting from user's dir
        conf.directory = Gdx.files.local("maps");

        // Filter out all files which do not have the .ogg extension and are not of an audio MIME type - belt and braces
        conf.mimeFilter = "map/*";
        conf.nameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("properties");
            }
        };

        // Add a nice title
        conf.title = "Choose map file";

        fileChooser.chooseFile(conf, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle file) {
                try {
                    System.out.println("Selected file: " + file.path());
                    map = new GameMap(BomberQuestGame.this, file.path()); // Create a new game map (you should change this to load the map from a file instead)
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    Gdx.app.exit();//如果地图加载失败，退出游戏
                }

                currentGameScreen = new GameScreen(BomberQuestGame.this, map);
                setScreen(currentGameScreen); // Set the current screen to GameScreen

                MusicTrack.BACKGROUND_MENU.stop(); // Play some background music
                MusicTrack.BACKGROUND.play(); // Play some background music
            }

            @Override
            public void onCancellation() {

            }

            @Override
            public void onError(Exception exception) {

            }
        });
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen

        if(currentGameScreen != null)
        currentGameScreen.getTimer().setPause(true);

        MusicTrack.BACKGROUND.stop(); // Play some background music
        MusicTrack.BACKGROUND_MENU.play(); // Play some background music
    }

    /**
     * Switches to the game screen.
     */
    public void goToGame() {
        create();
        currentGameScreen = new GameScreen(this, this.map);
        this.setScreen(currentGameScreen); // Set the current screen to GameScreen

        MusicTrack.BACKGROUND_MENU.stop(); // Play some background music
        MusicTrack.BACKGROUND.play(); // Play some background music
    }

    //新添加:
    /**
     * Switches to the VictoryAndGameOver screen
     */
    public void goToVictoryAndGameOver(boolean won){
        this.setScreen(new VictoryAndGameOverScreen(this, won));

        currentGameScreen = null;

        if(won){
            MusicTrack.WIN.play();
            MusicTrack.BACKGROUND.stop();
        }else {
            MusicTrack.LOSE.play();
            MusicTrack.BACKGROUND.stop();
        }
    }

    /** Returns the skin for UI elements. */
    public Skin getSkin() {
        return skin;
    }

    /** Returns the main SpriteBatch for rendering. */
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
    
    /** Returns the current map, if there is one. */
    public GameMap getMap() {
        return map;
    }
    
    /**
     * Switches to the given screen and disposes of the previous screen.
     * @param screen the new screen
     */
    @Override
    public void setScreen(Screen screen) {
        Screen previousScreen = super.screen;
        super.setScreen(screen);
        if (previousScreen != null && previousScreen != screen) {
           // previousScreen.dispose();
        }
    }

    /** Cleans up resources when the game is disposed. */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
    }
}
