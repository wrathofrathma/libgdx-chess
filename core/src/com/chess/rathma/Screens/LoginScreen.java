package com.chess.rathma.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.chess.rathma.Chess;
import com.chess.rathma.Packets.IdentPacket;
import com.chess.rathma.Packets.PublicKeyPacket;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginScreen implements Screen {
    private Chess chess;
    private Skin skin;
    private Table table;
    private Stage stage;
    public boolean lock=true;
    public LoginScreen(Chess chess)
    {
        this.chess = chess;
    }
    Label statusLabel;
    TextField userTextField;
    TextField passwordTextField;
    TextField serverTextField;

    @Override
    public void show() {
        /* Basic things we need */
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("menu.json"));
        table = new Table(skin);
        Gdx.input.setInputProcessor(stage);

        /* Let's change the size of the window, since we don't have much to fill the empty space */
        final Label userLabel = new Label("Username: ", skin);
        Label passwordLabel = new Label("Password: ",skin);
        final Label serverLabel = new Label("Server: ", skin);
        statusLabel = new Label("Disconnected",skin);

        userTextField = new TextField("",skin);
        passwordTextField = new TextField("",skin);
        serverTextField = new TextField("",skin);

        /* Setting up our text fields */
        passwordTextField.setMessageText("Disabled!");
        passwordTextField.setPasswordMode(true);
        passwordTextField.setPasswordCharacter('*');
        passwordTextField.setDisabled(true);
        userTextField.setDisabled(true);
        userTextField.setMessageText("Disabled!");

        serverTextField.setText("Chess Network");

        TextButton connectButton = new TextButton("Connect!", skin);

        /* Setting up our events !*/
        connectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    if(!chess.network.isConnected())
                    {
                        if(serverTextField.getText().equals("Chess Network")) {
                            chess.network.connect(5000, "2601:145:c300:4910:9232:47cc:a8bd:c810", 7667);
                            if (chess.network.isConnected()) {
                                serverTextField.setDisabled(true);
                                chess.network.sendTCP(new PublicKeyPacket(chess.keyModule.getKeys().getPublic().getEncoded()));
                            }
                        }
                        else {
                            chess.network.connect(5000, serverTextField.getText(), 7667);
                            if (chess.network.isConnected()) {
                                serverTextField.setDisabled(true);
                                chess.network.sendTCP(new PublicKeyPacket(chess.keyModule.getKeys().getPublic().getEncoded()));
                            }
                        }
                    }
                    else if(chess.keySet) {
                        try {
                            byte[] username = userTextField.getText().getBytes("utf-8");
                            byte[] encryptedUser = chess.keyModule.encrypt(username, chess.sessionKey);
                            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                            //byte[] password = sha256.digest(passwordTextField.getText().getBytes("utf-8"));
                            byte[] password = passwordTextField.getText().getBytes("utf-8");
                            System.out.println("Password hash: " + password + " || " + password.hashCode());

                            byte[] encryptedPassword = chess.keyModule.encrypt(password,chess.sessionKey);

                            IdentPacket packet = new IdentPacket(encryptedUser,encryptedPassword);
                            chess.network.sendTCP(packet);
                        } catch (NoSuchAlgorithmException e)
                        {
                            e.printStackTrace();
                        }

                    }
                    else {
                        chess.network.sendTCP(new PublicKeyPacket(chess.keyModule.getKeys().getPublic().getEncoded()));
                    }

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        serverTextField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if((c== '\n' || c=='\r') && !textField.getText().equals("")) {
                    try {
                        if(!chess.network.isConnected())
                        {
                            if(serverTextField.getText().equals("Chess Network")) {
                                chess.network.connect(5000, "2601:145:c300:4910:9232:47cc:a8bd:c810", 7667);
                                if (chess.network.isConnected()) {
                                    serverTextField.setDisabled(true);
                                    chess.network.sendTCP(new PublicKeyPacket(chess.keyModule.getKeys().getPublic().getEncoded()));
                                }
                            }
                            else {
                                chess.network.connect(5000, serverTextField.getText(), 7667);
                                if (chess.network.isConnected()) {
                                    serverTextField.setDisabled(true);
                                    chess.network.sendTCP(new PublicKeyPacket(chess.keyModule.getKeys().getPublic().getEncoded()));
                                }
                            }
                        }
                        else if(chess.keySet) {
                            try {
                                byte[] username = userTextField.getText().getBytes("utf-8");
                                byte[] encryptedUser = chess.keyModule.encrypt(username, chess.sessionKey);
                                MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                                //byte[] password = sha256.digest(passwordTextField.getText().getBytes("utf-8"));
                                byte[] password = passwordTextField.getText().getBytes("utf-8");

                                System.out.println("Password hash: " + password + " || " + password.hashCode());

                                byte[] encryptedPassword = chess.keyModule.encrypt(password,chess.sessionKey);
                                System.out.println("Password hash: " + encryptedPassword + " || " + encryptedPassword.hashCode());

                                IdentPacket packet = new IdentPacket(encryptedUser,encryptedPassword);
                                chess.network.sendTCP(packet);
                            } catch (NoSuchAlgorithmException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else {
                            chess.network.sendTCP(new PublicKeyPacket(chess.keyModule.getKeys().getPublic().getEncoded()));
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        userTextField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if((c== '\n' || c=='\r') && !textField.getText().equals("")) {
                    try {
                        if(!chess.network.isConnected())
                        {
                            if(serverTextField.getText().equals("Chess Network")) {
                                chess.network.connect(5000, "2601:145:c300:4910:9232:47cc:a8bd:c810", 7667);
                                if (chess.network.isConnected()) {
                                    serverTextField.setDisabled(true);
                                    chess.network.sendTCP(new PublicKeyPacket(chess.keyModule.getKeys().getPublic().getEncoded()));
                                }
                            }
                            else {
                                chess.network.connect(5000, serverTextField.getText(), 7667);
                                if (chess.network.isConnected()) {
                                    serverTextField.setDisabled(true);
                                    chess.network.sendTCP(new PublicKeyPacket(chess.keyModule.getKeys().getPublic().getEncoded()));
                                }
                            }
                        }
                        else {
                            try {
                                byte[] username = userTextField.getText().getBytes("utf-8");
                                byte[] encryptedUser = chess.keyModule.encrypt(username, chess.sessionKey);
                                MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                                //byte[] password = sha256.digest(passwordTextField.getText().getBytes("utf-8"));
                                byte[] password = passwordTextField.getText().getBytes("utf-8");
                                System.out.println("Password hash: " + password + " || " + password.hashCode());

                                byte[] encryptedPassword = chess.keyModule.encrypt(password,chess.sessionKey);
                                System.out.println("Password hash: " + encryptedPassword + " || " + encryptedPassword.hashCode());


                                IdentPacket packet = new IdentPacket(encryptedUser,encryptedPassword);
                                chess.network.sendTCP(packet);
                            } catch (NoSuchAlgorithmException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });


        stage.addActor(table);
        table.setFillParent(true);
        table.pad(10);
        table.setBackground(skin.getDrawable("background"));
        /* Organisation/Layout */
        table.align(Align.topLeft);

        table.add(serverLabel)
            .align(Align.left).padTop(5).padBottom(5)
            .padRight(10);
        table.add(serverTextField)
            .padTop(5).padBottom(5)
            .align(Align.left)
            .expandX();
        table.row()
                .expandX();

        table.add(userLabel)
            .align(Align.left)
            .padRight(10).padTop(5).padBottom(5);
        table.add(userTextField)
            .align(Align.left)
            .padTop(5).padBottom(5)
            .expandX();

        table.row()
                .expandX();

        table.add(passwordLabel)
            .align(Align.left)
            .padTop(5).padBottom(5)
            .padRight(10);
        table.add(passwordTextField)
            .expandX()
            .padTop(5).padBottom(5)
            .align(Align.left);

        table.row().expandX();

        table.add(connectButton).align(Align.center)
            .padTop(5)
            .padBottom(5)
            .expandX()
            .colspan(2);
        table.row().expandY().expandX();
        table.add(statusLabel).align(Align.bottom).expandX().colspan(2);

    }

    public void connected(){
        statusLabel.setText("Connected!");
    }
    public void keysExchanged()
    {
        statusLabel.setText("Keys exchanged!");
        userTextField.setDisabled(false);
        userTextField.setMessageText("");
        passwordTextField.setDisabled(false);
        passwordTextField.setMessageText("");

    }
    @Override
    public void render(float delta) {
        stage.act();
        if(chess.network.isConnected() && lock==false)
        {
            this.dispose();
            chess.setScreen(chess.menuScreen);
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        chess.batch.begin();
        stage.draw();
        chess.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
