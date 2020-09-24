package cz.shroomware.diorama.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.editor.logic.LogicGraph;
import cz.shroomware.diorama.engine.level.logic.Logic;

public class LogicEditorScreen implements Screen, InputProcessor {
    //TODO make ProjectSelScreeen from same parent
    InputMultiplexer inputMultiplexer;
    Color backgroundColor = new Color(0x424242ff);
    Logic logic;
    DioramaGame game;
    LogicGraph graph;
    ShapeRenderer shapeRenderer;
    SpriteBatch spriteBatch;
    OrthographicCamera camera;

    public LogicEditorScreen(DioramaGame game, String levelName, Logic logic) {
        this.logic = logic;
        this.game = game;

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        graph = new LogicGraph(levelName, logic, game.getEditorResources(), shapeRenderer);
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(graph);
        inputMultiplexer.addProcessor(this);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void hide() {
        graph.savePositions();
        dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
                | GL20.GL_DEPTH_BUFFER_BIT);

        graph.act();
        graph.draw();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(camera.viewportWidth - 40, 0, 40, 40);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        graph.getViewport().update(width, height, false);
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
                break;
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
        OrthographicCamera camera = (OrthographicCamera) graph.getCamera();
        camera.translate(-Gdx.input.getDeltaX() * camera.zoom, Gdx.input.getDeltaY() * camera.zoom);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        OrthographicCamera camera = (OrthographicCamera) graph.getCamera();
        camera.zoom += 0.1f * amount;
        camera.update();
        return true;
    }
}
