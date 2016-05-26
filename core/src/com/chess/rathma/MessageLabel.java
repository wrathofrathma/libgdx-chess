package com.chess.rathma;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by rathma on 5/26/16.
 */
public class MessageLabel extends Label{
    public MessageLabel(CharSequence text, Skin skin) {
        super(text, skin);
        setWrap(true);
    }

    public MessageLabel(CharSequence text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public MessageLabel(CharSequence text, Skin skin, String fontName, Color color) {
        super(text, skin, fontName, color);
    }

    public MessageLabel(CharSequence text, Skin skin, String fontName, String colorName) {
        super(text, skin, fontName, colorName);
    }

    public MessageLabel(CharSequence text, LabelStyle style) {
        super(text, style);
    }

    @Override
    public String toString() {
        return ""+ getText();
    }
}
