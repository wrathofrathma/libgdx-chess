package com.chess.rathma;


import com.chess.rathma.Packets.CreateGamePacket;
import com.chess.rathma.Packets.MessagePacket;
import com.chess.rathma.Packets.MovePacket;


import com.chess.rathma.Packets.PlayerInfoPacket;
import com.chess.rathma.Screens.GameScreen;
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
            System.out.println(mp.message);
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
            System.out.println("Received PlayerInfoPacket");
            PlayerInfoPacket packet = (PlayerInfoPacket)object;
            if(packet.specialID==true)
            {
                chess.userID=packet.userID;
                chess.nickname=packet.username;
            }
        }

    }

    public void disconnected(Connection connection)
    {
        System.err.println("We've disconnected from the server");
    }

}
