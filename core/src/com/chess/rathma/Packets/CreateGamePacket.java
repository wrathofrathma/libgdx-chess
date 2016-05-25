package com.chess.rathma.Packets;

/**
 * Creates a new GameRoom.
 */
public class CreateGamePacket {
    public int gameID;
    public String player1;
    public String player2;
    public boolean white;
    public int p1;
    public int p2;

    public CreateGamePacket(){}
    public CreateGamePacket(int gameID, int player1ID, int player2ID)
    {
        this.gameID = gameID;
        this.p1=player1ID;
        this.p2=player2ID;
    }
}
