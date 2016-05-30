package com.chess.rathma.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.chess.rathma.Chess;
import com.chess.rathma.Packets.IdentPacket;

import java.io.IOException;


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

    @Override
    public void show() {
        /* Basic things we need */
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("menu.json"));
        table = new Table(skin);
        Gdx.input.setInputProcessor(stage);

        /* Let's change the size of the window, since we don't have much to fill the empty space */
        Label userLabel = new Label("Username: ", skin);
        Label passwordLabel = new Label("Password: ",skin);
        final Label serverLabel = new Label("Server: ", skin);

        final TextField userTextField = new TextField("",skin);
        final TextField passwordTextField = new TextField("",skin);
        passwordTextField.setMessageText("Disabled!");
        final TextField serverTextField = new TextField("",skin);

        passwordTextField.setPasswordMode(true);
        passwordTextField.setPasswordCharacter('*');
        passwordTextField.setDisabled(true);

        TextButton connectButton = new TextButton("Connect!", skin);

        /* Setting up our events !*/
        connectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    if(!chess.network.isConnected())
                    {
                        chess.network.connect(5000,serverTextField.getText(),7667);
                        if(chess.network.isConnected()) {
                            serverTextField.setDisabled(true);
                            chess.network.sendTCP(new IdentPacket(userTextField.getText()));
                        }
                    }
                    else {
                        chess.network.sendTCP(new IdentPacket(userTextField.getText()));
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
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
    }

    @Override
    public void render(float delta) {
        stage.act();
        if(chess.network.isConnected() && lock==false)
        {
            this.dispose();
            chess.setScreen(new MenuScreen(chess));
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
