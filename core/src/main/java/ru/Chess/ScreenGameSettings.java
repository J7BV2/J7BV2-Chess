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

public class ScreenGameSettings implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Vector3 touch;
    private BitmapFont font70white, font70gray;
    private BitmapFont font50white;
    private InputKeyboard keyboard;
    private Main main;
    public String time = timer/60+ " minutes";
    Texture imgBackGround;

    SunButton btnVariant;
    SunButton btnClassic;
    SunButton btnChess960;
    SunButton btnTimer;
    SunButton btnPlay;
    SunButton btnEnemy;
    SunButton btnBack;

    public ScreenGameSettings(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font70white = main.font70white;
        font70gray = main.font70gray;
        font50white = main.font50white;
        keyboard = new InputKeyboard(font50white, SCR_WIDTH, SCR_HEIGHT/2, 7);

        imgBackGround = new Texture("chess2.png");

        btnVariant = new SunButton("Variant", font70white, 100, 1200);
        btnClassic = new SunButton("Classic", font70white, 200, 1100);
        btnChess960 = new SunButton("Chess960", font70white, 200, 1000);
        setFontColorByVariants();
        btnTimer = new SunButton("Timer: "+ time, font70white, 100, 850);
        btnEnemy = new SunButton("Enemy: "+main.player.enemy, font70white, 100, 750);
        btnPlay = new SunButton ("Play", font70white, 350);
        btnBack = new SunButton("Back", font70white, 150);
    }
    @Override
    public void show() {
        Gdx.graphics.setForegroundFPS(10);
    }

    @Override
    public void render(float delta) {
        // касания
        if(Gdx.input.justTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            if(keyboard.isKeyboardShow) {
                if (keyboard.touch(touch)) {
                    main.player.enemy = keyboard.getText();
                    btnEnemy.setText("Enemy: "+main.player.enemy);
                }
            } else {
                if (btnEnemy.hit(touch)) {
                    keyboard.start();
                }
                if (btnClassic.hit(touch)) {
                    variants = CLASSIC;
                    setFontColorByVariants();
                }
                if (btnChess960.hit(touch)) {
                    variants = CHESS960;
                    setFontColorByVariants();
                }
                if (btnTimer.hit(touch)) {
                    if(timer == ONE_MIN) timer = THREE_MIN;
                    else if(timer == THREE_MIN) timer = FIVE_MIN;
                    else if(timer == FIVE_MIN) timer = TEN_MIN;
                    else if(timer == TEN_MIN) timer = THIRTY_MIN;
                    else if(timer == THIRTY_MIN) timer = ONE_MIN;
                    time = timer /60+" minutes";
                    btnTimer.setText("Timer: "+ time);
                }
                if (btnPlay.hit(touch)) {
                    ScreenGame.currentPlayer = PieceColor.WHITE;
                    main.setScreen(main.screenGame);
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
        font70white.draw(batch, "GAME SETTINGS", 0, 1500, SCR_WIDTH, Align.center, false);
        btnEnemy.font.draw(batch, btnEnemy.text, btnEnemy.x, btnEnemy.y);
        btnVariant.font.draw(batch, btnVariant.text, btnVariant.x, btnVariant.y);
        btnClassic.font.draw(batch, btnClassic.text, btnClassic.x, btnClassic.y);
        btnChess960.font.draw(batch, btnChess960.text, btnChess960.x, btnChess960.y);
        btnTimer.font.draw(batch, btnTimer.text, btnTimer.x, btnTimer.y);
        btnPlay.font.draw(batch, btnPlay.text, btnPlay.x, btnPlay.y);
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
    public void dispose() {}

    private void setFontColorByVariants(){
        btnClassic.setFont(variants == CLASSIC ? font70white : font70gray);
        btnChess960.setFont(variants == CHESS960 ? font70white : font70gray);
    }
}
