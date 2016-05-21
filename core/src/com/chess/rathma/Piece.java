package com.chess.rathma;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Base Piece class. Might abstract to the 6 piece types.
 * For now we'll just leave the logic in here as a switch.
 * */
public class Piece extends Actor {

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

    Colour colour;
    Type piece;
    Piece(Colour c, Type t)
    {
        colour = c;
        piece = t;

    }
    public void setSprite(Colour c, Type t)
    {


    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }
}
