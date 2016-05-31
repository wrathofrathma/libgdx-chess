package com.chess.rathma;



/**
 * Created by rathma on 5/30/16.
 */
public class GameLabel{
    public GameRoom gameRoom;
    public GameLabel(GameRoom room){
        this.gameRoom = room;
    }
    public String toString()
    {
        return gameRoom.toString();
    }
}
