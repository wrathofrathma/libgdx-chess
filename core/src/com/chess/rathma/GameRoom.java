package com.chess.rathma;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.chess.rathma.Packets.GameEndPacket;
import com.chess.rathma.Packets.MovePacket;
import com.chess.rathma.Screens.GameScreen;

/**
 * This will hold both players information that is shared by the server(ELO, colour, etc)
 *
 * Should also hold the individual boardstates so you can have separate games going.
 * and an ID for the game room.
 *
 *
 */
public class GameRoom {
    public int gameID;
    public int[][] board;
    public int p1, p2; //Player IDs
    public String player1, player2; //Player usernames, for TextLabel purposes.
    private Chess chess;
    public GameEndPacket gameEnd;
    public enum COLOUR {
        WHITE,
        BLACK
    }
    public enum GameState{
        ONGOING,
        COMPLETED, //Set after ENDFLAG state while waiting to destroy screen
        ENDFLAG, //When receiving GameEndPacket this flag is set
        DESTROY
    }
    public GameState state;
    public COLOUR colour;
    public GameRoom()
    {
        board = new int[8][8];
    }
    public GameRoom(int gameID, int p1, int p2, Chess chess, boolean colour){
        this.gameID = gameID;
        this.p1 = p1;
        this.p2 = p2;
        this.chess = chess;
        if(colour)
        {
            this.colour=COLOUR.WHITE;
        }
        else
            this.colour=COLOUR.BLACK;
        state=GameState.ONGOING;
    }

    /* Received GameEndPacket - Set game end flag */
    public void endGame(GameEndPacket packet)
    {
   //     System.out.println("Game over!");
     //   System.out.println(packet.winnerUsername + " wins on condition: " + packet.condition);
        System.out.println("In endGame()");
        state = GameState.ENDFLAG;
        gameEnd = packet;

    }


    public boolean isValidMove(Piece piece, int newx, int newy){
        /* Will check simple movement logic */
        return true;
    }


    public Piece getPiece(int currentX, int currentY)
    {
        for(Actor actor : ((GameScreen)chess.getScreen()).stage.getActors())
        {
            if(actor instanceof Piece)
            {
                Piece p = (Piece)actor;
             /* Find piece */
                if(p.locx == currentX && p.locy==currentY) {
                    return p;
                }
            }
        }
        System.err.println("Piece not found.");
        return new Piece(); //Should hopefully never fire.
    }

    public void Move(Piece piece, int newx, int newy, Array<Actor> actors)
    {
        board[newx][newy]=board[piece.locx][piece.locy];
        /* Absolute position */
        for(Actor actor : actors)
        {
            if(actor instanceof Piece)
            {
                Piece p = (Piece)actor;
                if(p.locx==newx && p.locy == newy)
                {
                    //Remove piece from stage.
                    p.remove();
                }
            }
        }
        piece.locx = newx;
        piece.locy = newy;
        /* Board position */
        if(piece.screen.gameRoom.colour==COLOUR.BLACK)
            piece.setY(piece.grabBlackY());
        else
            piece.setY(piece.locy*68);
        piece.setX(piece.locx*68);

    }
    public boolean attemptMove(Piece piece, int newx, int newy){
        /* if isValidMove(piece) returns true, we'll check with the server. */
        //Let's unpack the origin position & the new position

        if(isValidMove(piece, newx, newy))
        {
            piece.setX(piece.locx*68);
            if(piece.screen.gameRoom.colour==COLOUR.BLACK)
                piece.setY(piece.grabBlackY());
            else
                piece.setY(piece.locy*68);
            chess.network.sendTCP(new MovePacket(piece.screen.activeGameID,piece.locx, piece.locy, newx, newy,chess.userID));
            return true;
        }
        else
        {
            //Return back to original position & don't fire a packet
            piece.setX(piece.locx*68);
            if(piece.screen.gameRoom.colour==COLOUR.BLACK)
                piece.setY(piece.grabBlackY());
            else
                piece.setY(piece.locy*68);
        }

        return true;
    }
}
