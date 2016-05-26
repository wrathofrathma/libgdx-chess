package com.chess.rathma;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.chess.rathma.Screens.GameScreen;
import com.chess.rathma.Screens.PieceListener;

/**
 * Base Piece class. Might abstract to the 6 piece types.
 * For now we'll just leave the logic in here as a switch.
 * */
public class Piece extends Actor {


    public Sprite texture;
    public enum Type {
        PAWN,
        KNIGHT,
        BISHOP,
        ROOK,
        QUEEN,
        KING
    }
    public enum Colour {
        WHITE,
        BLACK
    }


    public Colour colour;
    public Type piece;
    public int pieceID;
    /* X & Y location relative to the board - Values of 0-7*/
    public int locx;
    public int locy;


    public Piece(){
        pieceID=-1;
    }

    public float boardMultiplier; //Using this gets the relative location to the board.

    public float grabBlackY()
    {
        if(gameRoom.colour== GameRoom.COLOUR.BLACK) {
            switch (locy) {
                case 0:
                    return 7 * boardMultiplier;
                case 1:
                    return 6 * boardMultiplier;
                case 2:
                    return 5 * boardMultiplier;
                case 3:
                    return 4 * boardMultiplier;
                case 4:
                    return 3 * boardMultiplier;
                case 5:
                    return 2 * boardMultiplier;
                case 6:
                    return 1 * boardMultiplier;
                case 7:
                    return 0 * boardMultiplier;
            }
            return 0;
        }
        else{
            return locy*boardMultiplier;
        }
    }

    public int grabBlackLoc(int loc)
    {
        if(loc==-1) {
            switch (locy) {
                case 0:
                    return 7;
                case 1:
                    return 6;
                case 2:
                    return 5;
                case 3:
                    return 4;
                case 4:
                    return 3;
                case 5:
                    return 2;
                case 6:
                    return 1;
                case 7:
                    return 0;
            }
            return 0;
        }
        else
        {
            switch (loc) {
                case 0:
                    return 7;
                case 1:
                    return 6;
                case 2:
                    return 5;
                case 3:
                    return 4;
                case 4:
                    return 3;
                case 5:
                    return 2;
                case 6:
                    return 1;
                case 7:
                    return 0;
            }
            return 0;
        }
    }
    public GameRoom gameRoom;
    public ChessBoard chessBoard;

    public Piece(Colour c, Type t, int x, int y, TextureRegion texture, GameRoom gameRoom, ChessBoard chessBoard)
    {
        this.chessBoard = chessBoard;
        pieceID=1;//Why the fuck is this 1?
        this.gameRoom = gameRoom;
        this.texture = new Sprite(texture);
        colour = c;
        piece = t;
        this.locx = x;
        this.locy=y;
        setTouchable(Touchable.enabled);
        boardMultiplier = ((chessBoard.getWidth() + chessBoard.getHeight())/2)/8;
        setBounds(x*boardMultiplier,grabBlackY(),boardMultiplier,boardMultiplier);
        this.addListener(new PieceListener(this));

    }

    public float trueY()
    {
        if(gameRoom.colour== GameRoom.COLOUR.BLACK)
            return grabBlackY();
        else
            return locy*boardMultiplier;
    }
    public void clicked()
    {
        System.out.println("Clicked!");

    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(texture, getX(), getY(), boardMultiplier,boardMultiplier);


    }
}
