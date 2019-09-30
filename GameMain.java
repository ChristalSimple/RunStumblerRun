package com.fugi.balanel.fugi;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameMain extends Game
{
	SpriteBatch batch;
    AssetManager manager = new AssetManager();
    private FPSLogger fpsLogger;
    OrthographicCamera camera;
    private static final int screenWidth = 800;
    private static final int screenHeight = 480;
    Viewport viewport;
    TextureAtlas menuAtlas;
    TextureAtlas gameAtlas;
    TextureAtlas uiStageAtlas;
    FreeTypeFontGenerator fontGenerator;
    FreeTypeFontParameter fontParameter;
    Music bgMusic;
    float volume;
    BitmapFont font;
    int[] scores = new int[4];

    public GameMain()
    {
        fpsLogger = new FPSLogger();
        camera = new OrthographicCamera();
        camera.position.set(screenWidth / 2, screenHeight / 2, 0);
        viewport = new FitViewport(screenWidth, screenHeight, camera);
        fontParameter = new FreeTypeFontParameter();
        fontParameter.size = 26;
        fontParameter.color = Color.WHITE;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.borderWidth = 3;
        fontParameter.shadowColor = Color.BLACK;
        fontParameter.shadowOffsetX = 1;
        fontParameter.shadowOffsetY = 1;
    }
	
	@Override
	public void create ()
	{
		batch = new SpriteBatch();
        setScreen(new LoadingScreen(this));
	}

	@Override
	public void render ()
    {
        fpsLogger.log();
        super.render();
	}

	@Override
    public void resize(int width, int height)
    {
        viewport.update(width, height);
    }

	@Override
	public void dispose ()
    {
		batch.dispose();
        manager.dispose();
        font.dispose();
        fontGenerator.dispose();
	}

}
