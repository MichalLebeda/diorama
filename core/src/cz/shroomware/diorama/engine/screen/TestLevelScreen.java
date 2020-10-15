package cz.shroomware.diorama.engine.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.editor.EditorEngineGame;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.object.Player;
import cz.shroomware.diorama.engine.level.prototype.AtlasRegionPrototype;

public class TestLevelScreen extends BaseLevelScreen implements InputProcessor {
    protected static final float SPEED = 9.0f;
    protected static final float Y_CAMERA_DISTANCE = 6;
    protected EditorEngineGame game;
    protected Player player;

    public TestLevelScreen(EditorEngineGame game, Level level, float x, float y) {
        super(game.getResources(), level);
        this.game = game;

        updateBackgroundColor(level);

        player = new Player(new Vector3(x, y, 0),
                new AtlasRegionPrototype(game.getResources().getObjectAtlas().findRegion("dwarf")), level.getBoxFactory());
        level.getGameObjects().add(player);
    }

    @Override
    public void show() {
        Gdx.graphics.setTitle("Test: " + level.getMetaLevel().getName());

        Gdx.input.setInputProcessor(this);
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
        level.update(delta);

        player.update(0);
        camera.position.set(player.getPosition().cpy().add(0, -Y_CAMERA_DISTANCE, 4));
        camera.lookAt(player.getPosition());
        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        spriteBatch.getShader().setUniformf("u_camera_pos", camera.position);
        spriteBatch.getShader().setUniformf("u_background_color", Color.RED);
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
