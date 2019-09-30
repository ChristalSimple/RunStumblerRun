package com.fugi.balanel.fugi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;

public class LoadingScreen extends ScreenAdapter
{
    private GameMain game;

    private Stage loadingStage;

    private Image logo;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image loadingBg;
    private Image screenBg;
    private float startX, endX;
    private float percent;

    private Actor loadingBar;

    LoadingScreen(GameMain gm)
    {
        game = gm;
    }

    @Override
    public void show()
    {
        game.manager.load("loading.pack", TextureAtlas.class);
        //Temporary
        game.manager.load("logo.png", Texture.class);
        game.manager.finishLoading();

        loadingStage = new Stage();

        TextureAtlas atlas = game.manager.get("loading.pack", TextureAtlas.class);

        logo = new Image(game.manager.get("logo.png", Texture.class));
        //logo = new Image(atlas.findRegion("libgdx-logo"));
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));
        screenBg = new Image(atlas.findRegion("screen-bg"));

        Animation anim = new Animation(0.05f, atlas.findRegions("loading-bar-anim"));
        anim.setPlayMode(PlayMode.LOOP_REVERSED);
        loadingBar = new LoadingBar(anim);

        loadingStage.addActor(screenBg);
        loadingStage.addActor(loadingBar);
        loadingStage.addActor(loadingBg);
        loadingStage.addActor(loadingBarHidden);
        loadingStage.addActor(loadingFrame);
        loadingStage.addActor(logo);

        game.manager.load("Menu.pack", TextureAtlas.class);
        game.manager.load("Balanel.pack", TextureAtlas.class);
        game.manager.load("UiStage.pack", TextureAtlas.class);
        game.manager.load("walk_dust", ParticleEffect.class);
        game.manager.load("background_loop.mp3", Music.class);
    }

    @Override
    public void resize(int width, int height)
    {
        Vector2 scaledView = Scaling.fit.apply(800, 480, width, height);
        loadingStage.getViewport().update((int) scaledView.x, (int) scaledView.y, true);

        screenBg.setSize(width, height);

        logo.setX((width - logo.getWidth()) / 2);
        logo.setY((height - logo.getHeight()) / 2 + 75);

        loadingFrame.setX((loadingStage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY((loadingStage.getHeight() - loadingFrame.getHeight()) / 2 - 50);

        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);

        startX = loadingBarHidden.getX();
        endX = 440;

        loadingBg.setSize(450, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        if(game.manager.update())
        {
            game.menuAtlas = game.manager.get("Menu.pack", TextureAtlas.class);
            game.gameAtlas = game.manager.get("Balanel.pack", TextureAtlas.class);
            game.uiStageAtlas = game.manager.get("UiStage.pack", TextureAtlas.class);
            game.bgMusic = game.manager.get("background_loop.mp3", Music.class);
            game.fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("joan.ttf"));
            game.font = game.fontGenerator.generateFont(game.fontParameter);
            game.setScreen(new MenuScreen(game));
        }


        percent = Interpolation.linear.apply(percent, game.manager.getProgress(), 0.1f);

        loadingBarHidden.setX(startX + endX * percent);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setWidth(450 - 450 * percent);
        loadingBg.invalidate();

        loadingStage.act();
        loadingStage.draw();
    }

    @Override
    public void hide()
    {
        game.manager.unload("loading.pack");
    }

    @Override
    public void dispose()
    {
        loadingStage.dispose();
    }
}
