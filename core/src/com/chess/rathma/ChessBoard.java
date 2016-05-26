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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
public class ChessBoard extends WidgetGroup {

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
    public String boardTexture = "defaultboard544.png";

    public int boardID; //Might need this in the future for differentiating boards.

    public ChessBoard(TextureRegion[][] regions, GameRoom gameRoom)
    {
        this.regions = regions;
        this.gameRoom = gameRoom;
        loadBoard();
    }

    public void resize(int width, int height)
    {



    }
    public void loadBoard()
    {
     /* Expected input - String, File descriptor leading to png, jpg, or a text file with instructions on the boarder.
     * Anything without a text file description will be loaded assumed it has no boarder.02
     */

        texture = new Sprite(new Texture(Gdx.files.internal(boardTexture)));
        pieces = new WidgetGroup();
        pieces.setTransform(true);
        setBounds(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        pieces.setBounds(0,0,this.getWidth(),this.getHeight());
        setTouchable(Touchable.enabled);
        pieces.setTouchable(Touchable.enabled);

        addActor(pieces);
        texture.setBounds(this.getX(),this.getY(),this.getWidth(),this.getHeight());


        if(gameRoom!=null && gameRoom.colour== GameRoom.COLOUR.BLACK)
            texture.flip(true,false);

    }
    public void loadBoard(String _boardTexture)
    {
        boardTexture = _boardTexture;
        if(texture!=null)
            texture.getTexture().dispose();
        loadBoard();


    }

    //TODO we'll just have this only ever be called by the server and FORCE this to happen. Should do it after receiving a BoardPosition Packet.
    public synchronized void spawnPieces()
    {
        /* Spawn pieces based on the current activeGameID */
        //Our board is 544x544, that's 68x68 per square.
        /* Let's check for any lingering pieces before spawning actually */
        Array<Piece> removal=new Array<Piece>();
        for(Actor actor : pieces.getChildren())
        {
            if(actor instanceof Piece)
            {
                removal.add((Piece)actor);
            }
        }
        if(removal.size>0) {
            pieces.getChildren().removeAll(removal, true);
        }
        //TODO Configure for the boardID instead of assuming a single board.
        if (gameRoom.board != null) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (gameRoom.board[i][j] >= 0 && gameRoom.board[i][j] <= 11) {
                        switch (gameRoom.board[i][j]) {
                            case 0:
                                pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.KING, i, j, regions[0][0], gameRoom,this));
                                break;
                            case 1:
                                pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.QUEEN, i, j, regions[0][1], gameRoom,this));
                                break;
                            case 2:
                                pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.ROOK, i, j, regions[0][2], gameRoom,this));
                                break;
                            case 3:
                                pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.KNIGHT, i, j, regions[0][3], gameRoom,this));
                                break;
                            case 4:
                                pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.BISHOP, i, j, regions[0][4], gameRoom,this));
                                break;
                            case 5:
                                pieces.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.PAWN, i, j, regions[0][5], gameRoom,this));
                                break;
                            case 6:
                                pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.KING, i, j, regions[1][0], gameRoom,this));
                                break;
                            case 7:
                                pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.QUEEN, i, j, regions[1][1], gameRoom,this));
                                break;
                            case 8:
                                pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.ROOK, i, j, regions[1][2], gameRoom,this));
                                break;
                            case 9:
                                pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.KNIGHT, i, j, regions[1][3], gameRoom,this));
                                break;
                            case 10:
                                pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.BISHOP, i, j, regions[1][4], gameRoom,this));
                                break;
                            case 11:
                                pieces.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.PAWN, i, j, regions[1][5], gameRoom,this));
                                break;

                        }
                    }

                }
            }
        }
        else {
            System.out.println("Trying to generate pieces for a board that doesn't exist.");
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        texture.draw(batch);
        pieces.draw(batch, parentAlpha); //Why did you throw a null pointer exception after about 200 games?
    }
    public void dispose()
    {
        texture.getTexture().dispose();
    }
}

