package ru.Chess;

import static ru.Chess.Main.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

public class ScreenGlobalSettings implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Vector3 touch;
    private BitmapFont font70white, font70gray;
    private BitmapFont font50white;
    private Main main;
    private InputKeyboard keyboard;
    Texture imgBackGround;

    SunButton btnName;
    SunButton btnSound;
    SunButton btnBack;

    public ScreenGlobalSettings(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font70white = main.font70white;
        font70gray = main.font70gray;
        font50white = main.font50white;
        keyboard = new InputKeyboard(font50white, SCR_WIDTH, SCR_HEIGHT/2, 7);

        imgBackGround = new Texture("space3.png");

        loadSettings();
        btnName = new SunButton("Name: "+main.player.name, font70white, 100, 1200);
        btnSound = new SunButton(isSoundOn ? "Sound ON" : "Sound OFF", font70white, 100, 1050);
        btnBack = new SunButton("Back", font70white, 150);
    }

    @Override
    public void show() {Gdx.graphics.setForegroundFPS(10);}

    @Override
    public void render(float delta) {
        // касания
        if(Gdx.input.justTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if(keyboard.isKeyboardShow) {
                if (keyboard.touch(touch)) {
                    main.player.name = keyboard.getText();
                    btnName.setText("Name: "+main.player.name);
                }
            } else {
                if (btnName.hit(touch)) {
                    keyboard.start();
                }
                if (btnSound.hit(touch)) {
                    isSoundOn = !isSoundOn;
                    btnSound.setText(isSoundOn ? "Sound ON" : "Sound OFF");
                }
                if (btnBack.hit(touch)) {
                    main.setScreen(main.screenMenu);
                }
            }
        }
        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        font70white.draw(batch, "SETTINGS", 0, 1500, SCR_WIDTH, Align.center, false);
        btnName.font.draw(batch, btnName.text, btnName.x, btnName.y);
        btnSound.font.draw(batch, btnSound.text, btnSound.x, btnSound.y);
        btnBack.font.draw(batch, btnBack.text, btnBack.x, btnBack.y);
        keyboard.draw(batch);
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
    public void dispose() {keyboard.dispose();}

    private void saveSettings(){
        Preferences prefs = Gdx.app.getPreferences("ChessSettings");
        prefs.putString("name", main.player.name);
        prefs.putBoolean("sound", isSoundOn);
        prefs.flush();
    }

    private void loadSettings(){
        Preferences prefs = Gdx.app.getPreferences("ChessSettings");
        main.player.name = prefs.getString("name", "Noname");
        isSoundOn = prefs.getBoolean("sound", true);
    }
}
