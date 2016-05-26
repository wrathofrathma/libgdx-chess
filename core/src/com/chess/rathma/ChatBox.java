package com.chess.rathma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.chess.rathma.Packets.MessagePacket;


/**
 * Needs to house a scrollpane, which we'll add our messages to
 * Also needs a text field.
 *
 */
public class ChatBox extends WidgetGroup{
    /* Containers */
    private Array<MessageLabel> messageArray;
    public List messageList;
    public ScrollPane scrollPane;
    public TextField textField;
    public Table table;
    public Skin chatSkin;
    public final Chess chess;
    public ChatBox(final Chess chess){
        /* Initialising all of our chat stuffs */
        this.chess = chess;
        table = new Table();
        chatSkin = new Skin(Gdx.files.internal("style.json"));
        messageArray = new Array<MessageLabel>();


        messageList = new List(chatSkin);
        scrollPane = new ScrollPane(messageList);
        scrollPane.setScrollingDisabled(true, false);

        /* Setting up our textField */
        textField = new TextField("", chatSkin);

        /* Listening for when a message is sent! */
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if((c== '\n' || c=='\r') && !textField.getText().equals("")) {
                    chess.network.sendTCP(new MessagePacket(chess.userID, textField.getText()));
                    addMessage(chess.nickname + ": " +textField.getText());
                    textField.setText("");
                }
            }
        });


        //table.setDebug(true);
        /* Organising everything */
        addActor(table);
        table.setFillParent(true);
        table.add(scrollPane).expandX().padBottom(10).expandY().minHeight(150).minWidth(300).prefWidth(504).align(Align.bottomLeft);
        table.row().expandX();
        table.add(textField).expandX().align(Align.bottomLeft).minWidth(300).prefWidth(504);

    }


    /* Whenever a message is added, we need to add it to our array messageArray, then add them to our messageList  */
    public synchronized void addMessage(String message)
    {
        System.out.println("Adding to message queue: " + message);
        messageArray.add(new MessageLabel(message,chatSkin));
        messageList.setItems(messageArray);

        /* On receive message, if the scroll is at 100% to the bottom we want to keep it snapped there */

        if(scrollPane.isScrollY()) {
            scrollPane.layout();
            scrollPane.setScrollY(scrollPane.getMaxY());
        }
    }
    public void addMessage(MessagePacket messagePacket)
    {
        for(Player player : chess.playerList)
        {
            if(player.id == messagePacket.userid)
            {
                this.addMessage(player.name + ": " + messagePacket.message);
            }
        }
    }
}
