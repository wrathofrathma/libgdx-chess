package com.chess.rathma;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Chess extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter param;
    String text;
    BitmapFont font12;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("chessboard544.png"));
        generator = new FreeTypeFontGenerator(Gdx.files.internal("TTF/SourceCodePro-Regular.ttf"));
        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size=12;
        param.color = Color.WHITE;
        font12 = generator.generateFont(param);
        text = "Hallo world";

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
        font12.draw(batch, text, 20,20);
		batch.end();
	}
}
