package com.chess.rathma.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.chess.rathma.Chess;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 512;
		config.width = 300;
        config.resizable=false;
		new LwjglApplication(new Chess(), config);
	}
}
