package com.chess.rathma;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by rathma on 5/22/16.
 */
public class TextLabel extends Actor {
    public String text;
    int id; //Hack to differentiate text labels without further extending the class
    private BitmapFont font;
    public float height;
    public float width;
    public static GlyphLayout glyphLayout;

    public int getId()
    {
        return id;
    }
    public void setId(int i)
    {
        id=i;
    }
    /* 0 = UI Labels
     * 1 = Player label ID
     * 2 = Challenge Label ID
     * 3 = Kill screen label
     */


    private void init(float xi, float yi){

        glyphLayout = new GlyphLayout();
        if(text!=null && font!=null)
            glyphLayout.setText(font,text);
        height = glyphLayout.height;
        width = glyphLayout.width;
        setTouchable(Touchable.enabled);
        setBounds(xi,yi,glyphLayout.width,glyphLayout.height);
    }
    public TextLabel(String text, BitmapFont font, int id)
    {
        this.text = text;
        this.font = font;
        this.id = id;
        init(0,0);
    }
    public TextLabel(String text, BitmapFont font, float x, float y, int id)
    {
        this.text = text;
        this.font = font;
        this.id = id;
        init(x,y);

    }

    public void clicked(){}
    public void clicked(final Chess chess){}
    public void clicked(final Chess chess, int index)
    {


    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        font.draw(batch, text,getX(),getY());

    }
}
