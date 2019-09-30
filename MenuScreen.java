package com.fugi.balanel.fugi;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuScreen extends ScreenAdapter
{
    private GameMain game;

    private OrthographicCamera camera;
    private Stage menuStage;
    private Skin skin;
    private SpriteBatch batch;
    private Image menuTitle;
    private Table tableMainMenu;
    private ImageButton playButton;
    private ImageButtonStyle playButtonStyle;
    private ImageButton scoresButton;
    private ImageButtonStyle scoresButtonStyle;
    private ImageButton exitButton;
    private ImageButtonStyle exitButtonStyle;
    private ImageButton backButton;
    private ImageButtonStyle backButtonStyle;
    private Table tableScores;
    private LabelStyle labelStyle;
    private Label scoreOne;
    private Label scoreTwo;
    private Label scoreThree;
    private Label scoreFour;

    private ImageButtonStyle settingsButtonStyle;
    private ImageButton settingsButton;

    private Table settingsTable;

    private Group scoresGroup;

    private TextureRegion bgSky;
    private TextureRegion terrain;

    private float bgSkyScroll;
    private float terrainScroll;


    MenuScreen(GameMain gm)
    {
        game = gm;

        camera = game.camera;
        menuStage = new Stage(game.viewport);
        skin = new Skin(game.menuAtlas);

        batch = game.batch;

        bgSky = game.gameAtlas.findRegion("game-bg");
        terrain = game.gameAtlas.findRegion("road");

        menuTitle = new Image(game.menuAtlas.findRegion("menu-title"));

        tableMainMenu = new Table();

        playButtonStyle = new ImageButtonStyle();
        playButtonStyle.imageUp = skin.getDrawable("start-button-up");
        playButtonStyle.imageDown = skin.getDrawable("start-button-down");
        playButton = new ImageButton(playButtonStyle);
        tableMainMenu.add(playButton).padBottom(10);
        tableMainMenu.row();

        scoresButtonStyle = new ImageButtonStyle();
        scoresButtonStyle.imageUp = skin.getDrawable("scores-button-up");
        scoresButtonStyle.imageDown = skin.getDrawable("scores-button-down");
        scoresButton = new ImageButton(scoresButtonStyle);
        tableMainMenu.add(scoresButton).padBottom(10);
        tableMainMenu.row();

        exitButtonStyle = new ImageButtonStyle();
        exitButtonStyle.imageUp = skin.getDrawable("exit-button-up");
        exitButtonStyle.imageDown = skin.getDrawable("exit-button-down");
        exitButton = new ImageButton(exitButtonStyle);
        tableMainMenu.add(exitButton);

        backButtonStyle = new ImageButtonStyle();
        backButtonStyle.imageUp = skin.getDrawable("back-button-up");
        backButtonStyle.imageDown = skin.getDrawable("back-button-down");
        backButton = new ImageButton(backButtonStyle);

        settingsButtonStyle = new ImageButtonStyle();
        settingsButtonStyle.imageUp = skin.getDrawable("settings-up");
        settingsButtonStyle.imageDown = skin.getDrawable("settings-down");
        settingsButton = new ImageButton(settingsButtonStyle);
        settingsButton.setPosition(720, 400);

        //settingsTable = new Table();
        //settingsTable.setBackground(skin.getDrawable("settings-panel"));

        tableScores = new Table();
        tableScores.setBackground(skin.getDrawable("scores-table"));
        tableScores.pack();
        tableScores.padTop(102);
        tableScores.padLeft(-80);

        labelStyle = new LabelStyle(game.font, game.font.getColor());

        scoreOne = new Label("1.           " + game.scores[0], labelStyle);
        tableScores.add(scoreOne);
        tableScores.padBottom(5);
        tableScores.row();

        scoreTwo = new Label("2.           " + game.scores[1], labelStyle);
        tableScores.add(scoreTwo);
        tableScores.padBottom(5);
        tableScores.row();

        scoreThree = new Label("3.           " + game.scores[2], labelStyle);
        tableScores.add(scoreThree);
        tableScores.padBottom(5);
        tableScores.row();

        scoreFour = new Label("4.           " + game.scores[3], labelStyle);
        tableScores.add(scoreFour);
        tableScores.padBottom(10);
        tableScores.row();

        //tableScores.padBottom(50);

        scoresGroup = new Group();

        scoresGroup.addActor(tableScores);
        backButton.setPosition(tableScores.getWidth() / 2 -
                                       backButton.getWidth() / 2,
                               -tableScores.getHeight() / 2 +
                                       backButton.getHeight() + 20);
        scoresGroup.addActor(backButton);

        //tableScores.add(backButton);

        //menuStage.addActor(tableScores);
        menuStage.addActor(scoresGroup);
        menuStage.addActor(tableMainMenu);
        menuStage.addActor(menuTitle);
        menuStage.addActor(settingsButton);

        playButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                game.setScreen(new GameScreen(game));
                //game.setScreen(new GameStageBackup(game));
            }
        });

        scoresButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                showMenu(false);
            }
        });

        exitButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Gdx.app.exit();
            }
        });

        backButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                showMenu(true);
            }
        });

        settingsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {

            }
        });
    }

    private void showMenu(boolean showMenuTable)
    {
        MoveToAction actionMenuMoveIn = Actions.action(MoveToAction.class);
        actionMenuMoveIn.setPosition(400, 210);
        actionMenuMoveIn.setDuration(1.0f);
        actionMenuMoveIn.setInterpolation(Interpolation.swing);

        MoveToAction actionMenuMoveOut = Actions.action(MoveToAction.class);
        actionMenuMoveOut.setPosition(400, -130);
        actionMenuMoveOut.setDuration(1.4f);
        actionMenuMoveOut.setInterpolation(Interpolation.swing);

        MoveToAction actionScoresMoveIn = Actions.action(MoveToAction.class);
        actionScoresMoveIn.setPosition(240, 80);
        actionScoresMoveIn.setDuration(1.0f);
        actionScoresMoveIn.setInterpolation(Interpolation.swing);

        MoveToAction actionScoresMoveOut = Actions.action(MoveToAction.class);
        actionScoresMoveOut.setPosition(240, -330);
        actionScoresMoveOut.setDuration(1.4f);
        actionScoresMoveOut.setInterpolation(Interpolation.swing);

        MoveToAction settingsMoveIn = Actions.action(MoveToAction.class);
        settingsMoveIn.setPosition(400, 210);
        settingsMoveIn.setDuration(1.0f);
        settingsMoveIn.setInterpolation(Interpolation.swing);

        /*
        MoveToAction actionMenuMoveOut = Actions.action(MoveToAction.class);
        actionMenuMoveOut.setPosition(400, -130);
        actionMenuMoveOut.setDuration(1.4f);
        actionMenuMoveOut.setInterpolation(Interpolation.swing); */

        if(showMenuTable)
        {
            scoresGroup.addAction(actionScoresMoveOut);
            tableMainMenu.addAction(actionMenuMoveIn);
        }
        else
        {
            tableMainMenu.addAction(actionMenuMoveOut);
            scoresGroup.addAction(actionScoresMoveIn);
        }
    }


    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(menuStage);
        menuTitle.setPosition(400 - menuTitle.getWidth() / 2, 500);
        tableMainMenu.setPosition(400, -130);
        scoresGroup.setPosition(240, -330);

        MoveToAction actionTitleMoveIn = Actions.action(MoveToAction.class);
        actionTitleMoveIn.setPosition(400 - menuTitle.getWidth() / 2, 380);
        actionTitleMoveIn.setDuration(1.3f);
        actionTitleMoveIn.setInterpolation(Interpolation.elasticOut);

        MoveToAction actionTitleMoveOut = Actions.action(MoveToAction.class);
        actionTitleMoveOut.setPosition(400 - menuTitle.getWidth() / 2, 500);
        actionTitleMoveOut.setDuration(1.3f);
        actionTitleMoveOut.setInterpolation(Interpolation.elasticIn);

        menuTitle.addAction(actionTitleMoveIn);

        showMenu(true);
    }

    private void menuBackground(float delta)
    {
        terrainScroll -= 60f * delta;
        bgSkyScroll -= 6 * delta;

        if(bgSkyScroll * -1 > bgSky.getRegionWidth())
        {
            bgSkyScroll = 0;
        }

        if(terrainScroll * -1 > terrain.getRegionWidth())
        {
            terrainScroll = 0;
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.disableBlending();
        batch.draw(bgSky, bgSkyScroll, 0);
        batch.draw(bgSky, bgSkyScroll + bgSky.getRegionWidth() , 0);
        batch.enableBlending();
        batch.draw(terrain, terrainScroll, 0);
        batch.draw(terrain, terrainScroll + terrain.getRegionWidth(), 0);
        batch.end();
    }

    @Override
    public void render(float delta)
    {
        super.render(delta);

        camera.update();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        menuBackground(delta);

        menuStage.act();
        menuStage.draw();
    }

    @Override
    public void hide()
    {
        menuStage.dispose();
    }

}
