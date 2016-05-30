package com.chess.rathma;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by rathma on 5/30/16.
 */
public class ImageListener extends ClickListener {
    public Chess chess;
    public ImageListener (Chess chess)
    {
        this.chess = chess;
    }
}
