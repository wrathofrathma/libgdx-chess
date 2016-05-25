package com.chess.rathma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * This is just the actor. The 8x8 board instance will be held in GameRoom.
  */
public class ChessBoard extends Actor {

    public Sprite texture;
    public String boardTexture = "defaultboard544.png";

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
     * Anything without a text file description will be loaded assumed it has no boarder.02
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

