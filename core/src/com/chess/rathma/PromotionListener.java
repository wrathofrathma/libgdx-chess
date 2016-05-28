package com.chess.rathma;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by rathma on 5/27/16.
 */
public class PromotionListener extends ClickListener {
    public int pawnX;

    public PromotionListener(int pawnX)
    {
        this.pawnX = pawnX;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);


    }
}
