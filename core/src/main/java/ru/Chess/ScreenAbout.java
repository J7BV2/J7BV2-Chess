package ru.Chess;

import static ru.Chess.Main.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

public class ScreenAbout implements Screen {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Vector3 touch;
    private final BitmapFont font70;
    private final BitmapFont font50;
    private final Main main;

    Texture imgBackGround;

    SunButton btnBack;
    private final String text = "Приветствую.\n" +
        "\n" +
        "Это игра - шахматы\n" +
        "здесь реализованы все\n" +
        "правила настоящих\n"+
        "шахмат.\n"+
        "\n"+
        "Приятной игры!";

    public ScreenAbout(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font70 = main.font70white;
        font50 = main.font50white;

        imgBackGround = new Texture("chess3.png");

        btnBack = new SunButton("Back", font70, 150);
    }

    @Override
    public void show() {Gdx.graphics.setForegroundFPS(10);}

    @Override
    public void render(float delta) {
        // касания
        if(Gdx.input.justTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if(btnBack.hit(touch.x, touch.y)){
                main.setScreen(main.screenMenu);
            }
        }
        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        font70.draw(batch, "ABOUT", 0, 1500, SCR_WIDTH, Align.center, false);
        font50.draw(batch, "Developer - J7BV2", 0, 1590);
        font70.draw(batch, text, 0, 1300, SCR_WIDTH, Align.center, false);
        btnBack.font.draw(batch, btnBack.text, btnBack.x, btnBack.y);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
