package com.chess.rathma.Packets;

import java.util.Vector;

public class PlayerListPacket {
    /* What we want to include here or send out
     **** Username
      *  UserID
     */
    public Vector<String> usernames;
    public int[] userID;
    public boolean[] gameState;
    public PlayerListPacket(){}
}
