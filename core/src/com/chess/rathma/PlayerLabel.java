package com.chess.rathma;

/**
 * Created by rathma on 5/26/16.
 */
public class PlayerLabel {
    public Player player;
    public PlayerLabel(Player player)
    {
        this.player = player;
    }
    public String toString()
    {
        return player.name;
    }
}
