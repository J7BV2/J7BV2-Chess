package ru.Chess;

import static ru.Chess.Main.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class ScreenGame implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ChessBoard board;
    private BitmapFont font50, font70;
    private Texture[] whitePieces;
    private Texture[] blackPieces;
    private Texture lightTile, darkTile;

    private Texture imgBackGround;

    private int tileSize;
    private int boardOffsetX, boardOffsetY;

    private Piece selectedPiece = null;
    private int selectedX = -1, selectedY = -1;



    public ScreenGame(Main main) {
        batch = main.batch;
        camera = main.camera;
        font70 = main.font70white;
        font50 = main.font50white;

        imgBackGround = new Texture("space3.png");

        tileSize = (int) (SCR_WIDTH / 8);
        boardOffsetX = 0;
        boardOffsetY = (int) ((SCR_HEIGHT - 8 * tileSize) / 2);

        loadTextures();

        board = new ChessBoard();
        board.initializeBoard();
    }
    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        // Отрисовка доски
        drawBoard();

        // Отрисовка фигур
        drawPieces();

        // Отрисовка выделения
        if (selectedPiece != null) {
            drawSelection();
        }
        batch.end();
        // Обработка ввода
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
        // Загрузка текстур плиток
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
        // Здесь можно добавить эффект выделения выбранной фигуры
        // Например, отрисовку полупрозрачного прямоугольника
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
                    batch.draw(texture, boardOffsetX + x * tileSize, boardOffsetY + y * tileSize, tileSize, tileSize);
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
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            int x = (int) ((touchPos.x - boardOffsetX) / tileSize);
            int y = (int) ((touchPos.y - boardOffsetY) / tileSize);

            if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                if (selectedPiece == null) {
                    // Выбор фигуры
                    selectedPiece = board.getPiece(x, y);
                    if (selectedPiece != null) {
                        selectedX = x;
                        selectedY = y;
                    }
                } else {
                    // Попытка хода
                    if (board.isValidMove(selectedX, selectedY, x, y, selectedPiece.getColor())) {
                        board.movePiece(selectedX, selectedY, x, y);
                    }
                    selectedPiece = null;
                    selectedX = -1;
                    selectedY = -1;
                }
            }
        }
    }
}


