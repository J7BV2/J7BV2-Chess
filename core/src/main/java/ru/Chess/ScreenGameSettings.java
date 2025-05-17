package ru.Chess;

import static ru.Chess.Main.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private Main main;

    Texture imgBackGround;

    SunButton btnVariant;
    SunButton btnClassic;
    SunButton btnChess960;
    SunButton btnEnemy;
    SunButton btnPlay;
    SunButton btnBack;

    public ScreenGameSettings(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font70white = main.font70white;
        font70gray = main.font70gray;
        font50white = main.font50white;

        imgBackGround = new Texture("space3.png");

        btnVariant = new SunButton("Variant", font70white, 100, 1200);
        btnClassic = new SunButton("Classic", font70white, 200, 1100);
        btnChess960 = new SunButton("Chess960", font70white, 200, 1000);
        setFontColorByVariants();
        btnEnemy = new SunButton(humanEnemy ? "Enemy: Human" : "Enemy: Bot", font70white, 100, 850);
        btnPlay = new SunButton ("Play", font70white, 350);
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

            if (btnClassic.hit(touch)) {
                variants = CLASSIC;
                setFontColorByVariants();
            }
            if (btnChess960.hit(touch)) {
                variants = CHESS960;
                setFontColorByVariants();
            }
            if (btnEnemy.hit(touch)) {
                humanEnemy = !humanEnemy;
                btnEnemy.setText(humanEnemy ? "Enemy: Human" : "Enemy: Bot");
            }
            if (btnPlay.hit(touch)) {
                main.setScreen(main.screenGame);
            }
            if (btnBack.hit(touch)) {
                main.setScreen(main.screenMenu);
            }
        }
        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        font70white.draw(batch, "GAME SETTINGS", 0, 1500, SCR_WIDTH, Align.center, false);
        btnVariant.font.draw(batch, btnVariant.text, btnVariant.x, btnVariant.y);
        btnClassic.font.draw(batch, btnClassic.text, btnClassic.x, btnClassic.y);
        btnChess960.font.draw(batch, btnChess960.text, btnChess960.x, btnChess960.y);
        btnEnemy.font.draw(batch, btnEnemy.text, btnEnemy.x, btnEnemy.y);
        btnPlay.font.draw(batch, btnPlay.text, btnPlay.x, btnPlay.y);
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

    private void setFontColorByVariants(){
        btnClassic.setFont(variants == CLASSIC ? font70white : font70gray);
        btnChess960.setFont(variants == CHESS960 ? font70white : font70gray);
    }
}
