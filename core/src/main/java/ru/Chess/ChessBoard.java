package ru.Chess;

public class ChessBoard {
    private Piece[][] board;

    public ChessBoard() {
        board = new Piece[8][8];
    }

    public void initializeBoard() {
        // Расстановка черных фигур
        board[0][0] = new Piece(PieceType.ROOK, ru.Chess.PieceColor.BLACK);
        board[1][0] = new Piece(PieceType.KNIGHT, ru.Chess.PieceColor.BLACK);
        board[2][0] = new Piece(PieceType.BISHOP, ru.Chess.PieceColor.BLACK);
        board[3][0] = new Piece(PieceType.QUEEN, ru.Chess.PieceColor.BLACK);
        board[4][0] = new Piece(PieceType.KING, ru.Chess.PieceColor.BLACK);
        board[5][0] = new Piece(PieceType.BISHOP, ru.Chess.PieceColor.BLACK);
        board[6][0] = new Piece(PieceType.KNIGHT, ru.Chess.PieceColor.BLACK);
        board[7][0] = new Piece(PieceType.ROOK, ru.Chess.PieceColor.BLACK);

        for (int i = 0; i < 8; i++) {
            board[i][1] = new Piece(PieceType.PAWN, ru.Chess.PieceColor.BLACK);
        }

        // Расстановка белых фигур
        board[0][7] = new Piece(PieceType.ROOK, ru.Chess.PieceColor.WHITE);
        board[1][7] = new Piece(PieceType.KNIGHT, ru.Chess.PieceColor.WHITE);
        board[2][7] = new Piece(PieceType.BISHOP, ru.Chess.PieceColor.WHITE);
        board[3][7] = new Piece(PieceType.QUEEN, ru.Chess.PieceColor.WHITE);
        board[4][7] = new Piece(PieceType.KING, ru.Chess.PieceColor.WHITE);
        board[5][7] = new Piece(PieceType.BISHOP, ru.Chess.PieceColor.WHITE);
        board[6][7] = new Piece(PieceType.KNIGHT, ru.Chess.PieceColor.WHITE);
        board[7][7] = new Piece(PieceType.ROOK, ru.Chess.PieceColor.WHITE);

        for (int i = 0; i < 8; i++) {
            board[i][6] = new Piece(PieceType.PAWN, ru.Chess.PieceColor.WHITE);
        }
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x >= 8 || y < 0 || y >= 8) return null;
        return board[x][y];
    }

    public void movePiece(int fromX, int fromY, int toX, int toY) {
        board[toX][toY] = board[fromX][fromY];
        board[fromX][fromY] = null;
    }

    public boolean isValidMove(int fromX, int fromY, int toX, int toY, PieceColor color) {
        Piece piece = getPiece(fromX, fromY);
        if (piece == null || piece.getColor() != color) return false;

        Piece target = getPiece(toX, toY);
        if (target != null && target.getColor() == color) return false;

        // Здесь должна быть логика проверки правил движения для каждой фигуры
        // Это упрощенная версия - только для демонстрации
        return true;
    }
}
