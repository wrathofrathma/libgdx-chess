package com.chess.rathma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * I think that because we plan on implementing a server to handle the actual 8x8 array, we should leave this as a base actor for handling the chessboard, dimensions, and the boarder.
 */
public class ChessBoard extends Actor {

    Sprite texture;
    String boardTexture = "defaultboard544.png";

    public ChessBoard()
    {
        loadBoard();
    }
    public ChessBoard(String _boardTexture)
    {
        loadBoard(_boardTexture);
    }
    public void loadBoard()
    {
     /* Expected input - String, File descriptor leading to png, jpg, or a text file with instructions on the boarder.
     * Anything without a text file description will be loaded assumed it has no boarder.
     */
        texture = new Sprite(new Texture(Gdx.files.internal(boardTexture)));
    }
    public void loadBoard(String _boardTexture)
    {
        boardTexture = _boardTexture;
        if(texture!=null)
            texture.getTexture().dispose();
        loadBoard();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        texture.draw(batch);
    }
    public void dispose()
    {
        texture.getTexture().dispose();
    }
}
