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

public class ScreenMenu implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Vector3 touch;
    private BitmapFont font;
    private Main main;
    Texture imgBackGround;

    SunButton btnPlay;
    SunButton btnSettings;
    SunButton btnAbout;
    SunButton btnExit;

    public ScreenMenu(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font = main.font70white;

        imgBackGround = new Texture("chess6.png");

        btnPlay = new SunButton("Play", font, 100, 1000);
        btnSettings = new SunButton("Settings", font, 100, 850);
        btnAbout = new SunButton("About", font, 100, 700);
        btnExit = new SunButton("Exit", font, 100, 550);
    }

    @Override
    public void show() {
        Gdx.graphics.setForegroundFPS(10);

    }

    @Override
    public void render(float delta) {
        // касания
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if (btnPlay.hit(touch.x, touch.y)) {
                main.setScreen(main.screenGameSettings);
            }
            if (btnSettings.hit(touch.x, touch.y)) {
                main.setScreen(main.screenGlobalSettings);
            }
        }
        if (btnAbout.hit(touch.x, touch.y)) {
            main.setScreen(main.screenAbout);
        }
        if (btnExit.hit(touch.x, touch.y)) {
            Gdx.app.exit();
        }
        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        font.draw(batch, "CHESS", 0, 1450, SCR_WIDTH, Align.center, false);
        btnPlay.font.draw(batch, btnPlay.text, btnPlay.x, btnPlay.y);
        btnSettings.font.draw(batch, btnSettings.text, btnSettings.x, btnSettings.y);
        btnAbout.font.draw(batch, btnAbout.text, btnAbout.x, btnAbout.y);
        btnExit.font.draw(batch, btnExit.text, btnExit.x, btnExit.y);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
