package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

public class VictoryAndGameOverScreen implements Screen{
    private BomberQuestGame game;
    private Stage stage;
    private boolean won; // 声明一个私有布尔变量 won，表示玩家是否赢得了游戏。

    public VictoryAndGameOverScreen(BomberQuestGame game, boolean won) {
        this.game = game;
        this.won = won;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport()); // 初始化 stage 对象，使用一个新的 ScreenViewport。
        Gdx.input.setInputProcessor(stage); // 设置输入处理器为 stage，使 stage 可以接收用户输入。

        Table table = new Table(); // 创建一个新的 Table 对象，用于布局屏幕上的元素。
        table.setFillParent(true); // 设置 table 填充整个舞台。
        stage.addActor(table);  // 将 table 添加到舞台。

        Label.LabelStyle labelStyle = new Label.LabelStyle(); // 创建一个新的 LabelStyle 对象，用于自定义文本样式。
        labelStyle.font = new BitmapFont();

        String message = won ? "Victory! You won the game!" : "Game Over! You lost!";
        Label messageLabel = new Label(message, labelStyle); // 创建一个 Label 对象，显示游戏结果消息。
        table.add(messageLabel).pad(20);

        TextButton returnButton = new TextButton("Return to Main Menu", game.getSkin()); // 创建一个 TextButton 对象，显示“返回主菜单”的文本。
        table.row(); // 在 table 中添加一个新行。
        table.add(returnButton).pad(20);

        returnButton.addListener(new ChangeListener() { // 给返回按钮添加一个监听器。
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new MenuScreen(game)); // 当按钮被点击时，更改屏幕到主菜单。
            }
        });
    }

    @Override
    public void render(float delta) { // 实现 Screen 接口的 render 方法，用于绘制屏幕。
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // 更新舞台动作。
        stage.draw();  // 绘制舞台。
    }


    @Override
    public void resize(int width, int height) { // 实现 Screen 接口的 resize 方法，用于处理屏幕尺寸变化。
        stage.getViewport().update(width, height, true); // 更新舞台视图端口的尺寸。
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void dispose() {// 实现 Screen 接口的 hide 方法，用于当屏幕隐藏时。
        stage.dispose(); // 释放舞台资源。
    }

    public BomberQuestGame getGame() {
        return game;
    }

    //getter and setter
    public Stage getStage() {
        return stage;
    }

    public boolean isWon() {
        return won;
    }

    public void setGame(BomberQuestGame game) {
        this.game = game;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setWon(boolean won) {
        this.won = won;
        game.goToVictoryAndGameOver(won);
    }
}
