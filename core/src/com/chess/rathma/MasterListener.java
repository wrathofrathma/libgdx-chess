package com.chess.rathma;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Array;
import com.chess.rathma.Packets.*;


import com.chess.rathma.Screens.GameScreen;
import com.chess.rathma.Screens.LoginScreen;
import com.chess.rathma.Screens.MenuScreen;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * We don't need any other listeners other than this in a chess game.
 */
public class MasterListener extends Listener{
    private Chess chess;
    public MasterListener(Chess chess)
    {
        this.chess = chess;
    }

    public void received(Connection connection, Object object)
    {
        if(object instanceof MessagePacket)
        {
            MessagePacket mp = (MessagePacket) object;
            //TODO change this logic to deal with structural changes to Chess where the chatbox is located.
            chess.chatBox.addMessage(mp);
        }
        else if(object instanceof IdentPacket)
        {
            IdentPacket packet = (IdentPacket) object;
            if(packet.acceptBit ==true )
            {
                if(chess.getScreen() instanceof LoginScreen)
                {
                    ((LoginScreen)chess.getScreen()).lock=false;
                }
            }

        }
        else if(object instanceof String)
        {
            System.out.println((String) object);
        }

        /* We're going to put in some work here. Finally decided on a structure.*/
        else if(object instanceof MovePacket)
        {
            /* Iterate through the instances of games for the GameID and then pass the move */
        }

        else if(object instanceof PlayerInfoPacket)
        {
            PlayerInfoPacket packet = (PlayerInfoPacket)object;
            if(packet.specialID==true)
            {
                chess.userID=packet.userID;
                chess.nickname=packet.username;
            }
        }
        else if(object instanceof ServerShutdownPacket)
        {
            ServerShutdownPacket packet = (ServerShutdownPacket) object;
            System.err.println(packet.message);
            if(chess.getScreen() instanceof GameScreen)
            {
                GameScreen screen = (GameScreen) chess.getScreen();
                screen.shutdown(packet.message);
            }
            else if(chess.getScreen() instanceof MenuScreen)
            {
                MenuScreen screen = (MenuScreen) chess.getScreen();
                screen.shutdown(packet.message);

            }
        }
        else if(object instanceof PlayerListPacket)
        {
            Array<Player> removal = new Array<Player>();
            PlayerListPacket plp = (PlayerListPacket) object;
            boolean exists=false;
            int index=0; //Tracks where the gameState boolean is
            synchronized (chess.playerList) {
            /* Adding users not found in the local list */
                for (String username : plp.usernames) {
                    exists = false;
                    for (Player player : chess.playerList) {
                        if (player.name.equals(username))
                            exists = true;
                    }
                    if (!exists) {
                        chess.playerList.add(new Player(username, plp.gameState[index], plp.userID[index]));

                    }
                    index++;
                }

            /* Removing players no longer online. We also need to do it this way to avoid Concurrent Access exceptions */
                for (Player player : chess.playerList) {
                    boolean found = false;
                    for (String username : plp.usernames) {
                        if (username.equals(player.name))
                            found = true;
                    }
                    if (!found) {
                        removal.add(player);
                    }
                }
            }
            Array<Challenge> rmChallenge = new Array<Challenge>();
            /* Remove all current challenges before removing the player */
            for(Player p : removal)
            {
                for(Challenge c : chess.challenges)
                {
                    if(c.username.equals(p.name))
                    {
                        rmChallenge.add(c);
                    }
                }
            }
            if(rmChallenge.size>0) {
                chess.challenges.removeAll(rmChallenge, true);
            }
            if(removal.size>0) {
                synchronized (chess.playerList) {
                    chess.playerList.removeAll(removal, true);
                }
            }

            chess.challengeFlag = true;
            chess.playerListFlag = true;
            if(chess.getScreen() instanceof MenuScreen)
            {
                ((MenuScreen) chess.getScreen()).titleUpdate = true;
            }
        }
        else if(object instanceof ChallengePacket)
        {
            ChallengePacket challenge = (ChallengePacket)object;
            chess.addChallenge(challenge);
            chess.challengeFlag=true;
        }
        else if(object instanceof CreateGamePacket)
        {
            CreateGamePacket packet = (CreateGamePacket)object;
            synchronized (chess.gameRooms) {
                chess.gameRooms.add(new GameRoom(packet.gameID, packet.p1, packet.p2, chess, packet.white));

            }
        }
    }

    public void disconnected(Connection connection)
    {
        System.err.println("We've disconnected from the server");
    }

}
