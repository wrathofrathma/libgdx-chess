package com.chess.rathma.Screens;
/* Can't decide the flow of control. Whether MenuScreen should be controlled by Network or etc */
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.chess.rathma.Challenge;
import com.chess.rathma.Chess;
import com.chess.rathma.Packets.ChallengeAcceptPacket;
import com.chess.rathma.Packets.ChallengePacket;
import com.chess.rathma.Packets.CreateGamePacket;
import com.chess.rathma.Packets.RequestPacket;
import com.chess.rathma.Player;
import com.chess.rathma.TextLabel;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class MenuScreen implements Screen {
    public Stage stage;
    BitmapFont font;
    public final Chess chess;
 //   private Timer updateTimer;
    public MenuScreenListener menuScreenListener;
    //We need something to house challenges in....
    public Array<Challenge> challenges;
    private String fontString = "TTF/SourceCodePro-Regular.ttf";
    int challengeLabelId = 2;
    int staticLabelId = 0;
    int playerLabelId = 1;



    public MenuScreen(final Chess chess)
    {
        this.chess = chess;
        challenges = new Array<Challenge>();
        stage = new Stage();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontString));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        font = generator.generateFont(parameter);
        /* Check if we're connected to server.
            * If no, render a button that allows us to connect
            * If yes, then retrieve a list of connected players.
         */
        chess.network.sendTCP(new RequestPacket(1)); //Get our UserID
        generator.dispose();
        Gdx.input.setInputProcessor(stage);
        menuScreenListener = new MenuScreenListener(this);
        chess.network.addListener(menuScreenListener);

        chess.network.sendTCP(new RequestPacket(0));
        chess.network.sendTCP(new RequestPacket(2));
       // updatePlayerList();
        drawChallenges();
        drawUILabels();
        updateTitle();
       // updateTimer = new Timer();
        /*
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("updateTimer-0 hallow!");
                chess.network.sendTCP(new RequestPacket(0));
                updatePlayerList();
                drawUILabels();
                drawChallenges();
            }
        },1000,1000*20);*/


        /* We'll implement the input listener here since text is fucking hard to click
         *
          * In hindsight, the actor stores coordinates differently than text draws them. It was likely a flaw there.
          * */
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
                                label.clicked(chess);
                                break;
                            }
                        }
                    }
                }
            }
        });

    }
    //This should be synchronised because you can receive multiple packets at once, and the network thead isn't waiting.
    public synchronized void challenged(ChallengePacket packet)
    {
        boolean exists=false;
        for(Challenge challenge : challenges)
        {
            //Let's see if it exists first.
            if(challenge.challengeID==packet.challengeID)
            {
                exists=true;
            }
        }

        /* We need to extract the username and ID to create a proper challenge */
        for(Player player : chess.playerList)
        {
            if(player.id == packet.challengerID && !exists)
            {
                challenges.add(new Challenge(player.name,packet.challengeID));
            }
        }
        drawChallenges();
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
    public void drawUILabels()
    {
        removeLabels(0);
        synchronized (stage.getActors()) {
            stage.addActor(new TextLabel("Players in Queue:", font, 50, 500, staticLabelId));
            stage.addActor(new TextLabel("Open Challenges", font, 300, 500, staticLabelId));
        }
    }

    public void drawChallenges()
    {
        removeLabels(challengeLabelId);
        //Add challenge loop draw.
        if(challenges!=null) {
            for (int i = 0; i < challenges.size; i++) {
                stage.addActor(new TextLabel(challenges.get(i).username, font, 300, (475 - i * 25), challengeLabelId){
                    @Override
                    public void clicked(Chess chess) {
                        super.clicked(chess);
                        //Accepting the challenge and switching screens.
                        for(Challenge c : challenges)
                        {
                            if(c.username.equals(text))
                            {
                                /* ACCEPT CHALLENGE */
                                chess.network.sendTCP(new ChallengeAcceptPacket(c.challengeID,chess.userID));
                            }
                        }
                    }
                });
            }
        }
    }

    //We are doing this to prevent concurrent access, however we need to plan ahead.
    //I don't want to lock stage.actors because that'd freeze up the whole screen and halt the render thread...
    public synchronized void removeLabels(int id) {
        //Testing for now - If this causes performance issues we'll have to figure out something else.
        synchronized (stage.getActors()) {
            Array<Actor> removal = new Array<Actor>();
            for (Actor actor : stage.getActors()) {
                if (actor instanceof TextLabel) {
                    if (((TextLabel) actor).getId() == id) {
                        actor.clear();
                        removal.add(actor);
                    }
                }
            }
            stage.getActors().removeAll(removal, true);
        }
    }

    public synchronized void updatePlayerList()
    {
        //Synchronised to the playerList so we don't have any thread shenanigans
        synchronized (chess.playerList) {
            removeLabels(playerLabelId);
            stage.addActor(new TextLabel("Chess Lobby: " + chess.playerList.size + " players connected", font, 50, 540, playerLabelId));
            //For every player in playerlist, create a label.
            int i = 0; //Since we're using an itr instead of an index, we should track this.

            for (Player player : chess.playerList) {
                //Allows the network thread to catch up to the initialisation.
                //  if(player==null) //Currently commented out to set up synchronisation, would prefer this to be broken.
                //    break;
                if (!player.name.equals(chess.nickname)) {
                    stage.addActor(new TextLabel(player.name, font, 50, 475 - (i * 25), playerLabelId) {
                        @Override
                        public void clicked(Chess chess) {
                            for (Player p : chess.playerList)
                                if (p.name.equals(text))
                                    chess.network.sendTCP(new ChallengePacket(p.id, chess.userID, -1));
                            super.clicked(chess);
                        }
                    });
                    i++;
                }
            }
        }
     }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
     //   stage.act();
        if(chess.gameRooms.size>0)
        {
            this.dispose();
            chess.setScreen(new GameScreen(chess));
        }
        if(titleUpdate)
        {
            updateTitle();
            titleUpdate=false;
        }
        Gdx.gl.glClearColor(0, 0, 0, 1); //Black with a transparent bit.
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
        chess.network.removeListener(menuScreenListener);
        stage.dispose();
    }
}
