package com.chess.rathma;

/**
 * Should handle setting up a socket, connecting to the default address & port. Incoming/outgoing messages.
 *
 * We should create a data type for network messages as another class as well
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;


public class Network {
    public Socket socket; //We all know what a socket is. The basic interface to narnia.
    public String addr="localhost"; //TODO change address later.
    public int portno=38383; //TODO change portno later.
    public SocketHints hints; //I thought about just passing over this, but this might be useful.
    public Net.Protocol protocol; //I have no clue what this is, but it keeps bitching at me to use this.
    public Network()
    {
        socket = Gdx.net.newClientSocket(protocol.TCP, addr, portno, hints);
    }

}
