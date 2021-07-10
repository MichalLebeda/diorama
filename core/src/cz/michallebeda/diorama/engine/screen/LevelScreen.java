package cz.michallebeda.diorama.engine.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import cz.michallebeda.diorama.engine.EngineGame;
import cz.michallebeda.diorama.engine.level.Level;
import cz.michallebeda.diorama.engine.level.object.Player;
import cz.michallebeda.diorama.engine.level.prototype.AtlasRegionPrototype;

public class LevelScreen extends BaseLevelScreen implements InputProcessor {
    protected static final float SPEED = 9.0f;
    protected static final float Y_CAMERA_DISTANCE = 6;
    protected cz.michallebeda.diorama.engine.EngineGame game;
    protected cz.michallebeda.diorama.engine.level.object.Player player;

    public LevelScreen(EngineGame game, Level level, float x, float y) {
        super(game.getResources(), level);
        this.game = game;

        updateBackgroundColor(game.getResources(), level);

        cz.michallebeda.diorama.engine.level.prototype.AtlasRegionPrototype playerPrototype = new AtlasRegionPrototype(
                game.getResources(),
                game.getResources().getObjectAtlas().findRegion("dwarf"));
        player = new Player(new Vector3(x, y, 0), playerPrototype, level.getBoxFactory(), level.getCamera());
        level.getGameObjects().add(player);

        boxDebug = true;
    }

    @Override
    public void show() {
        Gdx.graphics.setTitle("Test: " + level.getMetaLevel().getName());

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void drawWorld(float delta) {
        player.setVelocity(0, 2);
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
        level.update(delta);

        player.update(0);
        camera.position.set(player.getPosition().cpy().add(0, -Y_CAMERA_DISTANCE, 4));
        camera.lookAt(player.getPosition());
        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.setShader(resources.getSpriteBatchShader());
        spriteBatch.begin();
        resources.getSpriteBatchShader().setUniformf("u_camera_pos", camera.position);
        level.draw(spriteBatch, decalBatch, delta);
        spriteBatch.end();

        decalBatch.render(camera, backgroundColor, 0);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        decalBatch.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }
}
