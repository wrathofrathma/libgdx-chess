package com.chess.rathma.Screens;

/* ********************
  * A dedicated game instance.
  *
  * Screen is the correct tool, but perhaps not for the instance of the game?
 **************************/

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.chess.rathma.*;
import com.chess.rathma.Packets.BoardPosition;
import com.chess.rathma.Packets.CreateGamePacket;

//User interface to the GameRoom.

public class GameScreen implements Screen{
    public final Chess chess;
    public int activeGameID;

    private GameListener gameListener;
    public GameRoom gameRoom;

    private Texture pieceTexture;
    private TextureRegion[][] regions;
    private String pieceTexturePath = "chesspieces2.png";
    public Stage stage;
    public BitmapFont endLabelFont;
    private String fontString = "TTF/SourceCodePro-Regular.ttf";

    public Sound moveSound;
    private ChessBoard board;
    public GameScreen(final Chess chess)
    {
        this.chess = chess;
        stage = new Stage();
        activeGameID = -1;
        moveSound = Gdx.audio.newSound(Gdx.files.internal("move.mp3"));
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontString));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size=16;
        parameter.color= Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 1;
        stage.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Clicked: " + x + "," +y);
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
        endLabelFont = generator.generateFont(parameter);

        init();
    }

    /* Keeping everything bundled here in case I decide to not dispose of the entire screen and reuse it later */
    public void init()
    {

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

        pieceTexture = new Texture(Gdx.files.internal(pieceTexturePath));
        regions = TextureRegion.split(pieceTexture, 64,64);
        board = new ChessBoard();
       // stage.addActor(board);
        gameListener = new GameListener(this);
        chess.network.addListener(gameListener);
        loadGame(-1);
    }

    public InputListener pieceListener = new InputListener() {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            System.out.println("Touched: " + x + " " + y);
            return super.touchDown(event, x, y, pointer, button);
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            super.touchUp(event, x, y, pointer, button);
        }
    };

    public void boardUpdated()
    {
        /* We also need to load the edited board state? Or should we go with passing a move */

        moveSound.play();
    }


    @Override
    public void show() {

        Gdx.input.setInputProcessor(stage);
        stage.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked: (" + x+","+y+")");

                super.clicked(event, x, y);

            }
        });

        Gdx.graphics.setTitle(gameRoom.player1 + " vs " + gameRoom.player2 + "    (Game " + activeGameID+")");
    }

    public void loadGame(int gameID)
    {
        if(gameID==-1)
        {
            this.activeGameID=chess.gameRooms.first().gameID;
        }
        else
            this.activeGameID = gameID;
        /* Dispose of all current actors
         * Reinitialise based on the new game data
         */
        stage.clear();
        stage.addActor(board);

        chess.network.sendTCP(new BoardPosition(activeGameID,true)); //Requesting the starting board position.

        /* Grab the game instance of the activeGameID */
        for(GameRoom room : chess.gameRooms)
        {
            if(room.gameID==activeGameID)
                this.gameRoom = room;
        }
        /* Set up the player strings for us with TextLabels */
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

        if(gameRoom.colour== GameRoom.COLOUR.BLACK) {
            //board.setRotation(180f);
            board.texture.flip(true,false);
        }
        spawnPieces();
    }

    public void spawnPieces()
    {
        /* Spawn pieces based on the current activeGameID */
        //Our board is 544x544, that's 68x68 per square.
        /* Let's check for any lingering pieces before spawning actually */
        Array<Piece> removal=new Array<Piece>();
        for(Actor actor : stage.getActors())
        {
            if(actor instanceof Piece)
            {
                removal.add((Piece)actor);
            }
        }
        if(removal.size>0) {
            System.out.println("Probably won't see this for a long time, but if things break it's in spawnPieces for removing actors");
            stage.getActors().removeAll(removal, true);
        }
            if (gameRoom.board != null) {
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (gameRoom.board[i][j] >= 0 && gameRoom.board[i][j] <= 11) {
                            switch (gameRoom.board[i][j]) {
                                case 0:
                                    stage.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.KING, i, j, regions[0][0], this));
                                    break;
                                case 1:
                                    stage.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.QUEEN, i, j, regions[0][1], this));
                                    break;
                                case 2:
                                    stage.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.ROOK, i, j, regions[0][2], this));
                                    break;
                                case 3:
                                    stage.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.KNIGHT, i, j, regions[0][3], this));
                                    break;
                                case 4:
                                    stage.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.BISHOP, i, j, regions[0][4], this));
                                    break;
                                case 5:
                                    stage.addActor(new Piece(Piece.Colour.BLACK, Piece.Type.PAWN, i, j, regions[0][5], this));
                                    break;
                                case 6:
                                    stage.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.KING, i, j, regions[1][0], this));
                                    break;
                                case 7:
                                    stage.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.QUEEN, i, j, regions[1][1], this));
                                    break;
                                case 8:
                                    stage.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.ROOK, i, j, regions[1][2], this));
                                    break;
                                case 9:
                                    stage.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.KNIGHT, i, j, regions[1][3], this));
                                    break;
                                case 10:
                                    stage.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.BISHOP, i, j, regions[1][4], this));
                                    break;
                                case 11:
                                    stage.addActor(new Piece(Piece.Colour.WHITE, Piece.Type.PAWN, i, j, regions[1][5], this));
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
                System.out.println("Clicked: " + x + "," +y);
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
    public void destroyGame()
    {
        this.dispose();
        chess.setScreen(new MenuScreen(chess));
    }
    @Override
    public void render(float delta) {
        //R refreshes the pieces on the board & spawns them in since the network can't beat us atm
        if(Gdx.input.isKeyPressed(Input.Keys.R))
        {
            chess.network.sendTCP(new BoardPosition(activeGameID,true));
            spawnPieces();
        }
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
