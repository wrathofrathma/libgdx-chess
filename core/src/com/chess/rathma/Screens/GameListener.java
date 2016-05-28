package com.chess.rathma.Screens;

import com.badlogic.gdx.Game;
import com.chess.rathma.ChessBoard;
import com.chess.rathma.GameRoom;
import com.chess.rathma.Packets.*;
import com.chess.rathma.Piece;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * Created by rathma on 5/23/16.
 */
public class GameListener extends Listener{
    public GameScreen screen;
    public GameListener(GameScreen screen)
    {
        this.screen = screen;
    }

    public void received(Connection connection, Object object) {
        /* We have to handle this here since we need access to the specific screen to trigger events - Fuck polling*/
        if(object instanceof BoardPosition)
        {
            System.out.println("Board position packet received");
            BoardPosition packet = (BoardPosition)object;
            //First we can check if the activeGameID fits, if not we search for it in the array. We can separate the triggering of events this way.

            //TODO check for boardID
            if(packet.gameID==screen.activeGameID)
            {
                System.out.println("Found Game ID");
                /* Trigger screen events */
                screen.gameRoom.board = packet.board;
                screen.board.spawnPieces();
            }
            else
            {
                System.out.println("Searching for game ID");
                for(GameRoom room : screen.chess.gameRooms)
                {
                    if(room.gameID == packet.gameID)
                    {
                        room.board = packet.board;
                        screen.board.spawnPieces();
                    }
                }
            }
        }
        else if(object instanceof MovePacket)
        {
            MovePacket packet = (MovePacket)object;
            //Handle move specific tasks, not quite there yet.
            if(packet.turnswitch)
            {
                //Toggle turn
            }
            /* Server commands are absolute! */
            Piece piece = screen.gameRoom.getPiece(packet.x1,packet.y1);
            if(piece.pieceID!=-1) {
                screen.gameRoom.Move(piece, packet.x2, packet.y2,screen.board.pieces.getChildren());
                screen.boardUpdated();
            }
            else
                System.err.println("Couldn't find the piece");
        }
        else if(object instanceof GameEndPacket)
        {
            System.out.println("GameEndPacket!");
            GameEndPacket packet = (GameEndPacket)object;
            if(packet.endbit)
            {
                /* Find game & end*/
                for(GameRoom room : screen.chess.gameRooms)
                {
                    if(room.gameID==packet.gameID)
                    {
                      //End this specific game.
                        room.endGame(packet);
                    }
                }
            }
        }
        else if(object instanceof PromotionAccept)
        {
            System.out.println("Received promotionaccept");
            PromotionAccept packet = (PromotionAccept) object;
            if(packet.gameID==screen.activeGameID)
            {
                screen.board.releasePromotion();
                for(GameRoom room : screen.chess.gameRooms)
                {
                    if(room.gameID==packet.gameID)
                    {
                        room.changeID(packet.pieceX,packet.pieceY,packet.newID);
                        screen.board.promotionUpdate(packet.pieceX,packet.pieceY);
                    }
                }
            }
            else
            {
                System.err.println("What the fuck gameID did you send in PromotionAccept?");
            }
        }
        else if(object instanceof PromotionPacket)
        {
            PromotionPacket packet = (PromotionPacket)object;
            if(screen.board.gameRoom.gameID==packet.gameID) {
                screen.board.promotionLock = packet.pawnx;
            }
        }
    }
}
