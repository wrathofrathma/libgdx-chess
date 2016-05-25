package com.chess.rathma.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.chess.rathma.Challenge;
import com.chess.rathma.GameRoom;
import com.chess.rathma.Packets.ChallengePacket;
import com.chess.rathma.Packets.CreateGamePacket;
import com.chess.rathma.Packets.PlayerListPacket;
import com.chess.rathma.Player;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * Created by rathma on 5/22/16.
 */
public class MenuScreenListener extends Listener {
    private MenuScreen menuScreen;
    public MenuScreenListener(MenuScreen menuScreen)
    {
        this.menuScreen = menuScreen;
    }
    public void received(Connection connection, Object object)
    {
        if(object instanceof PlayerListPacket)
        {
            Array<Player> removal = new Array<Player>();
            PlayerListPacket plp = (PlayerListPacket) object;
            boolean exists=false;
            int index=0; //Tracks where the gameState boolean is
            synchronized (menuScreen.chess.playerList) {
            /* Adding users not found in the local list */
                for (String username : plp.usernames) {
                    exists = false;
                    for (Player player : menuScreen.chess.playerList) {
                        if (player.name.equals(username))
                            exists = true;
                    }
                    if (!exists) {
                        menuScreen.chess.playerList.add(new Player(username, plp.gameState[index], plp.userID[index]));

                    }
                    index++;
                }

            /* Removing players no longer online. We also need to do it this way to avoid Concurrent Access exceptions */
                for (Player player : menuScreen.chess.playerList) {
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
                for(Challenge c : menuScreen.challenges)
                {
                    if(c.username.equals(p.name))
                    {
                        rmChallenge.add(c);
                    }
                }
            }
            if(rmChallenge.size>0) {
                menuScreen.challenges.removeAll(rmChallenge, true);
            }
            if(removal.size>0) {
                synchronized (menuScreen.chess.playerList) {
                    menuScreen.chess.playerList.removeAll(removal, true);
                }
            }

            menuScreen.drawChallenges();
            menuScreen.updatePlayerList();
            menuScreen.titleUpdate=true;
        }
        else if(object instanceof ChallengePacket)
        {
            ChallengePacket challenge = (ChallengePacket)object;
            menuScreen.challenged(challenge);
        }
        else if(object instanceof CreateGamePacket)
        {
            CreateGamePacket packet = (CreateGamePacket)object;
            /* Perhaps I should create a new gameRoom then have that switch screens....*/
            menuScreen.chess.gameRooms.add(new GameRoom(packet.gameID, packet.p1, packet.p2, menuScreen.chess, packet.white));
            //menuScreen.chess.startGame(packet);
        }
    }
}
