package cz.shroomware.diorama.editor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import cz.shroomware.diorama.editor.EditorEngineGame;
import cz.shroomware.diorama.editor.ui.logic.LogicEditor;
import cz.shroomware.diorama.editor.ui.logic.LogicGraph;
import cz.shroomware.diorama.engine.level.logic.Logic;

public class LogicEditorScreen implements Screen, InputProcessor {
    //TODO make ProjectSelScreeen from same parent
    LogicEditor logicEditor;
    InputMultiplexer inputMultiplexer;
    Color backgroundColor = new Color(0x424242ff);
    EditorEngineGame game;
    LogicGraph graph;
    ShapeRenderer shapeRenderer;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;

    public LogicEditorScreen(EditorEngineGame game, String levelName, Logic logic) {
        this.game = game;

        logicEditor = new LogicEditor(logic, levelName);
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        graph = new LogicGraph(logicEditor, game.getResources(), shapeRenderer);
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(graph);
        inputMultiplexer.addProcessor(this);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        inputMultiplexer.addProcessor(new GestureDetector(new GestureDetector.GestureListener() {
            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                graph.cancelConnection();
                return false;
            }

            @Override
            public boolean longPress(float x, float y) {
                return false;
            }

            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                return false;
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                return false;
            }

            @Override
            public boolean panStop(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean zoom(float initialDistance, float distance) {
                return false;
            }

            @Override
            public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
                return false;
            }

            @Override
            public void pinchStop() {

            }
        }));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void hide() {
        graph.save();
        dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
                | GL20.GL_DEPTH_BUFFER_BIT);

        graph.act();
        graph.draw();
    }

    @Override
    public void resize(int width, int height) {
        graph.getViewport().update(width, height);
        camera.setToOrtho(true, width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE:
                game.returnToEditor();
                return true;
            case Input.Keys.TAB:
                graph.toggleMode();
                return true;
            case Input.Keys.SPACE:
                Camera camera = graph.getCamera();
                camera.position.set(0, 0, 0);
                camera.update();
                return true;
            case Input.Keys.N:
                graph.centerByMax();
                return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        graph.move(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        graph.zoom(amount);
        return true;
    }
}
