package com.chess.rathma;

import com.badlogic.gdx.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.chess.rathma.Screens.GameScreen;

public class Chess extends Game{
	public SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
        setScreen(new GameScreen(this));
	}

	@Override
	public void render () {
        super.render();

	}


}
