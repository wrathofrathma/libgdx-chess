package com.chess.rathma.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.chess.rathma.Chess;
import com.chess.rathma.ChessBoard;

public class GameScreen implements Screen{

    private Chess game;
    private int [][] boardState;
    public Stage stage;
    public Sound moveSound;
    public  GameScreen(Chess game)
    {
        this.game = game;
        boardState = new int[8][8];
        //Receive gamestate from network at this point instead of default state.

        moveSound = Gdx.audio.newSound(Gdx.files.internal("move.mp3"));

        stage = new Stage();
        stage.addActor(new ChessBoard());
    }
    private void boardUpdated()
    {
        moveSound.play();
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        stage.draw();
        game.batch.end();
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
