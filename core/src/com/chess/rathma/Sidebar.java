package com.chess.rathma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.chess.rathma.Packets.MovePacket;
import com.chess.rathma.Packets.SurrenderPacket;
import com.chess.rathma.Screens.GameScreen;

/**
 * This will be the sidebar on the sides of games. Showing every move made, it'll hold a forfeit & draw icon and piece buffers.
 * Features to add -
 * Timer for both sides
 * Player names & connectivity icons
 * Elo/rating indicator
 * Piece buffer for both player 1 & 2
 */
public class Sidebar extends WidgetGroup {
    private Skin skin;
    public GameScreen gameScreen;
    public GameRoom gameRoom;
    private TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("sidebar.atlas"));

    /* Containers */
    private Table table;
    private ScrollPane movePane;
    private Table moveList;

    public Sidebar(final GameScreen gameScreen, Skin skin)
    {
        this.skin = skin;
        this.gameScreen = gameScreen;
        this.gameRoom = gameScreen.gameRoom;

        /* Let's initialise everything */
        table = new Table(skin);
        moveList = new Table(skin); // Using a table so we can add images & stuff :)
        movePane = new ScrollPane(moveList);

        /* Set up our primary table */
        table.setFillParent(true);
        table.align(Align.topLeft);
        table.setBackground(skin.getDrawable("default-round-large"));

        /* Set up our move table */
        movePane.setScrollingDisabled(true,false);
        moveList.setBackground(skin.getDrawable("default-scroll"));

        Label player1 = new Label(gameRoom.player1,skin);
        Label player2 = new Label(gameRoom.player2,skin);

        /* Let's get some icons */
        Image player1Icon = new Image(atlas.findRegion("player"));
        player1Icon.setColor(0,1,0,1);

        Image player2Icon = new Image(atlas.findRegion("player"));
        player2Icon.setColor(0,1,0,1);

        /* This is too convoluted */
        Image surrender = new Image(atlas.findRegion("flag"));
        surrender.setSize(24,24);
        surrender.addListener(new ImageListener(gameRoom.chess) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameRoom.chess.network.sendTCP(new SurrenderPacket(gameRoom.gameID,gameRoom.chess.userID));
            }
        });


        Image home = new Image(atlas.findRegion("home"));
        home.setSize(24,24);
        home.addListener(new ImageListener(gameScreen.chess){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.menuSwitch=true;
            }
        });
        /* Organisation of our icon group */



       // table.setDebug(true);
        /* Organisation of primary table */
        table.add(player2Icon).align(Align.topLeft)
                .maxHeight(24)
                .maxWidth(24);
        table.add(player2)
                .align(Align.left)
                .expandX();
        table.row();
        table.add(movePane)
                .fill(true)
                .expandY()
                .expandX()
                .colspan(2)
                .prefWidth(table.getWidth())
                .maxWidth(table.getWidth())
                .minHeight(200)
                .maxHeight(200);
        table.row();
        table.add(home)
            .align(Align.left)
            .maxHeight(24)
            .maxWidth(24);
        table.add(surrender)
            .align(Align.left)
            .maxHeight(24)
            .maxWidth(24);
        table.row();
        table.add(player1Icon)
                .align(Align.topLeft)
                .maxHeight(24)
                .maxWidth(24);
        table.add(player1)
                .align(Align.left)
                .expandX();

        this.addActor(table);

        loadMoves();
    }
    /* We'll call this once the game is over so we can rearrange things */
    public void gameEnd(String gameEnd)
    {
        table.clear();
        Label player1 = new Label(gameRoom.player1,skin);
        Label player2 = new Label(gameRoom.player2,skin);
        /* Let's get some icons */
        Image player1Icon = new Image(atlas.findRegion("player"));
        player1Icon.setColor(0,1,0,1);

        Image player2Icon = new Image(atlas.findRegion("player"));
        player2Icon.setColor(0,1,0,1);

        /* This is too convoluted */
        Image surrender = new Image(atlas.findRegion("flag"));
        surrender.setSize(24,24);
        surrender.setColor(Color.GRAY);
        Label endGameLabel = new Label(gameEnd,skin);
        endGameLabel.setWrap(true);

        Image home = new Image(atlas.findRegion("home"));
        home.setSize(24,24);
        home.addListener(new ImageListener(gameRoom.chess){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameRoom.state = GameRoom.GameState.DESTROY;
            }
        });
        /* Organisation of our icon group */



        // table.setDebug(true);
        /* Organisation of primary table */
        table.add(player2Icon).align(Align.topLeft)
                .maxHeight(24)
                .maxWidth(24);
        table.add(player2)
                .align(Align.left)
                .expandX();
        table.row();
        table.add(movePane)
                .fill(true)
                .expandY()
                .expandX()
                .colspan(2)
                .prefWidth(table.getWidth())
                .maxWidth(table.getWidth())
                .minHeight(200)
                .maxHeight(200);
        table.row();
        table.add(home)
                .align(Align.left)
                .maxHeight(24)
                .maxWidth(24);
        table.add(surrender)
                .align(Align.left)
                .maxHeight(24)
                .maxWidth(24);
        table.row();
        table.add(player1Icon)
                .align(Align.topLeft)
                .maxHeight(24)
                .maxWidth(24);
        table.add(player1)
                .align(Align.left)
                .expandX();
        table.row();
        table.add(endGameLabel).colspan(2)
            .align(Align.left)
            .expandX();
    }
    public void loadMoves()
    {
        if(gameRoom.moves!=null) {
            for (MovePacket move : gameRoom.moves) {
                addMove(move);
            }
        }
    }
    public void addMove(MovePacket m)
    {
        /* We need to change this to chess notation */
        moveList.row().expandX().spaceTop(1);
        Label move = new Label(m.toString(),skin);
        move.setWrap(true);
        moveList.add(move)
            .align(Align.topLeft)
            .prefWidth(movePane.getWidth())
            .spaceTop(1).spaceBottom(1);
        if(movePane.isScrollY())
        {
            movePane.layout();
            movePane.setScrollY(movePane.getMaxY());
        }
    }
}
