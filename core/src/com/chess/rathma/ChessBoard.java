package com.chess.rathma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

/** This is what we'll extend to house individual containers...
 * Current implementation just houses and loads the texture for the board.
  */


/* Two ways we can do this - First way wins out easily.
* First way, this class actually housing a group then overloading this.draw() to draw everything there.
*   Inside the group would be the texture & a table for the pieces.
*   Would probably extend WidgetGroup rather than actor.
*   Would have a loadPieces(int boardID) to draw & load things.
*
*   Pros - Modular
*
*   Cons - More difficult to implement
*        - Might not be the way libgdx meant for the classes to be use.
*
*
* Second way, GameScreen houses a WidgetGroup
*   GameScreen handles all generation of pieces.
*   Pros - Easy as fuck
* */
//TODO Add a skin - It is more efficient for drawing these things. Also allows us to apply logic outside of the code, such as the dimensions of pieces.
//It'd be pretty bad practise to reload the same resource just because we have multiple boards....>.>
//How about we accept a TextureRegion[][]?
public class ChessBoard extends Container {

    /* Containers */

    //A table makes no sense if we're not using "Empty" Pieces for white space.
    //We should just have a separate group and keep coordinates relative to the Board.
    //public Table pieceTable; //Will hold all Piece Actors
    public WidgetGroup pieces; //We'll just have to restrict the move logic using math.

    public GameRoom gameRoom; //So we can have access to our boardID;
    //Shouldn't board ID be stored here?
    //Definitely should be stored here

    /* We've moved to TextureAtlas because it allows us to more easily generate new textures. This is much more modular */
    /* Public so our promotion widget can access this without passing an unnecessary argument */
    public TextureAtlas atlas;

    /* Board texture stuff */
    public Sprite texture; //Will house the board texture

    public int boardID; //Might need this in the future for differentiating boards.
    public int promotionLock=-1; //As long as this isn't between 0-7 it isn't locked.
    public ChessBoard(GameRoom gameRoom)
    {
        this.gameRoom = gameRoom;
        atlas = new TextureAtlas(Gdx.files.internal("pieces.atlas"));
        loadBoard();
    }

    /* You know what, fuck this...let's just lock the size */
    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        System.out.println("ChessBoard size changed!!");
    }
    /* MUST be called before setting layout stuff in GameScreen */
    public void loadBoard()
    {
        /* Set up our background of each board. */
        this.align(Align.bottomLeft);
        texture = new Sprite(atlas.findRegion("board"));
        this.setBackground(new SpriteDrawable(texture));
        pieces = new WidgetGroup();
        /* This sets our container's X & Y to be relative to it's location & pieces to do the same thing */
        this.setTransform(true);
        pieces.setTransform(true);
        pieces.setBounds(0,0,this.getPrefWidth(),this.getPrefHeight());
        pieces.setFillParent(true);
        setBounds(0,0,this.getPrefWidth(),this.getPrefHeight());

        /* We probably want to be touchable xD That would be beneficial to making a game */
        setTouchable(Touchable.enabled);
        pieces.setTouchable(Touchable.enabled);

        /* Uh, yeah we sort of need to have this here xD Containers can only have one actor */
        setActor(pieces);

        if(gameRoom!=null && gameRoom.colour== GameRoom.COLOUR.BLACK)
            texture.flip(true,false);

    }

    public void promotionDialog(int x){
        synchronized (pieces.getChildren()) {
            pieces.addActor(new PromotionWidget(this.gameRoom, this, x));
        }
    }
    public void releasePromotion()
    {
        synchronized (pieces.getChildren()) {
            Array<Actor> removal=new Array<Actor>();
            for (Actor actor : pieces.getChildren()) {
                if (actor instanceof PromotionWidget) {
                    removal.add(actor);
                }
            }
            pieces.getChildren().removeAll(removal,true);
        }
    }


    public Piece getPiece(int x, int y)
    {
        Piece p;
        synchronized (pieces.getChildren())
        {
            for(Actor actor : pieces.getChildren())
            {
                if(actor instanceof Piece)
                {
                    if(((Piece)actor).locx==x && ((Piece)actor).locy==y)
                        return (Piece)actor;
                }
            }
            return null;
        }
    }


    public void promotionUpdate(int x, int y)
    {
        switch(gameRoom.board[x][y]) {
            case 1:
                getPiece(x, y).texture = new Sprite(atlas.findRegion("bq"));
                break;
            case 2:
                getPiece(x, y).texture = new Sprite(atlas.findRegion("br"));
                break;
            case 3:
                getPiece(x, y).texture = new Sprite(atlas.findRegion("bn"));
                break;
            case 4:
                getPiece(x, y).texture = new Sprite(atlas.findRegion("bb"));
                break;
            case 7:
                getPiece(x, y).texture = new Sprite(atlas.findRegion("wq"));
                break;
            case 8:
                getPiece(x, y).texture = new Sprite(atlas.findRegion("wr"));
                break;
            case 9:
                getPiece(x, y).texture = new Sprite(atlas.findRegion("wn"));
                break;
            case 10:
                getPiece(x, y).texture = new Sprite(atlas.findRegion("wb"));
                break;
        }
    }
    public void spawnPieces()
    {
        synchronized (pieces.getChildren()) {
        /* Spawn pieces based on the current activeGameID */
        /* Let's check for any lingering pieces before spawning actually */
            Array<Piece> removal = new Array<Piece>();
            for (Actor actor : pieces.getChildren()) {
                if (actor instanceof Piece) {
                    removal.add((Piece) actor);
                }
            }
            if (removal.size > 0) {
                pieces.getChildren().removeAll(removal, true);
            }
            //TODO Configure for the boardID instead of assuming a single board.
            if (gameRoom.board != null) {
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (gameRoom.board[i][j] >= 0 && gameRoom.board[i][j] <= 11) {
                            switch (gameRoom.board[i][j]) {
                                case 0:
                                    pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.KING, i, j, atlas.findRegion("bk"), gameRoom, this, false));
                                    break;
                                case 1:
                                    pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.QUEEN, i, j, atlas.findRegion("bq"), gameRoom, this, false));
                                    break;
                                case 2:
                                    pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.ROOK, i, j, atlas.findRegion("br"), gameRoom, this, false));
                                    break;
                                case 3:
                                    pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.KNIGHT, i, j, atlas.findRegion("bn"), gameRoom, this, false));
                                    break;
                                case 4:
                                    pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.BISHOP, i, j, atlas.findRegion("bb"), gameRoom, this, false));
                                    break;
                                case 5:
                                    pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.PAWN, i, j, atlas.findRegion("bp"), gameRoom, this, false));
                                    break;
                                case 6:
                                    pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.KING, i, j, atlas.findRegion("wk"), gameRoom, this, false));
                                    break;
                                case 7:
                                    pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.QUEEN, i, j, atlas.findRegion("wq"), gameRoom, this, false));
                                    break;
                                case 8:
                                    pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.ROOK, i, j, atlas.findRegion("wr"), gameRoom, this, false));
                                    break;
                                case 9:
                                    pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.KNIGHT, i, j, atlas.findRegion("wn"), gameRoom, this, false));
                                    break;
                                case 10:
                                    pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.BISHOP, i, j, atlas.findRegion("wb"), gameRoom, this, false));
                                    break;
                                case 11:
                                    pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.PAWN, i, j, atlas.findRegion("wp"), gameRoom, this, false));
                                    break;

                            }
                        }

                    }
                }
            } else {
                System.out.println("Trying to generate pieces for a board that doesn't exist.");
            }
        }
        layout();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        //TODO watch me for a ConcurrentAccessException later!
        pieces.draw(batch, parentAlpha); //Why did you throw a null pointer exception after about 200 games?
        if(promotionLock>=0 && promotionLock<=7)
        {
            promotionDialog(promotionLock);
            promotionLock=-1;
        }
    }
    public void dispose()
    {
        texture.getTexture().dispose();
    }
}

