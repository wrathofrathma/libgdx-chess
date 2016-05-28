package com.chess.rathma;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.chess.rathma.Packets.MessagePacket;


/**
 * Needs to house a scrollpane, which we'll add our messages to
 * Also needs a text field.
 *
 */
public class ChatBox extends WidgetGroup{
    /* Containers */
    public ScrollPane scrollPane;
    public TextField textField;
    public Table container; //Our primary table for organising the elements of our chat UI
    public Skin chatSkin;
    public Table chatTable; //The table nested inside of our scroll pane to display our images. It's a pain in the dick, but it's the best way.

    public final Chess chess;
    public ChatBox(final Chess chess){
        /* Initialising all of our chat stuffs */
        this.chess = chess;
        container = new Table();
        chatTable = new Table();
        chatSkin = new Skin(Gdx.files.internal("chatStyle.json"));
        scrollPane = new ScrollPane(chatTable);

        /* We want to disable scrolling along X since we're going to figure out this word wrapping */
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

        /* Organising everything */
        addActor(container);
        container.setFillParent(true);
        container.add(scrollPane)
                .expandX().expandY()
                .padBottom(10)
                .minHeight(150)
                .minWidth(560)
                .maxHeight(250)
                .maxWidth(1200) //This is mostly for if we decide to expand during bughouse.
                .prefWidth(560)
                .align(Align.bottomLeft)
                .prefHeight(200);
        container.row().expandX();
        container.add(textField)
                .expandX()
                .align(Align.bottomLeft)
                .minWidth(560)
                .prefWidth(560);

        /* Setting up our chatTable */
        chatTable.setWidth(scrollPane.getPrefWidth());
        chatTable.setFillParent(true);
        chatTable.align(Align.bottomLeft);



    }


    /* Whenever a message is added, we need to add it to our array messageArray, then add them to our messageList  */
    public synchronized void addMessage(String message)
    {

        Label messageLabel = new Label(message,chatSkin);
        messageLabel.setWrap(true);
        //chatTable.row().minHeight(20).minWidth(chatTable.getWidth());
        chatTable.row();
        chatTable.add(messageLabel)
                .align(Align.bottomLeft)
                .prefWidth(chatTable.getWidth())
                .prefHeight(messageLabel.getGlyphLayout().height)
                .padTop(1).padBottom(1);


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
