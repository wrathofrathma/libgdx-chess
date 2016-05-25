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
	public SpriteBatch batch;
    public Array<Player> playerList;
    public String addr = "10.0.0.43";
    public int portno=7667;
    public Client network;
    public Array<GameRoom> gameRooms;


    public int userID;
    public String nickname;
    //I have a feeling we'll need this later.
    enum STATE {
        MENU,
        GAME
    }

	@Override
	public void create () {
        playerList = new Array<Player>();
        gameRooms = new Array<GameRoom>();

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
}
