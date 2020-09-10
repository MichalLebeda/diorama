package cz.shroomware.diorama.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.engine.GameObject;
import cz.shroomware.diorama.engine.GameObjectPrototype;
import cz.shroomware.diorama.engine.Level;

public class PlayScreen extends BaseLevelScreen implements InputProcessor {
    private static final float SPEED = 0.1f;
    private static final float Y_CAMERA_DISTANCE = 4;
    GameObject player;

    public PlayScreen(DioramaGame game, Level level) {
        super(game);
        this.level = level;
        updateBackgorundColor();
        initCamera(level);

        player = new GameObject(new Vector3(level.getSize() / 2.f, 4, 0.5f),
                new Quaternion().setFromAxis(Vector3.X, 90),
                new GameObjectPrototype(game.getAtlas().findRegion("dwarf")));
        level.addObject(player);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.translate(0, SPEED, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.translate(0, -SPEED, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.translate(-SPEED, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.translate(SPEED, 0, 0);
        }

        camera.position.set(player.getPosition().cpy().add(0,-6,2.5f));
        camera.lookAt(player.getPosition());
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        spriteBatch.getShader().setUniformf("u_camera_pos", camera.position);
        level.draw(spriteBatch, decalBatch, delta);
        spriteBatch.end();

        decalBatch.render(camera,backgroundColor);
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
                level.removeObject(player);
                game.returnToEditor();
                return true;
        }

        return false;
    }
}