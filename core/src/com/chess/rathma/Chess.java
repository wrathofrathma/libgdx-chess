package com.chess.rathma;

import com.badlogic.gdx.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.utils.Array;
import com.chess.rathma.Packets.*;
import com.chess.rathma.Screens.GameScreen;
import com.chess.rathma.Screens.MenuScreen;
import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

import java.util.Vector;

public class Chess extends Game{

    /* Containers for the network & local information */
    public Array<GameRoom> gameRooms;
    public Array<Challenge> challenges;
    public Array<Player> playerList;
    /* Currently top level to deal with persistence through screens & what happens when we get a MessagePacket
     * Options later are to either keep this - Bad practise.
     * Or we can create an Array of messages here, then have the ChatBox local to each screen fill with them?
     */
    public ChatBox chatBox;


    /* Necessary objects */
    public SpriteBatch batch;
    public Client network;

    /* Flags for whether our containers have been touched */
    public boolean gameFlag;
    public boolean challengeFlag;
    public boolean playerListFlag;

    /* Network stuff */
    //public String addr="[2601:145:c300:4910:9232:47cc:a8bd:c810]"; //TODO change address later.
    public String addr="localhost";
    //public String addr="2601:145:c300:4910::b46c";
    public int portno=7667;

    /* Our actual identification from the network! */
    public int userID;
    //TODO create some sort of authentication system.
    public String nickname;

	@Override
	public void create () {
        playerList = new Array<Player>();
        gameRooms = new Array<GameRoom>();
        challenges = new Array<Challenge>();
        chatBox = new ChatBox(this);

        batch = new SpriteBatch();
        network = new Client();
        network.start(); //Starts the thread and handles reading/writing of the socket. Also notifies listeners.
        network.getKryo().register(MessagePacket.class);
        network.getKryo().register(MovePacket.class);
        network.getKryo().register(RequestPacket.class);
        network.getKryo().register(PlayerListPacket.class);
        network.getKryo().register(Vector.class);
        network.getKryo().register(ChallengePacket.class);
        network.getKryo().register(ChallengeAcceptPacket.class);
        network.getKryo().register(boolean[].class);
        network.getKryo().register(CreateGamePacket.class);
        network.getKryo().register(PlayerInfoPacket.class);
        network.getKryo().register(int[].class);
        network.getKryo().register(int[][].class);
        network.getKryo().register(BoardPosition.class);
        network.getKryo().register(GameEndPacket.class);
        network.getKryo().register(ServerShutdownPacket.class);
        network.getKryo().register(PromotionPacket.class);
        network.getKryo().register(PromotionAccept.class);

        network.addListener(new MasterListener(this));

        try {
            network.connect(5000, addr, portno);
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        menuScreen = new MenuScreen(this);
        setScreen(menuScreen);
	}
    public MenuScreen menuScreen;

	@Override
	public void render () {
        super.render();

	}

    public synchronized void addChallenge(ChallengePacket packet)
    {
        boolean exists=false;
        //TODO make this more efficient. We hit the second loop regardless.
        for(Challenge challenge : challenges)
        {
            //Let's see if it exists first.
            if(challenge.challengeID==packet.challengeID)
            {
                exists=true;
            }
        }

        for(Player player : playerList)
        {
            if(player.id == packet.challengerID && !exists)
            {
                challenges.add(new Challenge(player.name,packet.challengeID));

            }
        }
        challengeFlag = true;
    }
}
