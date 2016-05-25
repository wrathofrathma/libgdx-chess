package com.chess.rathma;

/**
 * Finally useful xD
 *
 */
public class Player {
    public boolean inGame=false;
    public Player(){}
    public Player(String n)
    {
        name = n;
    }
    public Player(String n, int i)
    {
        name = n;
        id=i;
    }
    public Player(String n, boolean gamestate)
    {
        inGame = gamestate;
        name = n;
    }
    public Player(String n, boolean gamestate, int userID)
    {
        this.inGame = gamestate;
        this.name = n;
        this.id = userID;
    }
    public String name;
    public int id;
}
