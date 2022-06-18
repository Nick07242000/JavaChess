package com.nameNotFound.javaChess.service;

import com.nameNotFound.javaChess.exceptions.InvalidPositionException;
import com.nameNotFound.javaChess.model.Board;
import com.nameNotFound.javaChess.model.pieces.Piece;
import com.nameNotFound.javaChess.userInterface.UserInterface;
import com.nameNotFound.javaChess.utils.Position;
import com.nameNotFound.javaChess.utils.SearchArray;
import com.nameNotFound.javaChess.utils.enums.ColorEnum;
import com.nameNotFound.javaChess.utils.enums.PieceEnum;

import java.util.ArrayList;

public class GameService {
    private final Game game = Game.getInstance();
    private static GameService instance;

    private GameService() {

    }

    public static GameService getInstance() {
        if (instance == null)
            instance = new GameService();
        return instance;
    }

    public Board getBoard() {
        return game.getBoard();
    }

    public String getTurn() {
        return game.getTurn().toString();
    }

    public ArrayList<Piece> getTakenPieces(ColorEnum color) {
        if (color.equals(ColorEnum.BLACK))
            return game.getBlackPiecesTaken();
        return game.getWhitePiecesTaken();
    }

    public void movePiece(String posOne, String posTwo) throws InvalidPositionException {
        Position pos1 = new Position(posOne.charAt(0) - 97, 7 - (posOne.charAt(1) - 49));
        Position pos2 = new Position(posTwo.charAt(0) - 97, 7 - (posTwo.charAt(1) - 49));
        Board board = getBoard();
        Piece pieceOne = board.getPiece(pos1);
        Piece pieceTwo = board.getPiece(pos2);
        ArrayList<Position> possibleMovements = new ArrayList<>();
        ArrayList<Position> possibleTakes = new ArrayList<>();
        if (pieceOne != null) {
            possibleMovements = pieceOne.possibleMovements(pos1);
            possibleTakes = pieceOne.possibleTakes(pos1);
        }
        boolean wasPieceTaken;
        if (pieceOne == null)
            throw new InvalidPositionException("Porfavor seleccione una Pieza!");
        else if (!pieceOne.getColor().equals(game.getTurn()))
            throw new InvalidPositionException("No puede mover las piezas de su oponente!");
        else if (pieceTwo == null) {
            if (!SearchArray.searchPositionInArray(possibleMovements, pos2))
                throw new InvalidPositionException("Esta Pieza no puede realizar ese movimiento!");
        }
        else if (pieceTwo.getColor().equals(game.getTurn())) {
            if (!pieceOne.getName().equals(PieceEnum.KING) && !pieceTwo.getName().equals(PieceEnum.ROOK)) {
                if (pieceOne.getName().equals(PieceEnum.ROOK) && pieceTwo.getName().equals(PieceEnum.KING))
                    throw new InvalidPositionException("Para Enrocar seleccione primero el Rey, y luego la Torre");
                throw new InvalidPositionException("Esa es su Pieza!");
            }
        }
        else if (!SearchArray.searchPositionInArray(possibleTakes, pos2))
            throw new InvalidPositionException("No puede tomar esa Pieza!");
        wasPieceTaken = game.movePiece(pos1, pos2, pieceOne, pieceTwo);
        if (game.isCheck()) {
            game.returnMovementBackwards();
            if (wasPieceTaken) {
                if (game.getTurn().equals(ColorEnum.BLACK)) {
                    Piece removedPiece = game.getWhitePiecesTaken().get(game.getWhitePiecesTaken().size()-1);
                    board.setPiece(removedPiece,pos2);
                    game.removePiece(ColorEnum.WHITE);
                }
                else {
                    Piece removedPiece = game.getBlackPiecesTaken().get(game.getBlackPiecesTaken().size()-1);
                    board.setPiece(removedPiece,pos2);
                    game.removePiece(ColorEnum.BLACK);
                }
            }
            throw new InvalidPositionException("Esta poniendo su Rey en Jaque!");
        }
        game.changeTurn();
        if (game.isCheck()) {
            throw new InvalidPositionException(UserInterface.CHECK_MESSAGE);
        }
    }
}

