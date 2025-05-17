package ru.Chess;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class ChessBoard {
    private Piece[][] board;
    private boolean whiteKingMoved = false;
    private boolean[] whiteRooksMoved = {false, false}; // [a-side, h-side]
    private boolean blackKingMoved = false;
    private boolean[] blackRooksMoved = {false, false};
    private int[] enPassantTarget = null; // [x, y] координаты клетки для взятия на проходе

    Sound sndPieceMove;
    Sound sndCheck;
    Sound sndCheckmate;

    public ChessBoard() {
        board = new Piece[8][8];
        sndPieceMove = Gdx.audio.newSound(Gdx.files.internal("piecemove.mp3"));
        sndCheck = Gdx.audio.newSound(Gdx.files.internal("check.mp3"));
    }

    public void initializeBoard() {
        // Расстановка черных фигур
        board[0][0] = new Piece(PieceType.ROOK, ru.Chess.PieceColor.WHITE);
        board[1][0] = new Piece(PieceType.KNIGHT, ru.Chess.PieceColor.WHITE);
        board[2][0] = new Piece(PieceType.BISHOP, ru.Chess.PieceColor.WHITE);
        board[3][0] = new Piece(PieceType.QUEEN, ru.Chess.PieceColor.WHITE);
        board[4][0] = new Piece(PieceType.KING, ru.Chess.PieceColor.WHITE);
        board[5][0] = new Piece(PieceType.BISHOP, ru.Chess.PieceColor.WHITE);
        board[6][0] = new Piece(PieceType.KNIGHT, ru.Chess.PieceColor.WHITE);
        board[7][0] = new Piece(PieceType.ROOK, ru.Chess.PieceColor.WHITE);

        for (int i = 0; i < 8; i++) {
            board[i][1] = new Piece(PieceType.PAWN, ru.Chess.PieceColor.WHITE);
        }

        // Расстановка белых фигур
        board[0][7] = new Piece(PieceType.ROOK, ru.Chess.PieceColor.BLACK);
        board[1][7] = new Piece(PieceType.KNIGHT, ru.Chess.PieceColor.BLACK);
        board[2][7] = new Piece(PieceType.BISHOP, ru.Chess.PieceColor.BLACK);
        board[3][7] = new Piece(PieceType.QUEEN, ru.Chess.PieceColor.BLACK);
        board[4][7] = new Piece(PieceType.KING, ru.Chess.PieceColor.BLACK);
        board[5][7] = new Piece(PieceType.BISHOP, ru.Chess.PieceColor.BLACK);
        board[6][7] = new Piece(PieceType.KNIGHT, ru.Chess.PieceColor.BLACK);
        board[7][7] = new Piece(PieceType.ROOK, ru.Chess.PieceColor.BLACK);

        for (int i = 0; i < 8; i++) {
            board[i][6] = new Piece(PieceType.PAWN, ru.Chess.PieceColor.BLACK);
        }
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x >= 8 || y < 0 || y >= 8) return null;
        return board[x][y];
    }

    public void movePiece(int fromX, int fromY, int toX, int toY) {
        sndPieceMove.play();
        board[toX][toY] = board[fromX][fromY];
        board[fromX][fromY] = null;
    }

    public boolean isValidMove(int fromX, int fromY, int toX, int toY, PieceColor color) {
        // Проверка на выход за пределы доски
        if (fromX < 0 || fromX >= 8 || fromY < 0 || fromY >= 8 ||
            toX < 0 || toX >= 8 || toY < 0 || toY >= 8) {
            return false;
        }
        // Нельзя ходить на ту же клетку
        if (fromX == toX && fromY == toY) {
            return false;
        }
        Piece piece = getPiece(fromX, fromY);
        Piece target = getPiece(toX, toY);

        // Проверка, что фигура существует и принадлежит текущему игроку
        if (piece == null || piece.getColor() != color) {
            return false;
        }
        // Проверка, что целевая клетка не занята своей фигурой
        if (target != null && target.getColor() == color) {
            return false;
        }
        // Получаем разницу координат
        int deltaX = Math.abs(toX - fromX);
        int deltaY = Math.abs(toY - fromY);

        // Проверка правил движения для каждой фигуры
        switch (piece.getType()) {
            case PAWN:
                return isValidPawnMove(fromX, fromY, toX, toY, color, target);
            case KNIGHT:
                return (deltaX == 1 && deltaY == 2) || (deltaX == 2 && deltaY == 1);
            case BISHOP:
                if (deltaX != deltaY) return false;
                return isPathClear(fromX, fromY, toX, toY);
            case ROOK:
                if (fromX != toX && fromY != toY) return false;
                return isPathClear(fromX, fromY, toX, toY);
            case QUEEN:
                if (deltaX != deltaY && fromX != toX && fromY != toY) return false;
                return isPathClear(fromX, fromY, toX, toY);
            case KING:
                return isValidKingMove(fromX, fromY, toX, toY, color);
            default:
                return false;
        }
    }
    private boolean isValidPawnMove(int fromX, int fromY, int toX, int toY, PieceColor color, Piece target) {
        int direction = (color == PieceColor.BLACK) ? -1 : 1;
        int startRow = (color == PieceColor.BLACK) ? 6 : 1;

        // Обычный ход вперед
        if (fromX == toX && target == null) {
            // На одну клетку
            if (toY == fromY + direction) {
                return true;
            }
            // На две клетки из начальной позиции
            if (fromY == startRow && toY == fromY + 2 * direction &&
                getPiece(fromX, fromY + direction) == null) {
                return true;
            }
        }

        // Взятие фигуры по диагонали
        if (Math.abs(toX - fromX) == 1 && toY == fromY + direction &&
            target != null && target.getColor() != color) {
            return true;
        }

        // Взятие на проходе
        if (enPassantTarget != null && toX == enPassantTarget[0] && toY == enPassantTarget[1] &&
            Math.abs(toX - fromX) == 1 && toY == fromY + direction) {
            return true;
        }

        return false;
    }
    private boolean isValidKingMove(int fromX, int fromY, int toX, int toY, PieceColor color) {
        int deltaX = Math.abs(toX - fromX);
        int deltaY = Math.abs(toY - fromY);

        // Обычный ход короля
        if ((deltaX <= 1 && deltaY <= 1)) {
            return true;
        }if (deltaX == 2 && deltaY == 0 && fromY == toY &&
            (fromY == 0 || fromY == 7) && !isInCheck(color)) {

            // Короткая рокировка (h-side)
            if (toX == 6) {
                Piece rook = getPiece(7, fromY);
                if (rook != null && rook.getType() == PieceType.ROOK && rook.getColor() == color) {
                    if (color == PieceColor.BLACK) {
                        if (!whiteKingMoved && !whiteRooksMoved[1] &&
                            getPiece(5, fromY) == null && getPiece(6, fromY) == null) {
                            return true;
                        }
                    } else {
                        if (!blackKingMoved && !blackRooksMoved[1] &&
                            getPiece(5, fromY) == null && getPiece(6, fromY) == null) {
                            return true;
                        }
                    }
                }
            }
            // Длинная рокировка (a-side)
            else if (toX == 2) {
                Piece rook = getPiece(0, fromY);
                if (rook != null && rook.getType() == PieceType.ROOK && rook.getColor() == color) {
                    if (color == PieceColor.BLACK) {
                        if (!whiteKingMoved && !whiteRooksMoved[0] &&
                            getPiece(1, fromY) == null && getPiece(2, fromY) == null &&
                            getPiece(3, fromY) == null) {
                            return true;
                        }
                    } else {
                        if (!blackKingMoved && !blackRooksMoved[0] &&
                            getPiece(1, fromY) == null && getPiece(2, fromY) == null &&
                            getPiece(3, fromY) == null) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    private boolean isPathClear(int fromX, int fromY, int toX, int toY) {
        int stepX = Integer.compare(toX, fromX);
        int stepY = Integer.compare(toY, fromY);

        int currentX = fromX + stepX;
        int currentY = fromY + stepY;

        while (currentX != toX || currentY != toY) {
            if (getPiece(currentX, currentY) != null) {
                return false;
            }
            currentX += stepX;
            currentY += stepY;
        }

        return true;
    }
    public boolean isInCheck(PieceColor color) {
        // Находим позицию короля
        int kingX = -1, kingY = -1;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getType() == PieceType.KING &&
                    piece.getColor() == color) {
                    kingX = x;
                    kingY = y;
                    break;
                }
            }
            if (kingX != -1) break;
        }
        // Проверяем, есть ли атака на короля
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getColor() != color) {
                    if (isValidMove(x, y, kingX, kingY, piece.getColor())) {
                        sndCheck.play();
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean isCheckmate(PieceColor color) {
        if (!isInCheck(color)) return false;

        // Проверяем все возможные ходы, чтобы убедиться, что нет выхода из шаха
        for (int fromY = 0; fromY < 8; fromY++) {
            for (int fromX = 0; fromX < 8; fromX++) {
                Piece piece = getPiece(fromX, fromY);
                if (piece != null && piece.getColor() == color) {
                    for (int toY = 0; toY < 8; toY++) {
                        for (int toX = 0; toX < 8; toX++) {
                            if (isValidMove(fromX, fromY, toX, toY, color)) {
                                // Делаем временный ход
                                Piece temp = getPiece(toX, toY);
                                board[toX][toY] = piece;
                                board[fromX][fromY] = null;

                                boolean stillInCheck = isInCheck(color);

                                // Отменяем временный ход
                                board[fromX][fromY] = piece;
                                board[toX][toY] = temp;

                                if (!stillInCheck) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
