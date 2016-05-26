package com.chess.rathma.Packets;


/**
 * Created by rathma on 5/25/16.
 */
public class ServerShutdownPacket {
    public ServerShutdownPacket(){}
    public ServerShutdownPacket(String message)
    {
        this.message = message;
    }
    public String message;
}
