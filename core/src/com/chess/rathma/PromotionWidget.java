package com.chess.rathma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.chess.rathma.Packets.PromotionPacket;


/**
 * Created by rathma on 5/27/16.
 */
public class PromotionWidget extends WidgetGroup{
    /* Let's actually plan this out...ffs
    * Background is easy, it is just a texture that fills this container.
    * We will inherit WidgetGroup since VerticalGroup can't really position shit easily.
    * VerticalGroup's Y will forever be ~525, or 8, since it must be along the 8th rank
    * VerticalGroup's X will be passed in and multiplied by the board multiplier.
    * Pieces, or Texture of pieces will be added to the vGroup with listeners.
    *
    * */
    private Sprite blanket;
    private TextureRegion[][] regions; //Our piece regions.
    public VerticalGroup vGroup;

    public ChessBoard chessBoard;
    public GameRoom gameRoom;


    public PromotionWidget(final GameRoom gameRoom, ChessBoard chessBoard, int pawnX){

        this.gameRoom = gameRoom;
        this.chessBoard = chessBoard;


        /* Setting a background */
        blanket = new Sprite(chessBoard.atlas.findRegion("translucentboard"));
        //setBackground(new SpriteDrawable(blanket));
        blanket.setBounds(0,0,chessBoard.getWidth(),chessBoard.getHeight());

        /* Setting up our vGroup */
        vGroup = new VerticalGroup();
        vGroup.space(1);

        /* Flags */
        setTouchable(Touchable.enabled);
        setFillParent(true);
        vGroup.setTouchable(Touchable.enabled);
        this.setTransform(true);


        float boardMultiplier = ((chessBoard.getWidth() + chessBoard.getHeight()) / 2) / 8;
        vGroup.setBounds(pawnX*boardMultiplier,chessBoard.getHeight()/2,chessBoard.getWidth()/8,chessBoard.getHeight()/2);

        /* Generating the pieces we need */
        Piece queen;
        Piece rook;
        Piece bishop;
        Piece knight;
        if(gameRoom.colour== GameRoom.COLOUR.BLACK)
        {
            System.out.println("Spawning black promo pieces");
            //Add 4 black pieces to vGroup
            queen = new Piece(Piece.Colour.BLACK, Piece.Type.QUEEN,pawnX,7,chessBoard.atlas.findRegion("bq"),gameRoom,chessBoard,true);
            rook = new Piece(Piece.Colour.BLACK, Piece.Type.ROOK,pawnX,6,chessBoard.atlas.findRegion("br"),gameRoom,chessBoard,true);
            knight = new Piece(Piece.Colour.BLACK, Piece.Type.KNIGHT,pawnX,5,chessBoard.atlas.findRegion("bn"),gameRoom,chessBoard,true);
            bishop = new Piece(Piece.Colour.BLACK, Piece.Type.BISHOP,pawnX,4,chessBoard.atlas.findRegion("bb"),gameRoom,chessBoard,true);
            queen.addListener(new PromotionListener(pawnX){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    System.out.println("Clicked queen");
                    gameRoom.chess.network.sendTCP(new PromotionPacket(pawnX,0,1,gameRoom.gameID,0));
                }
            });
            rook.addListener(new PromotionListener(pawnX){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    System.out.println("Clicked rook");
                    gameRoom.chess.network.sendTCP(new PromotionPacket(pawnX,0,2,gameRoom.gameID,0));

                }
            });
            knight.addListener(new PromotionListener(pawnX){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    System.out.println("Clicked knight");
                    gameRoom.chess.network.sendTCP(new PromotionPacket(pawnX,0,3,gameRoom.gameID,0));
                }
            });
            bishop.addListener(new PromotionListener(pawnX){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    System.out.println("Clicked bishop");
                    gameRoom.chess.network.sendTCP(new PromotionPacket(pawnX,0,4,gameRoom.gameID,0));

                }
            });

            vGroup.addActor(queen);
            vGroup.addActor(rook);
            vGroup.addActor(knight);
            vGroup.addActor(bishop);

        }
        else if(gameRoom.colour== GameRoom.COLOUR.WHITE)
        {
            System.out.println("Spawning white promo pieces");
            //Add 4 white pieces to vgroup
            queen = new Piece(Piece.Colour.WHITE, Piece.Type.QUEEN,pawnX,7,chessBoard.atlas.findRegion("wq"),gameRoom,chessBoard,true);
            rook = new Piece(Piece.Colour.WHITE, Piece.Type.ROOK,pawnX,6,chessBoard.atlas.findRegion("wr"),gameRoom,chessBoard,true);
            knight = new Piece(Piece.Colour.WHITE, Piece.Type.KNIGHT,pawnX,5,chessBoard.atlas.findRegion("wn"),gameRoom,chessBoard,true);
            bishop = new Piece(Piece.Colour.WHITE, Piece.Type.BISHOP,pawnX,4,chessBoard.atlas.findRegion("wb"),gameRoom,chessBoard,true);

            queen.addListener(new PromotionListener(pawnX){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    System.out.println("Clicked queen");
                    gameRoom.chess.network.sendTCP(new PromotionPacket(pawnX,7,7,gameRoom.gameID,0));

                }
            });
            rook.addListener(new PromotionListener(pawnX){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    System.out.println("Clicked rook");
                    gameRoom.chess.network.sendTCP(new PromotionPacket(pawnX,7,8,gameRoom.gameID,0));

                }
            });
            knight.addListener(new PromotionListener(pawnX){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    System.out.println("Clicked knight");
                    gameRoom.chess.network.sendTCP(new PromotionPacket(pawnX,7,9,gameRoom.gameID,0));

                }
            });
            bishop.addListener(new PromotionListener(pawnX){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    System.out.println("Clicked bishop");
                    gameRoom.chess.network.sendTCP(new PromotionPacket(pawnX,7,10,gameRoom.gameID,0));
                }
            });

            vGroup.addActor(queen);
            vGroup.addActor(rook);
            vGroup.addActor(knight);
            vGroup.addActor(bishop);
        }
        addActor(vGroup);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        blanket.draw(batch);

        super.draw(batch, parentAlpha);
    }
}
