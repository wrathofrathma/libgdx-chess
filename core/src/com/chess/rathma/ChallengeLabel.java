package com.chess.rathma;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by rathma on 5/25/16.
 */
public class ChallengeLabel{
    public Challenge challenge;
    public ChallengeLabel(Challenge challenge)
    {
        this.challenge = challenge;
    }
    public String toString()
    {
        return challenge.username;
    }

}
