package cz.shroomware.diorama.editor.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.Identifiable;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.Logic;

public class LogicGraph extends Stage {
    HashMap<Identifiable, LogicBlock> blocks = new HashMap<>();
    HashMap<Handler, HandlerButton> handlerButtonHashMap = new HashMap<>();
    HashMap<Event, EventButton> eventButtonHashMap = new HashMap<>();

    Logic logic;
    EventButton eventButton = null;
    HandlerButton handlerButton = null;
    ShapeRenderer shapeRenderer;
    Mode mode = Mode.ADD;
    Preferences preferences;

    public LogicGraph(String levelName, Logic logic, EditorResources editorResources, ShapeRenderer shapeRenderer) {
        super(new ScreenViewport());
        this.logic = logic;
        this.shapeRenderer = shapeRenderer;

        preferences = Gdx.app.getPreferences(levelName);

        OrthographicCamera camera = (OrthographicCamera) getCamera();
        camera.position.x = preferences.getFloat("camera.x", 0);
        camera.position.y = preferences.getFloat("camera.y", 0);
        camera.zoom = preferences.getFloat("camera.zoom", 1);
        camera.update();

        Set<Identifiable> parentsSet = logic.getAllParents();
        for (Identifiable identifiable : parentsSet) {
            LogicBlock block = new LogicBlock(logic, identifiable, editorResources) {
                @Override
                public void onEventClicked(EventButton button) {
                    if (eventButton == null) {
                        eventButton = button;
                        if (handlerButton != null) {
                            finishAction();
                        }
                    }
                }

                @Override
                public void onHandlerClicked(HandlerButton button) {
                    if (handlerButton == null) {
                        handlerButton = button;
                        if (eventButton != null) {
                            finishAction();
                        }
                    }
                }
            };
            float x = preferences.getFloat(identifiable.getId() + ".x", 0);
            float y = preferences.getFloat(identifiable.getId() + ".y", 0);
            block.setPosition(x, y);
            blocks.put(identifiable, block);
            eventButtonHashMap.putAll(block.getEventButtonHashMap());
            handlerButtonHashMap.putAll(block.getHandlerButtonHashMap());
            addActor(block);
        }

        preferences.flush();
    }

    public void cancelConnection() {
        eventButton = null;
        handlerButton = null;
    }

    public void toggleMode() {
        if (mode == Mode.ADD) {
            mode = Mode.REMOVE;
        } else if (mode == Mode.REMOVE) {
            mode = Mode.ADD;
        }
    }

    private void finishAction() {
        switch (mode) {
            case ADD:
                addConnection();
                break;
            case REMOVE:
                removeConnection();
                break;
        }
    }

    private void addConnection() {
        logic.connect(eventButton.getEvent(), handlerButton.getHandler());
        eventButton = null;
        handlerButton = null;
    }

    private void removeConnection() {
        logic.disconnect(eventButton.getEvent(), handlerButton.getHandler());
        eventButton = null;
        handlerButton = null;
    }

    @Override
    public void draw() {
        super.draw();
        drawLines();
    }

    public void save() {
        preferences.putFloat("camera.x", getCamera().position.x);
        preferences.putFloat("camera.y", getCamera().position.y);
        preferences.putFloat("camera.zoom", ((OrthographicCamera) getCamera()).zoom);

        for (Map.Entry<Identifiable, LogicBlock> entry : blocks.entrySet()) {
            Identifiable identifiable = entry.getKey();
            LogicBlock logicBlock = entry.getValue();
            preferences.putFloat(identifiable.getId() + ".x", logicBlock.getX());
            preferences.putFloat(identifiable.getId() + ".y", logicBlock.getY());
        }

        preferences.flush();
    }

    private void drawLine(EventButton actorA, HandlerButton actorB) {
        Gdx.gl20.glLineWidth(3);
        float lineStartXOffset = 0;
        float lineEndXOffset = 0;

        Vector2 aPosition = new Vector2(0, 0);
        Vector2 bPosition = new Vector2(0, 0);

        aPosition = actorA.localToStageCoordinates(aPosition);
        bPosition = actorB.localToStageCoordinates(bPosition);

        aPosition.add(actorA.getWidth(), actorA.getHeight() / 2);
        bPosition.add(0, actorB.getHeight() / 2);

        shapeRenderer.line(
                aPosition.x + lineStartXOffset,
                aPosition.y,
                bPosition.x + lineEndXOffset,
                bPosition.y);
    }

    private void drawLines() {
        shapeRenderer.setProjectionMatrix(getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        shapeRenderer.circle(0, 0, 10);

        Set<Map.Entry<Event, ArrayList<Handler>>> eventToHandlersConnectionsSet = logic.getEventToHandlersConnections().entrySet();
        for (Map.Entry<Event, ArrayList<Handler>> entry : eventToHandlersConnectionsSet) {
            HandlerButton handlerButton;
            EventButton eventButton;

            eventButton = eventButtonHashMap.get(entry.getKey());
            ArrayList<Handler> handlers = entry.getValue();
            for (Handler handler : handlers) {
                handlerButton = handlerButtonHashMap.get(handler);
                drawLine(eventButton, handlerButton);
            }
        }

        switch (mode) {
            case ADD:
                shapeRenderer.setColor(Color.WHITE);
                break;
            case REMOVE:
                shapeRenderer.setColor(Color.RED);
                break;
        }

        Vector3 cursorPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cursorPos = getCamera().unproject(cursorPos);
        if (eventButton != null && handlerButton == null) {
            Group parent = eventButton.getParent();
            shapeRenderer.line(parent.getX() + eventButton.getWidth() / 2 + eventButton.getX(),
                    parent.getY() + eventButton.getHeight() / 2 + eventButton.getY(),
                    cursorPos.x,
                    cursorPos.y);
        } else if (eventButton == null && handlerButton != null) {
            Group parent = handlerButton.getParent();
            shapeRenderer.line(parent.getX() + handlerButton.getX() + handlerButton.getWidth() / 2,
                    parent.getY() + handlerButton.getHeight() / 2 + handlerButton.getY(),
                    cursorPos.x,
                    cursorPos.y);
        }
        shapeRenderer.end();
    }

    public void centerByAverage() {
        Vector2 averagePos = new Vector2();

        for (LogicBlock block : blocks.values()) {
            averagePos.add(block.getX(), block.getY());
        }

        averagePos.scl(1f / blocks.size());

        for (LogicBlock block : blocks.values()) {
            block.setPosition(block.getX() - averagePos.x + getViewport().getWorldWidth() / 2,
                    block.getY() - averagePos.y + getViewport().getWorldHeight() / 2);
        }
    }

    public void centerByMax() {
        if (blocks.isEmpty()) {
            return;
        }

        Actor leftmost = null,
                rightmost = null,
                top = null,
                bottom = null;

        for (LogicBlock block : blocks.values()) {
            if (leftmost == null || block.getX() < leftmost.getX()) {
                leftmost = block;
            }
            if (rightmost == null || block.getX() + block.getWidth() > rightmost.getX() + rightmost.getWidth()) {
                rightmost = block;
            }
            if (top == null || block.getY() + block.getHeight() > top.getY() + top.getHeight()) {
                top = block;
            }
            if (bottom == null || block.getY() < bottom.getY()) {
                bottom = block;
            }
        }

        Vector2 size = new Vector2();
        Vector2 position = new Vector2();

        size.set(
                (rightmost.getX() - leftmost.getX() + rightmost.getWidth()),
                (top.getY() - bottom.getY() + top.getHeight()));

        position.set(leftmost.getX(), bottom.getY());

        for (LogicBlock block : blocks.values()) {
            block.setPosition(-position.x + block.getX() - size.x / 2,
                    -position.y + block.getY() - size.y / 2);
        }
    }

    public void move(int amountX, int amountY) {
        OrthographicCamera camera = (OrthographicCamera) getCamera();
        camera.translate(-amountX * camera.zoom, amountY * camera.zoom);
    }

    public void zoom(int amount) {
        OrthographicCamera camera = (OrthographicCamera) getCamera();
        camera.zoom += 0.1f * amount;
        if (camera.zoom < 1) {
            camera.zoom = 1;
        } else if (camera.zoom > 5) {
            camera.zoom = 5;
        }
        camera.update();
    }

    enum Mode {ADD, REMOVE}
}
