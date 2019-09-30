package com.fugi.balanel.fugi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameScreen extends ScreenAdapter implements InputProcessor
{
    private GameMain game;

    private Stage uiStage;
    private Skin uiSkin;

    private InputMultiplexer multiplexer = new InputMultiplexer();

    private SpriteBatch batch;
    private OrthographicCamera camera;

    private BitmapFont font;

    private TextureRegion gameBg;
    private float bgScroll = 0;
    private TextureRegion road;
    private float terrainScroll = 0;
    private float terrainScrollSpeed = 0;

    private Animation<TextureRegion> playerRun;
    private Animation<TextureRegion> playerDuck;
    private Animation<TextureRegion> enemyRun;
    private Animation<TextureRegion> enemyDuck;

    private TextureRegion bottle;
    private TextureRegion brokenBottle;
    private TextureRegion box;


    private Sprite playerSprite;
    private Sprite enemySprite;
    private Sprite bottleSprite;
    private Sprite brokenBottleSprite;
    private Sprite boxSprite;

    private ParticleEffect walkDustPlayer;

    private static final float JUMP_IMPULSE = 650;
    private float playerAnimTime = 0f;
    private float enemyAnimTime = 0f;
    private boolean gamePaused = false;
    private boolean gameOver = false;
    private boolean playerDuckTrigger = false;
    private boolean isDuckingPlayer = false;
    private boolean isJumpingPlayer = false;
    private float duckTimerPlayer = 0f;
    private boolean enemyDuckTrigger = false;
    private boolean isDuckingEnemy = false;
    private boolean isJumpingEnemy = false;
    private float duckTimerEnemy = 0;
    private static final float duckTimerLimit = 0.7f;

    private int spawnObject = 0;

    private Image scoreBar;

    private int score = 0;
    private boolean scoreSave = false;

    //Box2D
    private static final float WORLD_TO_BOX = 0.01f;
    private static final float BOX_TO_WORLD = 100f;
    private Box2DDebugRenderer debugRend;
    private static final boolean BOX2D_DEBUG = false;
    private World world;
    private Matrix4 cameraCopy;

    //Vector Coordinates
    private Vector2 playerPos;
    private Vector2 playerDefPos;
    private Vector2 enemyPos;
    private Vector2 enemyDefPos;
    private Vector2 bottlePos;
    private Vector2 bottleDefPos;
    private Vector2 brokenBottlePos;
    private Vector2 brokenBottleDefPos;
    private Vector2 boxPos;
    private Vector2 boxDefPos;

    private Vector2 tempVec;

    //Box2d Bodies
    private Body playerBody;
    private Body enemyBody;
    private Body bottleBody;
    private Body brokenBottleBody;
    private Body boxBody;
    private Body bodyA;
    private Body bodyB;
    private Body unknownBody;

    private FixtureDef playerRunFixt;
    private FixtureDef playerDuckFixt;
    private FixtureDef enemyRunFixt;
    private FixtureDef enemyDuckFixt;
    private PolygonShape boxPoly;

    private Body terrainBody; //No TextureRegion

    private ImageButton settingsButton;
    private ImageButtonStyle settingsButtonStyle;

    private ImageButton duckButton;
    private ImageButtonStyle duckButtonStyle;

    private Group gameOverGroup;

    private Image gameOverImage;

    private Image scoreGameOver;

    private LabelStyle labelStyle;
    private Label scoreGameOverLabel;

    private ImageButton tryAgainButton;
    private ImageButtonStyle tryAgainButtonStyle;


    GameScreen (GameMain gm)
    {
        game = gm;

        uiStage = new Stage(game.viewport);

        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(this);

        batch = game.batch;
        camera = game.camera;
        //camera.setToOrtho(false);
        font = game.font;
        gameBg = game.gameAtlas.findRegion("game-bg");
        road = game.gameAtlas.findRegion("road");

        playerRun = new Animation(0.2f,
                game.gameAtlas.findRegion("player1"),
                game.gameAtlas.findRegion("player2"),
                game.gameAtlas.findRegion("player3"),
                game.gameAtlas.findRegion("player4"),
                game.gameAtlas.findRegion("player5"),
                game.gameAtlas.findRegion("player6"));

        playerRun.setPlayMode(Animation.PlayMode.LOOP);

        playerDuck = new Animation(0.2f,
                game.gameAtlas.findRegion("player_duck1"),
                game.gameAtlas.findRegion("player_duck2"),
                game.gameAtlas.findRegion("player_duck3"),
                game.gameAtlas.findRegion("player_duck4"),
                game.gameAtlas.findRegion("player_duck5"));

        playerDuck.setPlayMode(Animation.PlayMode.LOOP);

        enemyRun = new Animation(0.1f,
                game.gameAtlas.findRegion("enemy1"),
                game.gameAtlas.findRegion("enemy2"),
                game.gameAtlas.findRegion("enemy3"),
                game.gameAtlas.findRegion("enemy4"),
                game.gameAtlas.findRegion("enemy5"));

        enemyRun.setPlayMode(Animation.PlayMode.LOOP);

        enemyDuck = new Animation(0.1f,
                game.gameAtlas.findRegion("enemy_duck1"),
                game.gameAtlas.findRegion("enemy_duck2"),
                game.gameAtlas.findRegion("enemy_duck3"),
                game.gameAtlas.findRegion("enemy_duck4"),
                game.gameAtlas.findRegion("enemy_duck5"));

        enemyDuck.setPlayMode(Animation.PlayMode.LOOP);

        bottle = game.gameAtlas.findRegion("bottle");
        brokenBottle = game.gameAtlas.findRegion("broken-bottle");
        box = game.gameAtlas.findRegion("box");


        bottleSprite = new Sprite(bottle);
        brokenBottleSprite = new Sprite(brokenBottle);
        boxSprite = new Sprite(box);

        walkDustPlayer = game.manager.get("walk_dust", ParticleEffect.class);
        walkDustPlayer.start();

        playerDefPos = new Vector2();
        playerDefPos.set(225f, 46f);

        playerPos = new Vector2();
        playerPos.set(playerDefPos.x, playerDefPos.y);
        tempVec = new Vector2();

        enemyDefPos = new Vector2();
        enemyDefPos.set(-40f, 46f);

        enemyPos = new Vector2();
        enemyPos.set(enemyDefPos.x, enemyDefPos.y);

        bottleDefPos = new Vector2();
        bottleDefPos.set(1300f, 180f);

        bottlePos = new Vector2();
        bottlePos.set(bottleDefPos.x, bottleDefPos.y);

        brokenBottleDefPos = new Vector2();
        brokenBottleDefPos.set(1300f, 180f);

        brokenBottlePos = new Vector2();
        brokenBottlePos.set(brokenBottleDefPos.x, brokenBottleDefPos.y);

        boxDefPos = new Vector2();
        boxDefPos.set(1300f, 46f);

        boxPos = new Vector2();
        boxPos.set(boxDefPos.x, boxDefPos.y);

        initPhysics();

        playerRunFixt = new FixtureDef();
        boxPoly = new PolygonShape();
        boxPoly.setAsBox((playerRun.getKeyFrame(0).getRegionWidth() / 2) * WORLD_TO_BOX,
                         (playerRun.getKeyFrame(0).getRegionHeight() / 2) * WORLD_TO_BOX);
        playerRunFixt.shape = boxPoly;
        playerRunFixt.density = 1f;
        playerRunFixt.restitution = 0f;

        playerDuckFixt = new FixtureDef();
        boxPoly = new PolygonShape();
        boxPoly.setAsBox((playerDuck.getKeyFrame(0).getRegionWidth() / 2) * WORLD_TO_BOX,
                         (playerDuck.getKeyFrame(0).getRegionHeight() / 2) * WORLD_TO_BOX);
        playerDuckFixt.shape = boxPoly;
        playerDuckFixt.density = 1f;
        playerDuckFixt.restitution = 0f;

        enemyRunFixt = new FixtureDef();
        boxPoly = new PolygonShape();
        boxPoly.setAsBox((enemyRun.getKeyFrame(0).getRegionWidth() / 2) * WORLD_TO_BOX,
                         (enemyRun.getKeyFrame(0).getRegionHeight() / 2) * WORLD_TO_BOX);
        enemyRunFixt.shape = boxPoly;
        enemyRunFixt.density = 1f;
        enemyRunFixt.restitution = 0f;

        enemyDuckFixt = new FixtureDef();
        boxPoly = new PolygonShape();
        boxPoly.setAsBox((enemyDuck.getKeyFrame(0).getRegionWidth() / 2) * WORLD_TO_BOX,
                         (enemyDuck.getKeyFrame(0).getRegionHeight() / 2) * WORLD_TO_BOX);
        enemyDuckFixt.shape = boxPoly;
        enemyDuckFixt.density = 1f;
        enemyDuckFixt.restitution = 0f;

        //boxPoly.dispose();

        walkDustPlayer.setPosition(playerDefPos.x + playerRun.getKeyFrame(0).getRegionWidth() / 2,
                                   playerDefPos.y);


        uiSkin = new Skin(game.uiStageAtlas);

        scoreBar = new Image(uiSkin.getDrawable("scoreBar"));

        scoreBar.setPosition(10, 400);

        uiStage.addActor(scoreBar);

        settingsButtonStyle = new ImageButtonStyle();
        settingsButtonStyle.imageUp = uiSkin.getDrawable("settings-up");
        settingsButtonStyle.imageDown = uiSkin.getDrawable("settings-down");
        settingsButton = new ImageButton(settingsButtonStyle);

        settingsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(gamePaused == false)
                {
                    gamePaused = true;
                }
                else
                {
                    gamePaused = false;
                }
                //game.setScreen(new MenuScreen(game));
            }
        });

        settingsButton.setPosition(720, 400);

        uiStage.addActor(settingsButton);

        duckButtonStyle = new ImageButtonStyle();
        duckButtonStyle.imageUp = uiSkin.getDrawable("duck-button-up");
        duckButtonStyle.imageDown = uiSkin.getDrawable("duck-button-down");
        duckButton = new ImageButton(duckButtonStyle);

        duckButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(!isJumpingPlayer && !gamePaused && !gameOver)
                {
                    playerDuckTrigger = true;
                }
            }
        });

        duckButton.setPosition(697, 20);

        uiStage.addActor(duckButton);

        gameOverGroup = new Group();

        gameOverImage = new Image(uiSkin.getDrawable("game-over"));
        gameOverImage.setPosition(400 - gameOverImage.getWidth() / 2,
                                  300 - gameOverImage.getHeight() / 2);

        scoreGameOver = new Image(uiSkin.getDrawable("game-over-score"));
        scoreGameOver.setPosition(400 - scoreGameOver.getWidth() / 2,
                                  230 - scoreGameOver.getHeight() / 2);

        labelStyle = new LabelStyle(game.font, game.font.getColor());
        scoreGameOverLabel = new Label("" + score, labelStyle);
        scoreGameOverLabel.setPosition(scoreGameOver.getX() + 185,
                                       scoreGameOver.getY() + 8);

        tryAgainButtonStyle = new ImageButtonStyle();
        tryAgainButtonStyle.imageUp = uiSkin.getDrawable("try-button-up");
        tryAgainButtonStyle.imageDown = uiSkin.getDrawable("try-button-down");
        tryAgainButton = new ImageButton(tryAgainButtonStyle);

        tryAgainButton.setPosition(400 - tryAgainButton.getWidth() / 2,
                                   160 - tryAgainButton.getHeight() / 2);

        gameOverGroup.addActor(tryAgainButton);
        gameOverGroup.addActor(scoreGameOver);
        gameOverGroup.addActor(scoreGameOverLabel);
        gameOverGroup.addActor(gameOverImage);

        //gameOverGroup.setPosition(0, 0);

        //uiStage.addActor(gameOverGroup);
    }

    private Body graphicsToPhysics(TextureRegion region, Vector2 position, BodyDef.BodyType bodyType)
    {
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = (position.x + region.getRegionWidth() / 2) * WORLD_TO_BOX;
        boxBodyDef.position.y = (position.y + region.getRegionHeight() / 2) * WORLD_TO_BOX;

        Body boxBody = world.createBody(boxBodyDef);

        PolygonShape boxPoly = new PolygonShape();
        boxPoly.setAsBox((region.getRegionWidth() / 2) * WORLD_TO_BOX,
                         (region.getRegionHeight() / 2) * WORLD_TO_BOX);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxPoly;
        fixtureDef.density = 1;
        fixtureDef.restitution = 0f;
        boxBody.createFixture(fixtureDef);

        boxPoly.dispose();
        boxBody.setUserData(region);

        return boxBody;
    }

    private void resetScreen()
    {
        bgScroll = 0f;
        terrainScroll = 0f;
        playerAnimTime = 0f;
        enemyAnimTime = 0f;
        spawnObject = 0;
        score = 0;
        playerDuckTrigger = false;
        isDuckingPlayer = false;
        enemyDuckTrigger = false;
        isDuckingEnemy = false;

        playerPos.set(playerDefPos.x, playerDefPos.y);
        enemyPos.set(enemyDefPos.x, enemyDefPos.y);
        boxPos.set(boxDefPos.x, boxDefPos.y);
        bottlePos.set(bottleDefPos.x, bottleDefPos.y);
        brokenBottlePos.set(brokenBottleDefPos.x, brokenBottleDefPos.y);

        initPhysics();

        playerSprite = new Sprite(playerRun.getKeyFrame(playerAnimTime));
        enemySprite = new Sprite(enemyRun.getKeyFrame(enemyAnimTime));

        MoveToAction gameOverMoveOut = Actions.action(MoveToAction.class);
        gameOverMoveOut.setPosition(0, -200);
        gameOverMoveOut.setDuration(1.0f);
        gameOverMoveOut.setInterpolation(Interpolation.swing);

        //gameOverGroup.addAction(gameOverMoveOut);

        gameOverGroup.remove();

        uiStage.addActor(settingsButton);
        uiStage.addActor(duckButton);
        uiStage.addActor(scoreBar);
    }

    private void initPhysics()
    {
        world = new World(new Vector2(0, -9.8f), true);
        debugRend = new Box2DDebugRenderer();

        playerBody = graphicsToPhysics(playerRun.getKeyFrame(0),
                playerPos,
                BodyDef.BodyType.DynamicBody);


        enemyBody = graphicsToPhysics(enemyRun.getKeyFrame(0),
                enemyPos,
                BodyDef.BodyType.DynamicBody);

        bottleBody = graphicsToPhysics(bottle,
                                       bottlePos,
                                       BodyDef.BodyType.KinematicBody);

        brokenBottleBody = graphicsToPhysics(brokenBottle,
                                             brokenBottlePos,
                                             BodyDef.BodyType.KinematicBody);

        boxBody = graphicsToPhysics(box,
                                    boxPos,
                                    BodyDef.BodyType.KinematicBody);


        //Terrain Body
        final BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = BodyDef.BodyType.StaticBody;
        boxBodyDef.position.x = (-800f + 2400f / 2) * WORLD_TO_BOX;
        boxBodyDef.position.y = (0 + 45f / 2) * WORLD_TO_BOX;

        terrainBody = world.createBody(boxBodyDef);

        PolygonShape boxPoly = new PolygonShape();
        boxPoly.setAsBox((1600f / 2) * WORLD_TO_BOX,
                (45f / 2) * WORLD_TO_BOX);

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxPoly;
        fixtureDef.density = 1;
        fixtureDef.restitution = 0f;

        terrainBody.createFixture(fixtureDef);
        boxPoly.dispose();

        world.setContactListener(new ContactListener()
        {
            @Override
            public void beginContact(Contact contact)
            {
                bodyA = contact.getFixtureA().getBody();
                bodyB = contact.getFixtureB().getBody();
                boolean playerFound = false;
                if(bodyA.equals(playerBody))
                {
                    playerFound = true;
                    unknownBody = bodyB;
                }
                else if(bodyB.equals(playerBody))
                {
                    playerFound = true;
                    unknownBody = bodyA;
                }

                if(playerFound)
                {
                    if(bodyA.equals(playerBody) && bodyB.equals(boxBody))
                    {
                        gameOver = true;
                        scoreSave = true;
                        settingsButton.remove();
                        duckButton.remove();
                        scoreBar.remove();
                    }
                    else if(bodyA.equals(boxBody) && bodyB.equals(playerBody))
                    {
                        gameOver = true;
                        scoreSave = true;
                        settingsButton.remove();
                        duckButton.remove();
                        scoreBar.remove();
                    }
                    else if(bodyA.equals(playerBody) && bodyB.equals(bottleBody))
                    {
                        gameOver = true;
                        scoreSave = true;
                        settingsButton.remove();
                        duckButton.remove();
                        scoreBar.remove();
                    }
                    else if(bodyA.equals(bottleBody) && bodyB.equals(playerBody))
                    {
                        gameOver = true;
                        scoreSave = true;
                        settingsButton.remove();
                        duckButton.remove();
                        scoreBar.remove();
                    }
                    else if(bodyA.equals(playerBody) && bodyB.equals(brokenBottleBody))
                    {
                        gameOver = true;
                        scoreSave = true;
                        settingsButton.remove();
                        duckButton.remove();
                        scoreBar.remove();
                    }
                    else if(bodyA.equals(brokenBottleBody) && bodyB.equals(playerBody))
                    {
                        gameOver = true;
                        scoreSave = true;
                        settingsButton.remove();
                        duckButton.remove();
                        scoreBar.remove();
                    }
                }
            }

            @Override
            public void endContact(Contact contact)
            {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold)
            {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse)
            {

            }
        });
    }

    private void updateScreen(float delta)
    {
        terrainScrollSpeed = 350f * delta;
        bgScroll -= terrainScrollSpeed / 10;
        terrainScroll -= terrainScrollSpeed;
        //playerAnimTime += delta * 2;
        //enemyAnimTime += delta;

        if(isDuckingPlayer || playerDuckTrigger)
        {
            duckPlayer(delta);
            playerSprite = new Sprite(playerDuck.getKeyFrame(playerAnimTime));
            playerPos.set(playerBody.getPosition().x * BOX_TO_WORLD - playerDuck.getKeyFrame(0).getRegionWidth() / 2,
                          playerBody.getPosition().y * BOX_TO_WORLD - playerDuck.getKeyFrame(0).getRegionHeight() / 2);
        }
        else
        {
            playerSprite = new Sprite(playerRun.getKeyFrame(playerAnimTime));
            playerPos.set(playerBody.getPosition().x * BOX_TO_WORLD - playerRun.getKeyFrame(0).getRegionWidth() / 2,
                          playerBody.getPosition().y * BOX_TO_WORLD - playerRun.getKeyFrame(0).getRegionHeight() / 2);
        }


        walkDustPlayer.setPosition(playerPos.x + playerRun.getKeyFrame(0).getRegionWidth() / 2,
                                   playerPos.y);
        walkDustPlayer.update(delta);


        if(isDuckingEnemy || enemyDuckTrigger)
        {
            duckEnemy(delta);
            enemySprite = new Sprite(enemyDuck.getKeyFrame(enemyAnimTime));
            enemyPos.set(enemyBody.getPosition().x * BOX_TO_WORLD - enemyDuck.getKeyFrame(0).getRegionWidth() / 2,
                         enemyBody.getPosition().y * BOX_TO_WORLD - enemyDuck.getKeyFrame(0).getRegionHeight() / 2);
        }
        else
        {
            enemySprite = new Sprite(enemyRun.getKeyFrame(enemyAnimTime));
            enemyPos.set(enemyBody.getPosition().x * BOX_TO_WORLD - enemyRun.getKeyFrame(0).getRegionWidth() / 2,
                         enemyBody.getPosition().y * BOX_TO_WORLD - enemyRun.getKeyFrame(0).getRegionHeight() / 2);
        }


        if(playerPos.y >= 50f)
        {
            isJumpingPlayer = true;
            walkDustPlayer.setDuration(0);
            playerAnimTime = 0;
        }

        if(playerPos.y <= 50f)
        {
            walkDustPlayer.setDuration(90);
            walkDustPlayer.start();
            playerAnimTime += delta * 2;
            isJumpingPlayer = false;
        }


        bottlePos.set(bottleBody.getPosition().x * BOX_TO_WORLD - (bottle.getRegionWidth() /2 + 7),
                      bottleBody.getPosition().y * BOX_TO_WORLD - bottle.getRegionHeight() / 2);

        brokenBottlePos.set(brokenBottleBody.getPosition().x * BOX_TO_WORLD - (brokenBottle.getRegionWidth() / 2 + 7),
                            brokenBottleBody.getPosition().y * BOX_TO_WORLD - brokenBottle.getRegionHeight() / 2);


        if(enemyPos.y >= 50f)
        {
            isJumpingEnemy = true;
            enemyAnimTime = 0;
        }

        if(enemyPos.y <= 50f)
        {
            isJumpingEnemy = false;
            enemyAnimTime += delta;
        }

        boxPos.set(boxBody.getPosition().x * BOX_TO_WORLD - (box.getRegionWidth() / 2 + 7),
                   boxBody.getPosition().y * BOX_TO_WORLD - box.getRegionHeight() / 2);

        if(spawnObject == 0)
        {
            boxBody.setLinearVelocity((-terrainScrollSpeed / delta) * WORLD_TO_BOX, 0f);
        }
        else if(spawnObject == 1)
        {
            bottleBody.setAngularVelocity(12);
            bottleBody.setLinearVelocity(((-terrainScrollSpeed - 5) / delta) * WORLD_TO_BOX, 0f);
        }
        else if(spawnObject == 2)
        {
            brokenBottleBody.setAngularVelocity(12);
            brokenBottleBody.setLinearVelocity(((-terrainScrollSpeed - 5) / delta) * WORLD_TO_BOX, 0f);
        }
        if(spawnObject > 2)
        {
            spawnObject = MathUtils.random(0, 2);
        }

        //System.out.println(boxPos.x);

        if(boxPos.x < -100)
        {
            boxBody = graphicsToPhysics(box,
                                        boxDefPos,
                                        BodyDef.BodyType.KinematicBody);
            spawnObject = MathUtils.random(0, 2);
        }

        if(bottlePos.x < -100)
        {
            bottleBody = graphicsToPhysics(bottle,
                                           bottleDefPos,
                                           BodyDef.BodyType.KinematicBody);
            spawnObject = MathUtils.random(0, 2);
        }

        if(brokenBottlePos.x < -100)
        {
            brokenBottleBody = graphicsToPhysics(brokenBottle,
                                                 brokenBottleDefPos,
                                                 BodyDef.BodyType.KinematicBody);
            spawnObject = MathUtils.random(0, 2);
        }

        playerSprite.setPosition(playerPos.x,
                                 playerPos.y);
        enemySprite.setPosition(enemyPos.x,
                                enemyPos.y);

        bottleSprite.setPosition(bottlePos.x,
                                 bottlePos.y);

        brokenBottleSprite.setPosition(brokenBottlePos.x,
                                       brokenBottlePos.y);

        boxSprite.setPosition(boxPos.x,
                              boxPos.y);

        if(boxPos.x - enemyPos.x <= 170f && boxPos.x >= 40f && enemyPos.y <= 50f)
        {
            score += 2;
            tempVec.set(0,
                        JUMP_IMPULSE * WORLD_TO_BOX);
            enemyBody.applyLinearImpulse(tempVec,
                                         enemyBody.getWorldCenter(),
                                         true);
        }

        if(bottlePos.x - enemyPos.x <= 170f && bottlePos.x >= 40f && enemyPos.y <=50f)
        {
            score += 4;
            if(!isJumpingEnemy && !gamePaused && !gameOver)
            {
                enemyDuckTrigger = true;
            }
        }

        if(brokenBottlePos.x - enemyPos.x <= 170f && brokenBottlePos.x >= 40f && enemyPos.y <=50f)
        {
            score += 4;
            if(!isJumpingEnemy && !gamePaused && !gameOver)
            {
                enemyDuckTrigger = true;
            }
        }

        if(bgScroll * -1 > gameBg.getRegionWidth())
        {
            bgScroll = 0;
        }

        if(bgScroll > 0)
        {
            bgScroll = -gameBg.getRegionWidth() + delta;
        }

        if(terrainScroll * -1 > road.getRegionWidth())
        {
            terrainScroll = 0;
        }

        if(terrainScroll > 0)
        {
            terrainScroll = -road.getRegionWidth() + delta;
        }

        world.step(delta, 6, 2);
    }

    private void duckPlayer(float delta)
    {
        if(playerDuckTrigger)
        {
            playerBody.destroyFixture(playerBody.getFixtureList().first());
            playerBody.createFixture(playerDuckFixt).setUserData(playerDuck.getKeyFrame(0));
            playerDuckTrigger = false;
            duckTimerPlayer = 0f;
        }
        duckTimerPlayer += delta;
        if(duckTimerPlayer <= duckTimerLimit)
        {
            isDuckingPlayer = true;
        }
        if(duckTimerPlayer > duckTimerLimit && isDuckingPlayer)
        {
            playerBody.destroyFixture(playerBody.getFixtureList().first());
            playerBody.createFixture(playerRunFixt).setUserData(playerRun.getKeyFrame(0));
            isDuckingPlayer = false;
        }
        if(gamePaused || gameOver)
        {
            duckTimerPlayer = 10f;
        }
    }

    private void duckEnemy(float delta)
    {
        if(enemyDuckTrigger)
        {
            enemyBody.destroyFixture(enemyBody.getFixtureList().first());
            enemyBody.createFixture(enemyDuckFixt).setUserData(enemyDuck.getKeyFrame(0));
            enemyDuckTrigger = false;
            duckTimerEnemy = 0f;
        }
        duckTimerEnemy += delta;
        if(duckTimerEnemy <= duckTimerLimit)
        {
            isDuckingEnemy = true;
        }
        if(duckTimerEnemy > duckTimerLimit && isDuckingEnemy)
        {
            enemyBody.destroyFixture(enemyBody.getFixtureList().first());
            enemyBody.createFixture(enemyRunFixt).setUserData(enemyRun.getKeyFrame(0));
            isDuckingEnemy = false;
        }
        if(gamePaused || gameOver)
        {
            duckTimerEnemy = 10f;
        }
    }

    private void gameOver()
    {
        tryAgainButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                resetScreen();
                gameOver = false;
            }
        });

        saveScore(scoreSave);
        scoreGameOverLabel.setText("" + score);

        MoveToAction gameOverMoveIn = Actions.action(MoveToAction.class);
        gameOverMoveIn.setPosition(0, 200);
        gameOverMoveIn.setDuration(1.0f);
        gameOverMoveIn.setInterpolation(Interpolation.swing);

        //gameOverGroup.addAction(gameOverMoveIn);
        uiStage.addActor(gameOverGroup);
    }

    private void saveScore(boolean flag)
    {
        if(flag)
        {
            scoreSave = false;
            for(int i = 0; i < game.scores.length; i++)
            {
                if(score > game.scores[i])
                {
                    if(i == 0)
                    {
                        game.scores[3] = game.scores[2];
                        game.scores[2] = game.scores[1];
                        game.scores[1] = game.scores[0];
                        game.scores[0] = score;
                    }
                    else if(i == 1)
                    {
                        game.scores[3] = game.scores[2];
                        game.scores[2] = game.scores[1];
                        game.scores[1] = score;
                    }
                    else if(i == 2)
                    {
                        game.scores[3] = game.scores[2];
                        game.scores[2] = score;
                    }
                    else
                    {
                        game.scores[3] = score;
                    }
                    break;
                }
            }
        }
    }

    private void drawScreen()
    {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.disableBlending();
        batch.draw(gameBg, bgScroll, 0);
        batch.draw(gameBg, bgScroll + gameBg.getRegionWidth(), 0);
        batch.enableBlending();

        batch.draw(road, terrainScroll, 0);
        batch.draw(road, terrainScroll + road.getRegionWidth(), 0);


        bottleSprite.setRotation(bottleBody.getAngle() * MathUtils.radiansToDegrees);
        bottleSprite.draw(batch);

        brokenBottleSprite.setRotation(brokenBottleBody.getAngle() * MathUtils.radiansToDegrees);
        brokenBottleSprite.draw(batch);

        playerSprite.setRotation(playerBody.getAngle() * MathUtils.radiansToDegrees);
        playerSprite.draw(batch);

        enemySprite.setRotation(enemyBody.getAngle() * MathUtils.radiansToDegrees);
        enemySprite.draw(batch);

        walkDustPlayer.draw(batch);

        boxSprite.setRotation(boxBody.getAngle() * MathUtils.radiansToDegrees);
        boxSprite.draw(batch);

        if(!gameOver)
        {
            font.draw(batch, "" + score, 110, 446);
        }
        batch.end();
    }

    @Override
    public void render(float delta)
    {
        super.render(delta);

        camera.update();
        Gdx.input.setInputProcessor(multiplexer);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cameraCopy = camera.combined.cpy();

        if(gamePaused)
        {
            drawScreen();

            if(BOX2D_DEBUG)
            {
                debugRend.render(world, cameraCopy.scl(BOX_TO_WORLD));
            }

            uiStage.act();
            uiStage.draw();
        }
        else if(gameOver)
        {
            gameOver();
            drawScreen();

            if(BOX2D_DEBUG)
            {
                debugRend.render(world, cameraCopy.scl(BOX_TO_WORLD));
            }

            uiStage.act();
            uiStage.draw();
        }
        else
        {
            updateScreen(delta);
            drawScreen();

            if(BOX2D_DEBUG)
            {
                debugRend.render(world, cameraCopy.scl(BOX_TO_WORLD));
            }

            uiStage.act();
            uiStage.draw();
        }
    }

    @Override
    public boolean keyDown(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        isJumpingPlayer = true;
        if(playerPos.y <= 50f && !playerDuckTrigger && !isDuckingPlayer && !gamePaused)
        {
            tempVec.set(0,
                        JUMP_IMPULSE * WORLD_TO_BOX);

            playerBody.applyLinearImpulse(tempVec,
                                          playerBody.getWorldCenter(),
                                          true);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }

    @Override
    public void hide()
    {
        uiStage.dispose();
    }
}
