package com.chess.rathma.Screens;
/* Can't decide the flow of control. Whether MenuScreen should be controlled by Network or etc */
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.chess.rathma.*;
import com.chess.rathma.Packets.ChallengeAcceptPacket;
import com.chess.rathma.Packets.ChallengePacket;
import com.chess.rathma.Packets.CreateGamePacket;
import com.chess.rathma.Packets.RequestPacket;



public class MenuScreen implements Screen {
    public final Chess chess;

    /* Containers */
    public Stage stage;
    public Table table;

    public ScrollPane playerListPane; //Will be our scrollable object housing our playerList.
    public List playerList; //Will house the actual widgets of players.

    public ScrollPane challengePane;
    public List challengeList;

    /* Style & UI stuff */
    public BitmapFont font; //What the fuck is your purpose actually?
    Skin menuSkin;

    public MenuScreen(final Chess chess)
    {
        this.chess = chess;
        Gdx.graphics.setWindowedMode(600,800);
        /* Initialising components */
        stage = new Stage();


        /* Setting up listeners */
        Gdx.input.setInputProcessor(stage);

        /* Requesting information from the server */
        chess.network.sendTCP(new RequestPacket(1)); //Get our UserID
        chess.network.sendTCP(new RequestPacket(0)); //Request PlayerList
        chess.network.sendTCP(new RequestPacket(2)); //Request challenges?


        //TODO Stuff subject to move to show()
        menuSkin = new Skin(Gdx.files.internal("menu.json"));
        font = menuSkin.getFont("default-font");


        /* Setting up our display */
        table = new Table();
        table.setBackground(new SpriteDrawable(new Sprite(menuSkin.getRegion("background"))));
        table.setFillParent(true);
        table.align(Align.topLeft); //Why the fuck would someone draw from the bottom of the screen?


        stage.addActor(table);

        //Enables debug lines.
        //table.setDebug(true);

        /* Organising the display! */
        /* Giving our panes some depth by adding a background */
        ScrollPane.ScrollPaneStyle paneStyle = new ScrollPane.ScrollPaneStyle();

        playerList = new List(menuSkin);
        playerListPane = new ScrollPane(playerList);
        playerListPane.setStyle(paneStyle);
        playerListPane.setScrollingDisabled(true,false);

        challengeList = new List(menuSkin);
        challengePane = new ScrollPane(challengeList);
        challengePane.setStyle(paneStyle);
        challengePane.setScrollingDisabled(true,false);
        Label challengeLabel = new Label("Challenges",menuSkin);
        Label playerLabel = new Label("Players online",menuSkin);

        //table.setDebug(true);
        /* Actually arranging our layout */
        table.pad(10);
        table.add(playerLabel)
                .align(Align.left)
                .padLeft(60).padTop(10);
        table.add(challengeLabel)
                .align(Align.right)
                .padRight(78).padTop(10);
        table.row();
        table.add(playerListPane)
                .expandX()
                .expandY()
                .align(Align.topLeft)
                .padLeft(10).padTop(10)
                .prefHeight(360)
                .prefWidth(240);
        table.add(challengePane)
                .expandX()
                .expandY()
                .align(Align.topRight)
                .padRight(10).padTop(10)
                .prefHeight(360)
                .prefWidth(240)
                .minWidth(240)
                .minHeight(360);

        /* Setting up our chat box! */
        table.row().padTop(10).expandY().minHeight(10); //This is to prevent any overlap hopefully!
        table.add(chess.chatBox).align(Align.bottomLeft)
                .expandY()
                .padBottom(5).padTop(10)
                .minWidth(560)
                .prefWidth(Gdx.graphics.getWidth()).width(Gdx.graphics.getWidth()-20)
                .prefHeight(200)
                .maxHeight(200);


        /* Setting up listeners for our different panes */
        playerList.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(getTapCount()>=2)
                {
                    chess.network.sendTCP(new ChallengePacket(((PlayerLabel)playerList.getSelected()).player.id, chess.userID, -1));
                }
            }
        });
        challengeList.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(getTapCount()>=2)
                {
                    chess.network.sendTCP(new ChallengeAcceptPacket(((ChallengeLabel)challengeList.getSelected()).challenge.challengeID,chess.userID));
                    synchronized (chess.challenges) {
                        for (Challenge challenge : chess.challenges) {
                            if (challenge.challengeID == ((ChallengeLabel) challengeList.getSelected()).challenge.challengeID) {
                                chess.challenges.removeValue(challenge,true);
                            }
                        }
                    }
                }
            }
        });




    }
    /* Only called on server shutdown */
    public void shutdown(String message){
        synchronized (stage.getActors()) {
            for (Actor actor : stage.getActors()) {
                actor.clearListeners();
            }
        }
        GlyphLayout glyphLayout = new GlyphLayout(font, message);
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
            stage.addActor(new TextLabel(message, font, (Gdx.graphics.getWidth() / 2) - (glyphLayout.width / 2), (Gdx.graphics.getHeight() / 2) - (glyphLayout.height / 2), 3) {
                @Override
                public void clicked(Chess chess) {
                    super.clicked(chess);
                    System.exit(0);
                }
            });
        }
    }
    /* Calling Gdx.graphics.setTitle() outside of the render thread most likely upsets windows.
    * By upsets, I mean literally throws a fucking tantrum and crashes the whole project. So we'll just use a state to keep track of window updates.
    * */
    public boolean titleUpdate=false;
    public void updateTitle()
    {
        if(chess.nickname!=null && chess.playerList!=null)
            Gdx.graphics.setTitle("User: " + chess.nickname + "@Chess Lobby: " + chess.playerList.size + " players connected");
        else if(chess.nickname!=null)
            Gdx.graphics.setTitle("User: " + chess.nickname + "@Chess Lobby: ");
        else
            Gdx.graphics.setTitle("Not Connected D=");
    }

    public void updateChallenges()
    {
        synchronized (chess.challenges)
        {
            Array<ChallengeLabel> challengeArray = new Array<ChallengeLabel>();
            for(Challenge challenge : chess.challenges)
            {
                challengeArray.add(new ChallengeLabel(challenge));
            }
            challengeList.setItems(challengeArray);
        }


    }

    public synchronized void updatePlayerList()
    {
        //Synchronised to the playerList so we don't have any thread shenanigans
        synchronized (chess.playerList) {
            //We need this because GDX's List only takes a single array.
            Array<PlayerLabel> labels=new Array<PlayerLabel>();
            PlayerLabel bufferLabel;
            //For each player in playerlist, create a label and add to our temporary array.
            for(final Player player : chess.playerList)
            {
                //Let's actually check if the player ISN'T us xD
                if(player.id!=chess.userID) {
                    labels.add(new PlayerLabel(player));
                }
                //TODO maybe add a listener to the label? Unless there is one built in.
            }

            //Set the items in the list to contain the list of player labels.
            playerList.setItems(labels);
        }
     }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.act();
        if(chess.gameRooms.size>0)
        {
            //TODO change this to this.hide() - Need to migrate most of hte constructor to the show() however.
            //TODO Also need to properly configure this.hide() to kill listeners
            this.dispose();
            chess.setScreen(new GameScreen(chess));
        }
        if(titleUpdate)
        {
            updateTitle();
            titleUpdate=false;
        }
        if(chess.playerListFlag)
        {
            updatePlayerList();
            chess.playerListFlag=false;
        }
        if(chess.challengeFlag)
        {
            updateChallenges();
            chess.challengeFlag=false;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1); //Black with a transparent bit.
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        chess.batch.begin();
        stage.draw();
        chess.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
        stage.dispose();
    }
}
