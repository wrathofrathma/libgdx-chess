package com.chess.rathma.Packets;

/**
 * Created by rathma on 5/22/16.
 */

public class MovePacket {
    //Only from the server. Never outgoing.
    public MovePacket(int gameID, int boardID, int x1, int y1, int x2, int y2,boolean turnswitch)
    {
        this.boardID = boardID;
        this.x1 = x1;
        this.x2 = x2;
        this.y2 = y2;
        this.y1 = y1;
        this.gameID=gameID;
        this.turnswitch = turnswitch;
    }
    public MovePacket(int gameID, int boardID, int x1, int y1, int x2, int y2,int playerID)
    {
        this.boardID=boardID;
        this.playerID = playerID;
        this.x1 = x1;
        this.x2 = x2;
        this.y2 = y2;
        this.y1 = y1;
        this.gameID=gameID;
    }
    public int boardID;
    public int playerID;
    public MovePacket(){}
    //String structure : LOC1 LOC2
    public int x1, y1;
    public int x2, y2;
    public int gameID;
    public boolean turnswitch=true;

    @Override
    public String toString() {
        return "("+x1+","+y1+") to ("+x2+","+y2+")";
    }
}
