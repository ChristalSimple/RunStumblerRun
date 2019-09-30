package com.fugi.balanel.fugi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameStageBackup extends ScreenAdapter
{
    GameMain game;
    private boolean keyHandled;

    Stage uiStage;
    Skin uiSkin;

    InputMultiplexer multiplexer;

    SpriteBatch batch;
    OrthographicCamera camera;

    TextureRegion gameBg;
    float bgOffset = 0;
    TextureRegion road;
    float terrainOffset = 0;

    Animation<TextureRegion> player;
    Animation<TextureRegion> enemy;

    TextureRegion bottle;
    TextureRegion brokenBottle;
    TextureRegion box;

    private static final int JUMP_IMPULSE = 100;
    private static final float TAP_DRAW_TIME_MAX = 1.0f;
    float tapDrawTime;
    float playerAnimTime = 0f;
    float enemyAnimTime = 0f;

    TextureRegion scoreBar;

    float score;

    //Box2D
    Box2DDebugRenderer debugRend;
    private static final boolean BOX2D_DEBUG = true;
    World world;

    //Vector Coordinates
    Vector2 playerPos;
    Vector2 playerDefPos;
    Vector2 enemyPos;
    Vector2 enemyDefPos;
    Vector2 bottlePos;
    Vector2 brokenBottlePos;
    Vector2 boxPos;

    //Box2d Bodies
    Body playerBody;
    Body enemyBody;
    Body bottleBody;
    Body brokenBottleBody;
    Body boxBody;

    Body terrainBody; //No TextureRegion


    ImageButton settingsButton;
    ImageButtonStyle settingsButtonStyle;

    public GameStageBackup (GameMain gm)
    {
        game = gm;

        uiStage = new Stage(game.viewport);

        keyHandled = false;
        Gdx.input.setCatchBackKey(true);

        batch = new SpriteBatch();
        camera = game.camera;
        gameBg = game.gameAtlas.findRegion("game-bg");
        road = game.gameAtlas.findRegion("road");

        player = new Animation(0.2f,
                game.gameAtlas.findRegion("player1"),
                game.gameAtlas.findRegion("player2"),
                game.gameAtlas.findRegion("player3"),
                game.gameAtlas.findRegion("player4"),
                game.gameAtlas.findRegion("player5"),
                game.gameAtlas.findRegion("player6"));

        player.setPlayMode(Animation.PlayMode.LOOP);

        enemy = new Animation(0.1f,
                game.gameAtlas.findRegion("enemy1"),
                game.gameAtlas.findRegion("enemy2"),
                game.gameAtlas.findRegion("enemy3"),
                game.gameAtlas.findRegion("enemy4"),
                game.gameAtlas.findRegion("enemy5"));

        enemy.setPlayMode(Animation.PlayMode.LOOP);

        bottle = game.gameAtlas.findRegion("bottle");
        brokenBottle = game.gameAtlas.findRegion("broken-bottle");
        box = game.gameAtlas.findRegion("box");

        world = new World(new Vector2(0, -98f), true);
        debugRend = new Box2DDebugRenderer();


        playerDefPos = new Vector2();
        playerDefPos.set(225, 57);

        playerPos = new Vector2();
        playerPos.set(playerDefPos.x, playerDefPos.y);

        enemyDefPos = new Vector2();
        enemyDefPos.set(-40, 57);

        enemyPos = new Vector2();
        enemyPos.set(enemyDefPos.x, enemyDefPos.y);

        bottlePos = new Vector2();
        bottlePos.set(600f, 200f);

        brokenBottlePos = new Vector2();
        brokenBottlePos.set(600f, 260f);

        boxPos = new Vector2();
        boxPos.set(600f, 57f);


        playerBody = graphicsToPhysics(player.getKeyFrame(0),
                playerPos,
                BodyDef.BodyType.DynamicBody);


        enemyBody = graphicsToPhysics(enemy.getKeyFrame(0),
                enemyPos,
                BodyDef.BodyType.DynamicBody);


        bottleBody = graphicsToPhysics(bottle,
                                       bottlePos,
                                       BodyDef.BodyType.DynamicBody);

        brokenBottleBody = graphicsToPhysics(brokenBottle,
                                             brokenBottlePos,
                                             BodyDef.BodyType.DynamicBody);

        boxBody = graphicsToPhysics(box,
                                    boxPos,
                                    BodyDef.BodyType.KinematicBody);


        //Terrain Body
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = BodyDef.BodyType.StaticBody;
        boxBodyDef.position.x = -800f + 2400f / 2;
        boxBodyDef.position.y = 0 + 57f / 2;

        terrainBody = world.createBody(boxBodyDef);

        PolygonShape boxPoly = new PolygonShape();
        boxPoly.setAsBox(1600f / 2,
                57f / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxPoly;
        fixtureDef.density = 1;
        fixtureDef.restitution = 0f;

        terrainBody.createFixture(fixtureDef);
        boxPoly.dispose();



        scoreBar = game.gameAtlas.findRegion("scoreBar");


        uiSkin = new Skin(game.gameAtlas);

        settingsButtonStyle = new ImageButtonStyle();
        settingsButtonStyle.imageUp = uiSkin.getDrawable("settings-up");
        settingsButtonStyle.imageDown = uiSkin.getDrawable("settings-down");
        settingsButton = new ImageButton(settingsButtonStyle);

        uiStage.addActor(settingsButton);


        settingsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {

            }
        });

        settingsButton.setPosition(720, 400);

    }

    private Body graphicsToPhysics(TextureRegion region, Vector2 position, BodyDef.BodyType bodyType)
    {
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = position.x + region.getRegionWidth() / 2;
        boxBodyDef.position.y = position.y + region.getRegionHeight() / 2;

        Body boxBody = world.createBody(boxBodyDef);

        PolygonShape boxPoly = new PolygonShape();
        boxPoly.setAsBox(region.getRegionWidth() / 2,
                region.getRegionHeight() / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxPoly;
        fixtureDef.density = 1;
        fixtureDef.restitution = 0f;
        boxBody.createFixture(fixtureDef);

        boxPoly.dispose();
        boxBody.setUserData(region);

        return boxBody;
    }

    private void updateScreen(float delta)
    {
        bgOffset -= 0.4f;
        terrainOffset -= 4f;
        playerAnimTime += delta * 2;
        enemyAnimTime += delta;

        playerPos.set(playerBody.getPosition().x - player.getKeyFrame(0).getRegionWidth() / 2,
                playerBody.getPosition().y - player.getKeyFrame(0).getRegionHeight() / 2);

        enemyPos.set(enemyBody.getPosition().x - enemy.getKeyFrame(0).getRegionWidth() / 2,
                enemyBody.getPosition().y - enemy.getKeyFrame(0).getRegionHeight() / 2);


        bottlePos.set(bottleBody.getPosition().x - bottle.getRegionWidth() /2,
                      bottleBody.getPosition().y - bottle.getRegionHeight() / 2);

        brokenBottlePos.set(brokenBottleBody.getPosition().x - bottle.getRegionWidth() / 2,
                            brokenBottleBody.getPosition().y - bottle.getRegionHeight() / 2);

        //boxBody.setLinearVelocity(terrainOffset, 0f);
        boxPos.set(boxBody.getPosition().x - box.getRegionWidth() / 2,
                   boxBody.getPosition().y - box.getRegionHeight() / 2);


        if(bgOffset * -1 > gameBg.getRegionWidth())
        {
            bgOffset = 0;
        }

        if(bgOffset > 0)
        {
            bgOffset = -gameBg.getRegionWidth();
        }

        if(terrainOffset * -1 > road.getRegionWidth())
        {
            terrainOffset = 0;
        }

        if(terrainOffset > 0)
        {
            terrainOffset = -road.getRegionWidth();
        }

        world.step(delta, 6, 2);
    }

    private void drawScreen()
    {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.disableBlending();
        batch.draw(gameBg, bgOffset, 0);
        batch.draw(gameBg, bgOffset + gameBg.getRegionWidth(), 0);
        batch.enableBlending();

        batch.draw(road, terrainOffset, 0);
        batch.draw(road, terrainOffset + road.getRegionWidth(), 0);


        batch.draw(bottle,
                    bottlePos.x,
                    bottlePos.y);

        batch.draw(brokenBottle,
                    brokenBottlePos.x,
                    brokenBottlePos.y);


        batch.draw(player.getKeyFrame(playerAnimTime),
                playerPos.x,
                playerPos.y);

        batch.draw(enemy.getKeyFrame(enemyAnimTime),
                enemyPos.x,
                enemyPos.y);


        batch.draw(box,
                    boxPos.x,
                    boxPos.y);


        batch.end();
    }

    @Override
    public void render(float delta)
    {
        super.render(delta);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateScreen(delta);
        drawScreen();

        uiStage.act();
        uiStage.draw();
        if(Gdx.input.isKeyPressed(Input.Keys.BACK))
        {
            if (keyHandled)
            {
                return;
            }
            keyHandled = true;
        }

        if(BOX2D_DEBUG)
        {
            debugRend.render(world, camera.combined);
        }
    }
}
