package com.chess.rathma.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.chess.rathma.GameRoom;
import com.chess.rathma.Piece;

/**
 *
 */
public class PieceListener extends ClickListener{
    private Piece piece;
    public PieceListener(Piece piece)
    {
        this.piece = piece;

    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
        if(piece.colour== Piece.Colour.BLACK)
        {
            switch(piece.piece) {
                case PAWN:
                        System.out.println("Clicked black pawn!");
                    break;
                case BISHOP:
                    System.out.println("Clicked black bishop!");
                    break;
                case KNIGHT:
                    System.out.println("Clicked black knight!");
                    break;
                case ROOK:
                    System.out.println("Clicked black rook!");
                    break;
                case KING:
                    System.out.println("Clicked black king!");
                    break;
                case QUEEN:
                    System.out.println("Clicked black queen!");
                    break;
            }
        }
        else{
            switch(piece.piece) {
                case PAWN:
                    System.out.println("Clicked white pawn!");
                    break;
                case BISHOP:
                    System.out.println("Clicked white bishop!");
                    break;
                case KNIGHT:
                    System.out.println("Clicked white knight!");
                    break;
                case ROOK:
                    System.out.println("Clicked white rook!");
                    break;
                case KING:
                    System.out.println("Clicked white king!");
                    break;
                case QUEEN:
                    System.out.println("Clicked white queen!");
                    break;
            }
        }

    }

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer) {
        super.touchDragged(event, x, y, pointer);
        piece.toFront();
        piece.setY(((Gdx.graphics.getHeight()-Gdx.input.getY() - piece.getHeight()/2))-piece.chessBoard.getY());
        piece.setX(((Gdx.input.getX())- piece.getWidth()/2) - piece.chessBoard.getX());
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        super.touchUp(event, x, y, pointer, button);
        int newx = (int)((piece.getX()+piece.getWidth()/2)/piece.boardMultiplier);
        int newy=0; //just need to initialise it.
        if(piece.gameRoom.colour== GameRoom.COLOUR.BLACK)
        {
            newy = (int) ((piece.getY() + piece.getHeight() / 2) / piece.boardMultiplier);
            newy = piece.grabBlackLoc(newy);
        }
        else {
            newy = (int) ((piece.getY() + piece.getHeight() / 2) / piece.boardMultiplier);
        }
        System.out.println("New x & y " + x + " " + y);
        if(newx!=piece.locx ||newy!=piece.locy) {
            piece.gameRoom.attemptMove(piece, newx, newy);
        }
        else
        {
            piece.setX((piece.locx*piece.boardMultiplier));
            if(piece.gameRoom.colour== GameRoom.COLOUR.BLACK) {
                piece.setY(piece.grabBlackY());
            }
            else {
                piece.setY(piece.locy * piece.boardMultiplier);
            }
        }
    }
}
