package ru.Chess;

import static ru.Chess.Main.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.Collections;

public class ChessBoard {
    public Piece[][] board;
    private boolean whiteKingMoved = false;
    private final boolean[] whiteRooksMoved = {false, false};
    private boolean blackKingMoved = false;
    private final boolean[] blackRooksMoved = {false, false};
    private final int[] enPassantTarget = null;

    Sound sndPieceMove;

    public ChessBoard() {
        board = new Piece[8][8];
        sndPieceMove = Gdx.audio.newSound(Gdx.files.internal("piecemove.mp3"));
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x >= 8 || y < 0 || y >= 8) return null;
        return board[x][y];
    }
    public void movePiece(int fromX, int fromY, int toX, int toY) {
        Piece piece = getPiece(fromX, fromY);
        if (piece == null) return;

        if (piece.getType() == PieceType.KING && Math.abs(toX - fromX) == 2) {
            performCastling(fromX, fromY, toX, toY, piece.getColor());
            if (isSoundOn) {sndPieceMove.play();}
            return;
        }
        if (isSoundOn) {sndPieceMove.play();}
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
        Piece piece1 = getPiece(fromX, fromY);
        if (piece1 == null) return false;

        // Проверка рокировки
        if (piece1.getType() == PieceType.KING && Math.abs(toX - fromX) == 2 && fromY == toY) {
            return isValidCastling(fromX, fromY, toX, toY, color);
        }
        // Проверка, что целевая клетка не занята своей фигурой
        if (target != null && target.getColor() == color) {
            return false;
        }
        // Разница координат
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
            // Одна клетка
            if (toY == fromY + direction) {
                return true;
            }
            // Две клетки
            if (fromY == startRow && toY == fromY + 2 * direction &&
                getPiece(fromX, fromY + direction) == null) {
                return true;
            }
        }
        // Взятие фигуры
        if (Math.abs(toX - fromX) == 1 && toY == fromY + direction &&
            target != null && target.getColor() != color) {
            return true;
        }
        // Взятие на проходе
        return enPassantTarget != null && toX == enPassantTarget[0] && toY == enPassantTarget[1] &&
            Math.abs(toX - fromX) == 1 && toY == fromY + direction;
    }

    private boolean isValidKingMove(int fromX, int fromY, int toX, int toY, PieceColor color) {
        int deltaX = Math.abs(toX - fromX);
        int deltaY = Math.abs(toY - fromY);
        return (deltaX <= 1 && deltaY <= 1);
    }

    private boolean isValidCastling(int fromX, int fromY, int toX, int toY, PieceColor color) {
        // Проверка действительности рокировки
        if (Math.abs(toX - fromX) != 2 || fromY != toY) return false;

        boolean isShortCastling = (toX > fromX);
        int y = (color == PieceColor.BLACK) ? 7 : 0;

        // Проверка, что король и ладья не двигались
        boolean kingMoved = (color == PieceColor.BLACK) ? whiteKingMoved : blackKingMoved;
        int rookX = isShortCastling ? 7 : 0;
        boolean rookMoved = (color == PieceColor.BLACK) ?
            (isShortCastling ? whiteRooksMoved[1] : whiteRooksMoved[0]) :
            (isShortCastling ? blackRooksMoved[1] : blackRooksMoved[0]);

        if (kingMoved || rookMoved) return false;

        // Проверка, что между королем и ладьей нет фигур
        int start = Math.min(fromX, rookX) + 1;
        int end = Math.max(fromX, rookX);
        for (int x = start; x < end; x++) {
            if (getPiece(x, y) != null) return false;
        }

        // Проверка, что король не под шахом и не проходит через битое поле
        if (isInCheck(color)) return false;

        // Проверка промежуточных клеток
        int step = isShortCastling ? 1 : -1;
        for (int x = fromX + step; x != toX; x += step) {
            if (wouldLeaveKingInCheck(fromX, fromY, x, y, color)) return false;
        }

        return true;
    }
    private void performCastling(int kingFromX, int kingFromY, int kingToX, int kingToY, PieceColor color) {
        boolean isShortCastling = (kingToX > kingFromX);
        int rookFromX = isShortCastling ? 7 : 0;
        int rookToX = isShortCastling ? 5 : 3;

        // Перемещение короля
        board[kingToX][kingToY] = board[kingFromX][kingFromY];
        board[kingFromX][kingFromY] = null;

        // Перемещение ладьи
        board[rookToX][kingFromY] = board[rookFromX][kingFromY];
        board[rookFromX][kingFromY] = null;

        // Обновление флагов
        if (color == PieceColor.WHITE) {
            whiteKingMoved = true;
            if (isShortCastling) whiteRooksMoved[1] = true;
            else whiteRooksMoved[0] = true;
        } else {
            blackKingMoved = true;
            if (isShortCastling) blackRooksMoved[1] = true;
            else blackRooksMoved[0] = true;
        }
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
        // Нахождение позиции короля
        int kingX = -1, kingY = -1;
        outerloop:
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getType() == PieceType.KING &&
                    piece.getColor() == color) {
                    kingX = x;
                    kingY = y;
                    break outerloop;
                }
            }
        }

        // Проверка, атаки на короля
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getColor() != color) {
                    if (isValidMoveWithoutCheck(x, y, kingX, kingY, piece.getColor())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCheckmate(PieceColor color) {
        if (!isInCheck(color)) return false;

        // Проверка все возможные ходы
        for (int fromY = 0; fromY < 8; fromY++) {
            for (int fromX = 0; fromX < 8; fromX++) {
                Piece piece = getPiece(fromX, fromY);
                if (piece != null && piece.getColor() == color) {
                    for (int toY = 0; toY < 8; toY++) {
                        for (int toX = 0; toX < 8; toX++) {
                            if (isValidMove(fromX, fromY, toX, toY, color)) {
                                // Попытка сделать ход
                                Piece captured = getPiece(toX, toY);
                                board[toX][toY] = piece;
                                board[fromX][fromY] = null;

                                boolean stillInCheck = isInCheck(color);

                                board[fromX][fromY] = piece;
                                board[toX][toY] = captured;

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

    public boolean isStalemate(PieceColor color) {
        if (isInCheck(color)) return false;

        // Проверка, есть ли допустимые ходы
        for (int fromY = 0; fromY < 8; fromY++) {
            for (int fromX = 0; fromX < 8; fromX++) {
                Piece piece = getPiece(fromX, fromY);
                if (piece != null && piece.getColor() == color) {
                    for (int toY = 0; toY < 8; toY++) {
                        for (int toX = 0; toX < 8; toX++) {
                            if (isValidMove(fromX, fromY, toX, toY, color)) {
                                // Попытка сделать ход
                                Piece captured = getPiece(toX, toY);
                                board[toX][toY] = piece;
                                board[fromX][fromY] = null;

                                boolean stillInCheck = isInCheck(color);

                                board[fromX][fromY] = piece;
                                board[toX][toY] = captured;

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

    public boolean wouldLeaveKingInCheck(int fromX, int fromY, int toX, int toY, PieceColor color) {
        // Сохранение текущего состояния
        Piece originalFrom = getPiece(fromX, fromY);
        Piece originalTo = getPiece(toX, toY);

        // Временный ход
        board[toX][toY] = originalFrom;
        board[fromX][fromY] = null;

        // Проверка, остался ли король под шахом
        boolean inCheck = isInCheck(color);

        board[fromX][fromY] = originalFrom;
        board[toX][toY] = originalTo;

        return inCheck;
    }

    private boolean isValidMoveWithoutCheck(int fromX, int fromY, int toX, int toY, PieceColor color) {
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

        // Разница координат
        int deltaX = Math.abs(toX - fromX);
        int deltaY = Math.abs(toY - fromY);

        // Проверка правил движения для каждой фигуры
        switch (piece.getType()) {
            case PAWN:
                return isValidPawnMoveWithoutCheck(fromX, fromY, toX, toY, color, target);
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
                return isValidKingMoveWithoutCheck(fromX, fromY, toX, toY, color);
            default:
                return false;
        }
    }
    private boolean isValidPawnMoveWithoutCheck(int fromX, int fromY, int toX, int toY, PieceColor color, Piece target) {
        int direction = (color == PieceColor.BLACK) ? -1 : 1;
        int startRow = (color == PieceColor.BLACK) ? 6 : 1;

        if (fromX == toX && target == null) {
            if (toY == fromY + direction) {
                return true;
            }
            if (fromY == startRow && toY == fromY + 2 * direction &&
                getPiece(fromX, fromY + direction) == null) {
                return true;
            }
        }
        if (Math.abs(toX - fromX) == 1 && toY == fromY + direction &&
            target != null && target.getColor() != color) {
            return true;
        }
        return enPassantTarget != null && toX == enPassantTarget[0] && toY == enPassantTarget[1] &&
            Math.abs(toX - fromX) == 1 && toY == fromY + direction &&
            getPiece(toX, fromY) != null &&
            getPiece(toX, fromY).getType() == PieceType.PAWN &&
            getPiece(toX, fromY).getColor() != color;
    }
    private boolean isValidKingMoveWithoutCheck(int fromX, int fromY, int toX, int toY, PieceColor color) {
        int deltaX = Math.abs(toX - fromX);
        int deltaY = Math.abs(toY - fromY);

        // Ходы короля
        if ((deltaX <= 1 && deltaY <= 1)) {
            return true;
        }

        // Рокировка
        if (deltaX == 2 && deltaY == 0 && fromY == toY &&
            (fromY == 0 || fromY == 7)) {

            // Проверка, не двигались ли король и ладья
            boolean kingMoved = (color == PieceColor.BLACK) ? whiteKingMoved : blackKingMoved;
            if (kingMoved) return false;

            // Короткая рокировка
            if (toX == 6) {
                Piece rook = getPiece(7, fromY);
                if (rook == null || rook.getType() != PieceType.ROOK || rook.getColor() != color) {
                    return false;
                }

                boolean rookMoved = (color == PieceColor.BLACK) ? whiteRooksMoved[1] : blackRooksMoved[1];
                if (rookMoved) return false;

                // Проверка, свободны ли клетки между королем и ладьей
                return getPiece(5, fromY) == null && getPiece(6, fromY) == null;
            }
            // Длинная рокировка
            else if (toX == 2) {
                Piece rook = getPiece(0, fromY);
                if (rook == null || rook.getType() != PieceType.ROOK || rook.getColor() != color) {
                    return false;
                }

                boolean rookMoved = (color == PieceColor.BLACK) ? whiteRooksMoved[0] : blackRooksMoved[0];
                if (rookMoved) return false;

                // Проверка, свободны ли клетки между королем и ладьей
                return getPiece(1, fromY) == null && getPiece(2, fromY) == null &&
                    getPiece(3, fromY) == null;
            }
        }

        return false;
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
    public void initializeFisherBoard() {
        // Очистка доски
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }

        // Расстановка пешек
        for (int x = 0; x < 8; x++) {
            board[x][1] = new Piece(PieceType.PAWN, PieceColor.WHITE);
            board[x][6] = new Piece(PieceType.PAWN, PieceColor.BLACK);
        }

        ArrayList<PieceType> pieces = new ArrayList<>();
        pieces.add(PieceType.ROOK);
        pieces.add(PieceType.KNIGHT);
        pieces.add(PieceType.BISHOP);
        pieces.add(PieceType.QUEEN);
        pieces.add(PieceType.KING);
        pieces.add(PieceType.BISHOP);
        pieces.add(PieceType.KNIGHT);
        pieces.add(PieceType.ROOK);

        Collections.shuffle(pieces.subList(1, pieces.size()-1));

        // Проверка, что король между ладьями
        int kingPos = pieces.indexOf(PieceType.KING);
        if (kingPos < 1 || kingPos > 6) {
            pieces.set(kingPos, pieces.get(4));
            pieces.set(4, PieceType.KING);
        }

        // Белые фигуры
        for (int x = 0; x < 8; x++) {
            board[x][0] = new Piece(pieces.get(x), PieceColor.WHITE);
        }

        // Черные фигуры
        for (int x = 0; x < 8; x++) {
            board[x][7] = new Piece(pieces.get(x), PieceColor.BLACK);
        }
        fixBishopsPositions();
    }

    private void fixBishopsPositions() {
        int blackBishop1X = -1, blackBishop2X = -1;
        for (int x = 0; x < 8; x++) {
            if (board[x][0] != null && board[x][0].getType() == PieceType.BISHOP) {
                if (blackBishop1X == -1) blackBishop1X = x;
                else blackBishop2X = x;
            }
        }

        // Если слоны на клетках одного цвета
        if ((blackBishop1X + blackBishop2X) % 2 == 0) {
            for (int x = 0; x < 8; x++) {
                if (board[x][0] != null &&
                    board[x][0].getType() != PieceType.BISHOP &&
                    board[x][0].getType() != PieceType.KING) {
                    Piece temp = board[x][0];
                    board[x][0] = board[blackBishop2X][0];
                    board[blackBishop2X][0] = temp;
                    break;
                }
            }
        }
    }
}
