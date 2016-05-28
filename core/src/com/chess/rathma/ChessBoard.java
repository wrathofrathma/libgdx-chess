package com.chess.rathma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
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

    /* Piece Textures */
    public TextureRegion[][] regions; //We will pass this in from another class, if it's not passed we will gen our own.
    /* Piece ID numbers - Only relevant for texture region
     * 0 WPawn
     * 1 WBishop
     * 2 WKnight
     * 3 WRook
     * 4 WQueen
     * 5 WKing
     * 6 BPawn
     * 7 BBishop
     * 8 BKnight
     * 9 BRook
     * 10 BQueen
     * 11 BKing
     */
    /* Board texture stuff */
    public Sprite texture; //Will house the board texture
    public String boardTexture = "defaultboard600.png";
    //public String boardTexture = "defaultboard544.png";

    public int boardID; //Might need this in the future for differentiating boards.
    public int promotionLock=-1; //As long as this isn't between 0-7 it isn't locked.
    public ChessBoard(TextureRegion[][] regions, GameRoom gameRoom)
    {

        this.regions = regions;
        this.gameRoom = gameRoom;

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
        texture = new Sprite(new Texture(Gdx.files.internal(boardTexture)));
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


    public void loadBoard(String _boardTexture)
    {
        boardTexture = _boardTexture;
        if(texture!=null)
            texture.getTexture().dispose();
        loadBoard();

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
                getPiece(x, y).texture = new Sprite(regions[0][1]);
                break;
            case 2:
                getPiece(x, y).texture = new Sprite(regions[0][2]);
                break;
            case 3:
                getPiece(x, y).texture = new Sprite(regions[0][3]);
                break;
            case 4:
                getPiece(x, y).texture = new Sprite(regions[0][4]);
                break;
            case 7:
                getPiece(x, y).texture = new Sprite(regions[1][1]);
                break;
            case 8:
                getPiece(x, y).texture = new Sprite(regions[1][2]);
                break;
            case 9:
                getPiece(x, y).texture = new Sprite(regions[1][3]);
                break;
            case 10:
                getPiece(x, y).texture = new Sprite(regions[1][4]);
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
                                    pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.KING, i, j, regions[0][0], gameRoom, this, false));
                                    break;
                                case 1:
                                    pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.QUEEN, i, j, regions[0][1], gameRoom, this, false));
                                    break;
                                case 2:
                                    pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.ROOK, i, j, regions[0][2], gameRoom, this, false));
                                    break;
                                case 3:
                                    pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.KNIGHT, i, j, regions[0][3], gameRoom, this, false));
                                    break;
                                case 4:
                                    pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.BISHOP, i, j, regions[0][4], gameRoom, this, false));
                                    break;
                                case 5:
                                    pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.PAWN, i, j, regions[0][5], gameRoom, this, false));
                                    break;
                                case 6:
                                    pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.KING, i, j, regions[1][0], gameRoom, this, false));
                                    break;
                                case 7:
                                    pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.QUEEN, i, j, regions[1][1], gameRoom, this, false));
                                    break;
                                case 8:
                                    pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.ROOK, i, j, regions[1][2], gameRoom, this, false));
                                    break;
                                case 9:
                                    pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.KNIGHT, i, j, regions[1][3], gameRoom, this, false));
                                    break;
                                case 10:
                                    pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.BISHOP, i, j, regions[1][4], gameRoom, this, false));
                                    break;
                                case 11:
                                    pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.PAWN, i, j, regions[1][5], gameRoom, this, false));
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

