package ru.Chess;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public static final float SCR_WIDTH = 896;
    public static final float SCR_HEIGHT = 1600;
    public static int THREE_MIN = 180, FIVE_MIN = 300, ONE_MIN =60, TEN_MIN= 600, THIRTY_MIN = 1800;
    public static final int CLASSIC = 0, CHESS960 = 1;
    public static int variants = CLASSIC;
    public static boolean isSoundOn = true;
    public static int timer = ONE_MIN;

    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Vector3 touch;
    public BitmapFont font70white;
    public BitmapFont font70gray;
    public BitmapFont font50white;

    Player player;
    ScreenMenu screenMenu;
    ScreenGame screenGame;
    ScreenGameSettings screenGameSettings;
    ScreenGlobalSettings screenGlobalSettings;
    ScreenAbout screenAbout;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
        touch = new Vector3();
        font70white = new BitmapFont(Gdx.files.internal("fnt/roboto70white.fnt"));
        font70gray = new BitmapFont(Gdx.files.internal("fnt/roboto70gray.fnt"));
        font50white = new BitmapFont(Gdx.files.internal("fnt/roboto50white.fnt"));

        player = new Player();
        screenMenu = new ScreenMenu(this);
        screenGame = new ScreenGame(this);
        screenGlobalSettings = new ScreenGlobalSettings(this);
        screenGameSettings = new ScreenGameSettings(this);
        screenAbout = new ScreenAbout(this);
        setScreen(screenMenu);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font70white.dispose();
        font70gray.dispose();
        font50white.dispose();
    }
}
