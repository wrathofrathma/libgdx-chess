package com.chess.rathma.Screens;

/* ********************
  * A dedicated game instance.
  *
  * Screen is the correct tool, but perhaps not for the instance of the game?
 **************************/

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.chess.rathma.*;
import com.chess.rathma.Packets.BoardPosition;
import com.chess.rathma.Packets.CreateGamePacket;

//User interface to the GameRoom.

public class GameScreen implements Screen{
    public final Chess chess;

    /* Our containers */
    public Stage stage; //Base container that all actors & layouts are added to.
    public Table table;



    /* Active game objects */
    public int activeGameID;
    private GameListener gameListener;
    public GameRoom gameRoom;
    public ChessBoard board;


    public Skin gameSkin;
    private String gameSkinString = "game.json";

    public BitmapFont endLabelFont;

    public Sound moveSound;
    public GameScreen(final Chess chess)
    {
        this.chess = chess;

    }


    public void boardUpdated()
    {
        /* We also need to load the edited board state? Or should we go with passing a move */

        moveSound.play();
    }

    /* show() is executed whenever the screen unhides or when it is first created */
    @Override
    public void show() {
        /* Initialising most of our UI stuff */
        gameSkin = new Skin(Gdx.files.internal(gameSkinString));
        stage = new Stage(new ScreenViewport());
        table = new Table();


        /* Initialising our media objects & Textures */
        moveSound = Gdx.audio.newSound(Gdx.files.internal("move.mp3"));
        endLabelFont = gameSkin.getFont("default-font") ;

        /* Setting up the active game */
        activeGameID = -1;
        gameListener = new GameListener(this);
        chess.network.addListener(gameListener);
        loadGame(-1);

        /* Our input processor stuff */
        Gdx.input.setInputProcessor(stage);


        //table.setDebug(true);

        /* Setting up our UI */
        table.setBackground(gameSkin.getDrawable("background"));
        table.setFillParent(true);
        table.align(Align.topLeft);
        stage.addActor(table);
        table.add(board)
                .maxHeight(544).maxWidth(544).padTop(28);
        table.row();
        table.add(chess.chatBox).align(Align.bottomLeft)
                .expandX().expandY()
                .padBottom(5).padTop(10).padLeft(10)
                .prefWidth(Gdx.graphics.getWidth()).width(Gdx.graphics.getWidth()-20)
                .prefHeight(200)
                .maxHeight(200);

    }

    public void loadGame(int gameID)
    {

        //TODO need an overall with bughouse chess and multiple boards being supported.
        synchronized (chess.gameRooms) {
            if (gameID == -1) {
                this.activeGameID = chess.gameRooms.first().gameID;
            } else
                this.activeGameID = gameID;
        }
        /* Dispose of all current actors
         * Reinitialise based on the new game data
         */
        stage.clear();

        //TODO we also need to handle multiple positions being sent.
        /* Grab the game instance of the activeGameID */
        for(GameRoom room : chess.gameRooms)
        {
            if(room.gameID==activeGameID)
                this.gameRoom = room;
        }
        /* Set up the player strings for us with TextLabels */
        //TODO check for the gameID instead, and then loop through all of the players. Store as an array rather than two definite values.
        if(gameRoom.player1==null)
        {
            for(Player p : chess.playerList)
            {
                if(p.id==gameRoom.p1)
                    gameRoom.player1 = p.name;
            }
        }
        if(gameRoom.player2==null)
        {
            for(Player p : chess.playerList)
            {
                if(p.id==gameRoom.p2)
                    gameRoom.player2 = p.name;
            }
        }

        board = new ChessBoard(this.gameRoom);
        chess.network.sendTCP(new BoardPosition(activeGameID,true)); //Requesting the starting board position.
    }


    /* Later on might want to send the game ID */
    public void endGame()
    {
        chess.network.removeListener(gameListener);
        for(Actor actor : stage.getActors())
        {
            actor.clearListeners();
        }
        GlyphLayout glyphLayout = new GlyphLayout(endLabelFont,gameRoom.gameEnd.condition);
        stage.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Array<Actor> actors = stage.getActors();
                for(int i=0; i<actors.size;i++)
                {
                    if(actors.get(i) instanceof TextLabel)
                    {
                        TextLabel label = (TextLabel)actors.get(i);
                        if(x>=label.getX() && x<=label.getX()+label.getWidth())
                        {
                            //For some reason, unlike every other object, the location of text objects is the top left rather than bottom left.
                            if(y<=label.getY() && y>=label.getY()-label.getHeight())
                            {
                                System.out.println("Clicked label: " + label.text);
                                label.clicked(chess);
                                break;
                            }
                        }
                    }
                }
            }
        });
        stage.addActor(new TextLabel(gameRoom.gameEnd.condition,endLabelFont,(Gdx.graphics.getWidth()/2)-(glyphLayout.width/2), (Gdx.graphics.getHeight()/2)-(glyphLayout.height/2),3)
        {
            @Override
            public void clicked(Chess chess) {
                super.clicked(chess);
                gameRoom.state= GameRoom.GameState.DESTROY;
            }
        });

    }

    /* Only called on server shutdown */
    public void shutdown(String message){
        chess.network.removeListener(gameListener);
        synchronized (stage.getActors()) {
            for (Actor actor : stage.getActors()) {
                actor.clearListeners();
            }
        }
        GlyphLayout glyphLayout = new GlyphLayout(endLabelFont, message);
        stage.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Array<Actor> actors = stage.getActors();
                for(int i=0; i<actors.size;i++)
                {
                    if(actors.get(i) instanceof TextLabel)
                    {
                        TextLabel label = (TextLabel)actors.get(i);
                        if(x>=label.getX() && x<=label.getX()+label.getWidth())
                        {
                            //For some reason, unlike every other object, the location of text objects is the top left rather than bottom left.
                            if(y<=label.getY() && y>=label.getY()-label.getHeight())
                            {
                                System.out.println("Clicked label: " + label.text);
                                label.clicked(chess);
                                break;
                            }
                        }
                    }
                }
            }
        });
        synchronized (stage.getActors()) {
            stage.addActor(new TextLabel(message, endLabelFont, (Gdx.graphics.getWidth() / 2) - (glyphLayout.width / 2), (Gdx.graphics.getHeight() / 2) - (glyphLayout.height / 2), 3) {
                @Override
                public void clicked(Chess chess) {
                    super.clicked(chess);
                    chess.dispose();
                    System.exit(0);
                }
            });
        }
    }

    //TODO change this to hide() so we're not constantly reinitialising things.
    //Still should drop us in the menuhowever.
    public void destroyGame()
    {
        this.dispose();
        //TODO change this to chess.menuScreen after we change it to not kill itself.
        chess.setScreen(new MenuScreen(chess));
    }

    @Override
    public void render(float delta) {
        /* Check if there are any valid games in process */
        if(chess.gameRooms.size<=0) {
            this.dispose();
            chess.setScreen(new MenuScreen(chess));
        }
        /* Check if current game is completed */
        if(gameRoom.state == GameRoom.GameState.ENDFLAG)
        {
            gameRoom.state = GameRoom.GameState.COMPLETED;
            endGame();
        }
        if(gameRoom.state == GameRoom.GameState.DESTROY) {
            destroyGame();
        }

        stage.act();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        chess.batch.begin();
        stage.draw();
        chess.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        chess.network.removeListener(gameListener);
        stage.dispose();
        for(GameRoom room : chess.gameRooms)
        {
            if(gameRoom.gameID == room.gameID)
            {
                chess.gameRooms.removeValue(room,true);
            }
        }
    }
}
