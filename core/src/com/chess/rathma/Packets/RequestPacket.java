package com.chess.rathma.Packets;

/**
 * Created by rathma on 5/22/16.
 */
public class RequestPacket {

/* **** We would have to write our own serialisation for enums to work. We'll do that another time.
    public enum REQUEST{
        PLAYERLIST //Will request all active players that are challengable.
    }


    public REQUEST request;
    */


    /*
       0 = PLAYERLIST
       1 = UserID Request
       3 = Full Board Position

     */
    public int request;
    public RequestPacket(){}
    public RequestPacket(int request)
    {
        this.request=request;
    }
}
