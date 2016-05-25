package com.chess.rathma;

import com.badlogic.gdx.graphics.g2d.Batch;
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


    public TextureRegion texture;
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
    public Piece(Colour c, Type t)
    {
        colour = c;
        piece = t;
    }

    public int textureSize = 68;
    /* We'll only call this for draw location! */
    public float grabBlackY()
    {
        if(screen.gameRoom.colour== GameRoom.COLOUR.BLACK) {
            switch (locy) {
                case 0:
                    return 7 * textureSize;
                case 1:
                    return 6 * textureSize;
                case 2:
                    return 5 * textureSize;
                case 3:
                    return 4 * textureSize;
                case 4:
                    return 3 * textureSize;
                case 5:
                    return 2 * textureSize;
                case 6:
                    return 1 * textureSize;
                case 7:
                    return 0 * textureSize;
            }
            return 0;
        }
        else{
            return locy*textureSize;
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
    public GameScreen screen;
    public Piece(Colour c, Type t, int x, int y, TextureRegion texture, GameScreen screen)
    {
        pieceID=1;
        this.screen = screen;
        this.texture = texture;
        colour = c;
        piece = t;
        this.locx = x;
        this.locy=y;

        setTouchable(Touchable.enabled);

        setBounds(x*textureSize,grabBlackY(),textureSize,textureSize);
        this.addListener(new PieceListener(this));
    }

    public float trueY()
    {
        if(screen.gameRoom.colour== GameRoom.COLOUR.BLACK)
            return grabBlackY();
        else
            return locy*textureSize;
    }
    public void clicked()
    {
        System.out.println("Clicked!");

    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(texture, getX(), getY());


    }
}
