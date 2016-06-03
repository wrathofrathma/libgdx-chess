package com.chess.rathma;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.chess.rathma.Packets.GameEndPacket;
import com.chess.rathma.Packets.MovePacket;
import com.chess.rathma.Screens.GameScreen;

/**
 * Holds the absolute board state(s)
 * Computes move logic
 * Contains
 **** player IDs
 *  * usernames
 *  * Player colour - Determines if we flip the coordinates
 *  *
 *
 */
public class GameRoom {
    public int gameID;
    //TODO change board to be an array of boards to account for bughouse chess. We need board IDs
    public int[][] board;
    //TODO change player IDs and usernames to an Array<Player> structure.
    public int p1, p2; //Player IDs
    public String player1, player2; //Player usernames, for TextLabel purposes.
    public Chess chess;
    public Array<MovePacket> moves;
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

    public String toString(){
        return "("+gameID+") "+player1 + " vs " + player2;
    }

    public GameRoom()
    {
        board = new int[8][8];
        moves=new Array<MovePacket>();
    }
    public GameRoom(int gameID, int p1, int p2, Chess chess, boolean colour){
        this.gameID = gameID;
        this.p1 = p1;
        this.p2 = p2;
        this.chess = chess;
        moves = new Array<MovePacket>();
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
        state = GameState.ENDFLAG;
        gameEnd = packet;
    }

    /* Absolute command from the server */
    public void changeID(int x, int y, int newID)
    {
        //Still should check the bounds
        if((x<=7 && x>=0) && (y<=7 && y>=0))
        {
            board[x][y]=newID;
        }
    }

    public boolean isValidMove(Piece piece, int newx, int newy){
        /* Will check simple movement logic */
        return true;
    }


    public Piece getPiece(int currentX, int currentY)
    {
        if(chess.getScreen() instanceof GameScreen) {
            for (Actor actor : chess.gameScreen.board.pieces.getChildren()) {
                if (actor instanceof Piece) {
                    Piece p = (Piece) actor;
             /* Find piece */
                    if (p.locx == currentX && p.locy == currentY) {
                        return p;
                    }
                }
            }
            System.err.println("Piece not found.");
        }
        return null; //Should hopefully never fire.
    }
    public void tryPromote(Piece piece)
    {


    }
    /* Should only be called by the server - The instructions are absolute & the board WILL be changed to this
     * Probably need another function or packet for adding/modifying a piece value from the server(pawn promotion or bughouse/crazyhouse chess dropping pieces)
      * */
    /* Expected input: Piece's old X & Y, new coordinates */
    public void Move(Piece piece, MovePacket movePacket, Array<Actor> actors)
    {

        board[movePacket.x2][movePacket.y2]=board[piece.locx][piece.locy];
        /* Absolute position */
        for(Actor actor : actors)
        {
            if(actor instanceof Piece)
            {
                Piece p = (Piece)actor;
                if(p.locx==movePacket.x2 && p.locy == movePacket.y2)
                {
                    //Remove piece from stage.
                    p.remove();
                }
            }
        }
        piece.locx = movePacket.x2;
        piece.locy = movePacket.y2;
        /* Board position */
        if(piece.gameRoom.colour==COLOUR.BLACK) {
            piece.setX(piece.grabBlackX());
            piece.setY(piece.grabBlackY());
        }
        else {
            piece.setY(piece.locy * piece.boardMultiplier);
            piece.setX(piece.locx * piece.boardMultiplier);
        }
        moves.add(movePacket);
    }
    public void Move(MovePacket movePacket)
    {
        if(movePacket.gameID==gameID) {
            board[movePacket.x2][movePacket.y2] = board[movePacket.x1][movePacket.y1];
            board[movePacket.x1][movePacket.y1] = 12;
        }
        moves.add(movePacket);
    }



    /* Piece current and destination locations */
    /* This will be called by the client when we attempt a move */
    public boolean attemptMove(Piece piece, int newx, int newy){
        System.out.println("Attempting move: " + newx + " " + newy);
        if(isValidMove(piece, newx, newy))
        {
            if(piece.gameRoom.colour==COLOUR.BLACK) {
                piece.setY(piece.grabBlackY());
                piece.setX(piece.grabBlackX());
            }
            else {
                piece.setX(piece.locx*piece.boardMultiplier);
                piece.setY(piece.locy * piece.boardMultiplier);
            }
            chess.network.sendTCP(new MovePacket(piece.gameRoom.gameID,piece.chessBoard.boardID,piece.locx, piece.locy, newx, newy,chess.userID));
            return true;
        }
        else
        {
            //Return back to original position & don't fire a packet
            if(piece.gameRoom.colour==COLOUR.BLACK) {
                piece.setY(piece.grabBlackY());
                piece.setX(piece.grabBlackX());
            }
            else {
                piece.setX(piece.locx*piece.boardMultiplier);
                piece.setY(piece.locy * piece.boardMultiplier);
            }
        }

        return true;
    }
}
