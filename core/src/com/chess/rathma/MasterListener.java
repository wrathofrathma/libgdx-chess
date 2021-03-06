package com.chess.rathma;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Array;
import com.chess.rathma.Packets.*;


import com.chess.rathma.Screens.GameScreen;
import com.chess.rathma.Screens.LoginScreen;
import com.chess.rathma.Screens.MenuScreen;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;

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
                if(chess.getScreen() instanceof MenuScreen)
                    chess.menuScreen.gameAcceptFlag=true;
            }
        }
        else if(object instanceof SecretKeyPacket)
        {
            SecretKeyPacket packet = (SecretKeyPacket)object;
            chess.sessionKey = (SecretKey)chess.keyModule.unwrapSecretKey(packet.key,chess.keyModule.getKeys().getPrivate());
            chess.keySet = true;
            System.out.println("Session key received: " + chess.sessionKey.hashCode());
            if(chess.getScreen() instanceof LoginScreen)
            {
                ((LoginScreen)chess.getScreen()).keysExchanged();
            }
        }
        /* *************************************************************************/
        /* We have to handle this here since we need access to the specific screen to trigger events - Fuck polling*/
        else if(object instanceof BoardPosition)
        {
            BoardPosition packet = (BoardPosition)object;
            //First we can check if the activeGameID fits, if not we search for it in the array. We can separate the triggering of events this way.
            for(GameRoom room : chess.gameRooms) {
                if(room.gameID==packet.gameID) {
                    room.board=packet.board;
                }
            }
            if(chess.getScreen() instanceof GameScreen) {
                if(packet.gameID == ((GameScreen)chess.getScreen()).activeGameID) {
                    ((GameScreen)chess.getScreen()).board.spawnPieces();
                }
            }
        }
        else if(object instanceof MovePacket) {
            MovePacket packet = (MovePacket) object;
            System.out.println("Move packet!");
            /* Server commands are absolute! */
            synchronized (chess.gameRooms) {
                if (chess.getScreen() instanceof GameScreen) {
                    GameScreen screen = (GameScreen) chess.getScreen();
                    /* If it's the active screen, we need to move the sprite */
                    if (screen.activeGameID == packet.gameID) {
                        Piece piece = screen.gameRoom.getPiece(packet.x1, packet.y1);
                        if (piece != null) {
                            screen.gameRoom.Move(piece, packet, screen.board.pieces.getChildren());
                            screen.sidebar.addMove(packet);
                            screen.boardUpdated();
                        } else
                            System.err.println("Couldn't find the piece");
                    } else { //ActiveGameID is not the packet ID.
                        for (GameRoom room : chess.gameRooms) {
                            if (room.gameID == packet.gameID) {
                                room.Move(packet);
                            }
                        }
                    }
                } else { //Active screen is the menu
                    for (GameRoom room : chess.gameRooms) {
                        if (room.gameID == packet.gameID) {
                            room.Move(packet);
                        }
                    }
                }
            }
        }
        else if(object instanceof GameEndPacket)
        {
            System.out.println("GameEndPacket!");
            GameEndPacket packet = (GameEndPacket)object;
            if(packet.endbit)
            {
                /* Find game & end*/
                for(GameRoom room : chess.gameRooms)
                {
                    if(room.gameID==packet.gameID)
                    {
                        room.endGame(packet);
                    }
                }
            }
        }
        else if(object instanceof PromotionAccept)
        {
            PromotionAccept packet = (PromotionAccept) object;
            if(chess.getScreen() instanceof GameScreen) {
                GameScreen screen = (GameScreen)chess.getScreen();
                if(packet.gameID==screen.activeGameID) {
                    screen.board.releasePromotion();
                    screen.gameRoom.changeID(packet.pieceX,packet.pieceY,packet.newID);
                    screen.board.promotionUpdate(packet.pieceX,packet.pieceY);
                }
                else { //Game ID isn't active game ID. But user is in game.
                    synchronized (chess.gameRooms) {
                        for(GameRoom room : chess.gameRooms) {
                            if(room.gameID==packet.gameID) {
                                room.changeID(packet.pieceX,packet.pieceY,packet.newID);
                            }
                        }
                    }
                }
            }
            else { //Player is in the menu
                synchronized (chess.gameRooms) {
                    for(GameRoom room : chess.gameRooms) {
                        if(room.gameID==packet.gameID) {
                            room.changeID(packet.pieceX,packet.pieceY,packet.newID);
                        }
                    }
                }
            }
        }
        else if(object instanceof PromotionPacket)
        {
            PromotionPacket packet = (PromotionPacket)object;
            if(chess.getScreen() instanceof GameScreen)
            {
                GameScreen screen = (GameScreen)chess.getScreen();
                if(screen.board.gameRoom.gameID==packet.gameID)
                {
                    screen.board.promotionLock = packet.pawnx;
                }
            }
        }
    }


    public void disconnected(Connection connection)
    {
        System.err.println("We've disconnected from the server");
    }

}
