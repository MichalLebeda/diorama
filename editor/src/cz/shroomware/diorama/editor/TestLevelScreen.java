package cz.shroomware.diorama.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.CameraController;
import cz.shroomware.diorama.engine.ai.Node;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.object.Player;
import cz.shroomware.diorama.engine.level.prototype.AtlasRegionPrototype;
import cz.shroomware.diorama.engine.screen.BaseLevelScreen;

import static cz.shroomware.diorama.Utils.path;

public class TestLevelScreen extends BaseLevelScreen implements InputProcessor {
    protected static final float SPEED = 14.0f;
    protected EditorEngineGame game;
    protected Player player;
    protected CameraController cameraController;
    protected InputMultiplexer inputMultiplexer;

    public TestLevelScreen(EditorEngineGame game, Level level, float x, float y) {
        super(game.getResources(), level);
        this.game = game;

        updateBackgroundColor(game.getResources(), level);

        AtlasRegionPrototype playerPrototype = new AtlasRegionPrototype(
                game.getResources(),
                game.getResources().getObjectAtlas().findRegion("zombie"));
        player = new Player(new Vector3(x, y, 0), playerPrototype, level.getBoxFactory(), level.getCamera());
        level.getGameObjects().add(player);

        cameraController = new CameraController(level.getCamera());

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(cameraController);

        decalBatch.setOffset(Utils.Z_OFFSET_PER_METER);
    }

    @Override
    public void show() {
        Gdx.graphics.setTitle("Test: " + level.getMetaLevel().getName());

        Gdx.input.setInputProcessor(inputMultiplexer);
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void hide() {
        super.hide();
        Gdx.input.setCursorCatched(false);
    }

    @Override
    public void drawWorld(float delta) {
        PerspectiveCamera camera = level.getCamera();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.setVelocity(0, SPEED);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.setVelocity(0, -SPEED);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setVelocity(-SPEED, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setVelocity(SPEED, 0);
        }

        level.updatePhysics(delta);

        player.update(0);
        level.updateBasedOnPlayer(delta, level.getFloor(), player);

//        camera.lookAt(player.getPosition());
        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        spriteBatch.getShader().setUniformf("u_camera_pos", camera.position);
        spriteBatch.getShader().setUniformf("u_background_color", Color.RED);
        level.draw(spriteBatch, decalBatch, delta);
        if (path != null) {
            for (Node node : path) {
                spriteBatch.draw(resources.getObjectAtlas().findRegion("cursor"), node.getX(), node.getY(), 0.1f, 0.1f);
            }
        }
        spriteBatch.end();

        decalBatch.render(camera, backgroundColor, 0);

        spriteBatch.setShader(null);
        spriteBatch.setProjectionMatrix(screenCamera.combined);
        spriteBatch.begin();
        spriteBatch.draw(resources.getObjectAtlas().findRegion("cursor"), screenCamera.viewportWidth / 2, screenCamera.viewportHeight / 2);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        decalBatch.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (super.keyDown(keycode)) {
            return true;
        }

        switch (keycode) {
            case Input.Keys.ESCAPE:
                dispose();
                game.returnToEditor();
                return true;
            case Input.Keys.L:
                return true;
        }

        return false;
    }
}
