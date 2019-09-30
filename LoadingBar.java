package com.fugi.balanel.fugi;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class LoadingBar extends Actor
{
    Animation animation;
    TextureRegion region;
    float stateTime;

    public LoadingBar(Animation anim)
    {
        this.animation = anim;
        region = (TextureRegion) animation.getKeyFrame(0);
    }

    @Override
    public void act(float delta)
    {
        stateTime += delta;
        region = (TextureRegion) animation.getKeyFrame(stateTime);
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        batch.draw(region, getX(), getY());
    }
}
