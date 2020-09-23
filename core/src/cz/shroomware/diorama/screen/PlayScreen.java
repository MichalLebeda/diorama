package cz.shroomware.diorama.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.object.Player;
import cz.shroomware.diorama.engine.level.prototype.SingleRegionPrototype;

public class PlayScreen extends BaseScreen implements InputProcessor {
    protected static final float SPEED = 9.0f;
    protected static final float Y_CAMERA_DISTANCE = 6;
    Player player;

    public PlayScreen(DioramaGame game, Level level) {
        super(game);
        this.level = level;
        updateBackgorundColor(level);
        initCamera(level);

        player = new Player(new Vector3(level.getSize() / 2.f, Y_CAMERA_DISTANCE, 0.5f),
                new Quaternion().setFromAxis(Vector3.X, 90),
                new SingleRegionPrototype(game.getEditorResources().getObjectAtlas().findRegion("dwarf")), level.getBoxFactory());
        level.getGameObjects().add(player);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void drawWorld(float delta) {
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

        player.update();
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
        switch (keycode) {
            case Input.Keys.ESCAPE:
                level.getGameObjects().remove(player);
                game.returnToEditor();
                return true;
        }

        return false;
    }
}
