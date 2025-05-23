package ru.Chess;

import static ru.Chess.Main.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

public class ScreenGame implements Screen {
    private final SpriteBatch batch;
    private final Vector3 touch;
    private final OrthographicCamera camera;
    private final ChessBoard board;
    private final BitmapFont font70;
    private Texture[] whitePieces;
    private Texture[] blackPieces;
    private Texture lightTile, darkTile;
    private final Main main;

    public static final int GAME_ON = 0, CHECKMATE = 1, STALEMATE = 2, TIME_IS_OVER = 3;
    public static int gameOver = GAME_ON;
    public static PieceColor currentPlayer = PieceColor.WHITE;

    private static final int TO_WHITE = 0, TO_BLACK = 180;
    private float whiteTime = timer;
    private float blackTime = timer;
    private float timeElapsed = 0;
    private boolean isWhiteTurn = true;

    private final Texture imgBackGround;

    private final int tileSize;
    private final int boardOffsetX;
    private final int boardOffsetY;

    private Piece selectedPiece = null;
    private int selectedX = -1, selectedY = -1;
    private int flipPosition;

    private String textForCheckmate;
    private String textForStalemate;

    Sound sndCheckmate;
    Sound sndCheck;
    Sound sndStalemate;

    SunButton btnBack;

    public ScreenGame(Main main) {
        this.main = main;
        touch = main.touch;
        batch = main.batch;
        camera = main.camera;
        font70 = main.font70white;

        imgBackGround = new Texture("chess9.png");

        tileSize = (int) (SCR_WIDTH / 8);
        boardOffsetX = 0;
        boardOffsetY = (int) ((SCR_HEIGHT - 8 * tileSize) / 2);

        loadTextures();
        loadSettings();

        sndStalemate = Gdx.audio.newSound(Gdx.files.internal("stalemate.mp3"));
        sndCheckmate = Gdx.audio.newSound(Gdx.files.internal("checkmatelose.mp3"));
        sndCheck = Gdx.audio.newSound(Gdx.files.internal("check.mp3"));

        btnBack = new SunButton("x", font70, 850, 1600);
        board = new ChessBoard();
    }
    @Override
    public void show() {
        if (variants == CLASSIC) {board.initializeBoard();}
        if (variants == CHESS960){board.initializeFisherBoard();}
        whiteTime = timer;
        blackTime = timer;
    }

    @Override
    public void render(float delta) {
        //касания
        touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touch);
        if(Gdx.input.justTouched()) {
            if (btnBack.hit(touch)) {
                main.setScreen(main.screenMenu);
                if (flipPosition == TO_BLACK) flipPosition = TO_WHITE;
                gameOver = GAME_ON;
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        board.board[i][j] = null;
                    }
                }
            }
        }
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);

        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        drawBoard();
        drawPieces();
        updateTimers();
        drawTimers();
        btnBack.font.draw(batch, btnBack.text, btnBack.x, btnBack.y);
        font70.draw(batch, main.player.name, 500, 250);
        font70.draw(batch, main.player.enemy, 500, 1450);
        if(gameOver == TIME_IS_OVER){
            font70.draw(batch, "Time is over", 0, 1550, SCR_WIDTH, Align.center, false);
        }
        if(gameOver == CHECKMATE){
            font70.draw(batch, textForCheckmate, 0, 1550, SCR_WIDTH, Align.center, false);
        }
        if (gameOver == STALEMATE){
            font70.draw(batch, textForStalemate, 0, 1550, SCR_WIDTH, Align.center, false);
        }
        if (selectedPiece != null) drawSelection();
        batch.end();
        handleInput();
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
    public void dispose() {
        batch.dispose();
        lightTile.dispose();
        darkTile.dispose();
        for (Texture t : whitePieces) t.dispose();
        for (Texture t : blackPieces) t.dispose();
    }

    private void loadTextures() {
        lightTile = new Texture(Gdx.files.internal("cellwhite.png"));
        darkTile = new Texture(Gdx.files.internal("cellblack.png"));

        // Загрузка текстур белых фигур
        whitePieces = new Texture[6];
        whitePieces[0] = new Texture(Gdx.files.internal("pieces/chess_pawn_w.png")); // пешка
        whitePieces[1] = new Texture(Gdx.files.internal("pieces/chess_horse_w.png")); // конь
        whitePieces[2] = new Texture(Gdx.files.internal("pieces/chess_bishop_w.png")); // слон
        whitePieces[3] = new Texture(Gdx.files.internal("pieces/chess_rook_w.png")); // ладья
        whitePieces[4] = new Texture(Gdx.files.internal("pieces/chess_queen_w.png")); // ферзь
        whitePieces[5] = new Texture(Gdx.files.internal("pieces/chess_king_w.png")); // король

        // Загрузка текстур черных фигур
        blackPieces = new Texture[6];
        blackPieces[0] = new Texture(Gdx.files.internal("pieces/chess_pawn_b.png")); // пешка
        blackPieces[1] = new Texture(Gdx.files.internal("pieces/chess_horse_b.png")); // конь
        blackPieces[2] = new Texture(Gdx.files.internal("pieces/chess_bishop_b.png")); // слон
        blackPieces[3] = new Texture(Gdx.files.internal("pieces/chess_rook_b.png")); // ладья
        blackPieces[4] = new Texture(Gdx.files.internal("pieces/chess_queen_b.png")); // ферзь
        blackPieces[5] = new Texture(Gdx.files.internal("pieces/chess_king_b.png")); // король
    }
    private void drawSelection() {
        batch.setColor(1, 1, 0, 0.5f);
        batch.draw(lightTile, boardOffsetX + selectedX * tileSize, boardOffsetY + selectedY * tileSize, tileSize, tileSize);
        batch.setColor(1, 1, 1, 1);
    }
    private void drawBoard() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Texture tile = (x + y) % 2 == 0 ? lightTile : darkTile;
                batch.draw(tile, boardOffsetX + x * tileSize, boardOffsetY + y * tileSize, tileSize, tileSize);
            }
        }
    }
    private void drawPieces() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null) {
                    Texture texture = getPieceTexture(piece);

                    float drawX = boardOffsetX + x * tileSize;
                    float drawY = boardOffsetY + y * tileSize;

                    // Параметры переворота
                    float originX = tileSize / 2f;
                    float originY = tileSize / 2f;
                    assert texture != null;
                    batch.draw(texture,
                        drawX, drawY,
                        originX, originY,
                        tileSize, tileSize,
                        1, 1,
                        flipPosition,
                        0, 0,
                        texture.getWidth(), texture.getHeight(),
                        false, false);
                }
            }
        }
    }
    private Texture getPieceTexture(Piece piece) {
        if (piece.getColor() == ru.Chess.PieceColor.WHITE) {
            switch (piece.getType()) {
                case PAWN: return whitePieces[0];
                case KNIGHT: return whitePieces[1];
                case BISHOP: return whitePieces[2];
                case ROOK: return whitePieces[3];
                case QUEEN: return whitePieces[4];
                case KING: return whitePieces[5];
            }
        } else {
            switch (piece.getType()) {
                case PAWN: return blackPieces[0];
                case KNIGHT: return blackPieces[1];
                case BISHOP: return blackPieces[2];
                case ROOK: return blackPieces[3];
                case QUEEN: return blackPieces[4];
                case KING: return blackPieces[5];
            }
        }
        return null;
    }
    private void handleInput() {
        if (gameOver == CHECKMATE || gameOver == STALEMATE) return;

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            int x = (int) ((touchPos.x - boardOffsetX) / tileSize);
            int y = (int) ((touchPos.y - boardOffsetY) / tileSize);

            if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                if (selectedPiece == null) {
                    selectedPiece = board.getPiece(x, y);
                    if (selectedPiece != null && selectedPiece.getColor() == currentPlayer) {
                        selectedX = x;
                        selectedY = y;
                    } else {
                        selectedPiece = null;
                    }
                } else {
                    if (board.isValidMove(selectedX, selectedY, x, y, currentPlayer)) {

                        // Проверка, убирает ли ход шах
                        if (!board.wouldLeaveKingInCheck(selectedX, selectedY, x, y, currentPlayer)) {
                            board.movePiece(selectedX, selectedY, x, y);
                            gameConditions();
                            switchPlayer();
                        }
                    }
                    selectedPiece = null;
                    selectedX = -1;
                    selectedY = -1;
                }
            }
        }
    }
    public void switchPlayer() {
        if (flipPosition == TO_WHITE) flipPosition = TO_BLACK;
        else if (flipPosition == TO_BLACK) flipPosition = TO_WHITE;
        currentPlayer = (currentPlayer == PieceColor.WHITE) ? PieceColor.WHITE : PieceColor.BLACK;
        timeElapsed = 0;
        if (gameOver == GAME_ON && !btnBack.hit(touch)) checkGameResult();
    }
    private void gameConditions() {
        isWhiteTurn = !isWhiteTurn;
        currentPlayer = isWhiteTurn ? PieceColor.WHITE : PieceColor.BLACK;
        timeElapsed = 0;
    }
    private void updateTimers() {
        if (gameOver == CHECKMATE || gameOver == STALEMATE) return;

        timeElapsed += Gdx.graphics.getDeltaTime();

        if (timeElapsed >= 1.0f) {
            timeElapsed = 0;

            if (isWhiteTurn) {
                whiteTime--;
            } else {
                blackTime--;
            }

            // Проверка на окончание времени
            if (whiteTime <= 0 || blackTime <= 0) {
                gameOver = TIME_IS_OVER;
            }
        }
    }
    private void drawTimers() {
        String whiteTimeStr = formatTime(whiteTime);
        String blackTimeStr = formatTime(blackTime);

        // Верхний таймер
        int timerYPositionTop = 1450;
        font70.draw(batch, blackTimeStr,100, timerYPositionTop);
        // Нижний таймер
        int timerYPositionBottom = 250;
        font70.draw(batch, whiteTimeStr,100, timerYPositionBottom);
    }

    @SuppressWarnings("DefaultLocale")
    private String formatTime(float seconds) {
        int minutes = (int)(seconds / 60);
        int secs = (int)(seconds % 60);
        return String.format("%02d:%02d", minutes, secs);
    }

    private void checkGameResult() {
        if (board.isInCheck(currentPlayer) && !board.isCheckmate(currentPlayer)) {
            if (isSoundOn) sndCheck.play();
        } else if (board.isCheckmate(currentPlayer)) {
                textForCheckmate = (currentPlayer == PieceColor.WHITE ? "Black" : "White") + " wins";
                gameOver = CHECKMATE;
                if (isSoundOn) sndCheckmate.play();

        } else if (board.isStalemate(currentPlayer)) {
            textForStalemate = "Stalemate";
            gameOver = STALEMATE;
            if (isSoundOn) sndStalemate.play();
        }
    }
    public void loadSettings(){
        Preferences prefs = Gdx.app.getPreferences("ChessSettings");
        main.player.name = prefs.getString("name", "Noname");
        isSoundOn = prefs.getBoolean("sound", true);
    }
}
