package cz.shroomware.diorama.editor.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.Logic;
import cz.shroomware.diorama.engine.level.logic.LogicallyRepresentable;

public class LogicGraph extends Stage {
    private static final float CLAMP_PAD = 15;
    private static final float CROSS_SIZE = 40;
    private static final Color EVENT_COLOR = new Color(1, 0.6f, 0.6f, 1);
    private static final Color HANDLER_COLOR = new Color(0.6f, 1, 0.6f, 1);
    private static final Color EVENT_COLOR_LINE = EVENT_COLOR.cpy().mul(0.8f);
    private static final Color HANDLER_COLOR_LINE = HANDLER_COLOR.cpy().mul(0.8f);
    private static final Color REMOVE_COLOR = new Color(1, 0.3f, 0.3f, 0.3f);

    Logic logic;

    HashMap<LogicallyRepresentable, LogicBlock> blocks = new HashMap<>();
    HashMap<Handler, HandlerButton> handlerButtonHashMap = new HashMap<>();
    HashMap<Event, EventButton> eventButtonHashMap = new HashMap<>();

    EventButton eventButton = null;
    HandlerButton handlerButton = null;

    Mode mode = Mode.ADD;

    ShapeRenderer shapeRenderer;
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

        Collection<LogicallyRepresentable> parentsSet = logic.getAllParents();
        for (LogicallyRepresentable logicallyRepresentable : parentsSet) {
            LogicBlock block = new LogicBlock(logicallyRepresentable, editorResources, EVENT_COLOR, HANDLER_COLOR) {
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

                @Override
                public void onButtonDragStart() {
                    cancelConnection();
                }
            };
            float x = preferences.getFloat(logicallyRepresentable.getId() + ".x", 0);
            float y = preferences.getFloat(logicallyRepresentable.getId() + ".y", 0);
            block.setPosition(x, y);
            blocks.put(logicallyRepresentable, block);
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
        shapeRenderer.setProjectionMatrix(getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.line(-CROSS_SIZE, 0, CROSS_SIZE, 0);
        shapeRenderer.line(0, -CROSS_SIZE, 0, CROSS_SIZE);

        shapeRenderer.end();

        super.draw();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        drawLines();
        shapeRenderer.end();
    }

    public void save() {
        preferences.putFloat("camera.x", getCamera().position.x);
        preferences.putFloat("camera.y", getCamera().position.y);
        preferences.putFloat("camera.zoom", ((OrthographicCamera) getCamera()).zoom);

        for (Map.Entry<LogicallyRepresentable, LogicBlock> entry : blocks.entrySet()) {
            LogicallyRepresentable logicallyRepresentable = entry.getKey();
            LogicBlock logicBlock = entry.getValue();
            preferences.putFloat(logicallyRepresentable.getId() + ".x", logicBlock.getX());
            preferences.putFloat(logicallyRepresentable.getId() + ".y", logicBlock.getY());
        }

        preferences.flush();
    }

    private void drawLine(EventButton actorA, HandlerButton actorB) {
        Gdx.gl20.glLineWidth(3);

        Vector2 aPosition = new Vector2(0, 0);
        Vector2 bPosition = new Vector2(0, 0);

        aPosition = actorA.localToStageCoordinates(aPosition);
        aPosition.add(actorA.getWidth() / 2, actorA.getHeight() / 2);

        bPosition = actorB.localToStageCoordinates(bPosition);
        bPosition.add(actorB.getWidth() / 2, actorB.getHeight() / 2);

        aPosition.add(bPosition);
        aPosition.scl(0.5f);
        bPosition = aPosition;
        Vector2 lineStart = clampToActor(actorA, bPosition);
        Vector2 lineEnd = clampToActor(actorB, aPosition);

        shapeRenderer.line(
                lineStart.x,
                lineStart.y,
                lineEnd.x,
                lineEnd.y,
                EVENT_COLOR_LINE,
                HANDLER_COLOR_LINE);
    }


    // TODO simplify
    private void drawLineFromEvent(EventButton eventButton, Vector3 cursorPos) {
        Vector2 startPosition = clampToActor(eventButton, cursorPos);
        switch (mode) {
            case ADD:
                shapeRenderer.line(startPosition.x, startPosition.y,
                        cursorPos.x,
                        cursorPos.y,
                        EVENT_COLOR_LINE,
                        HANDLER_COLOR_LINE);
                break;
            case REMOVE:
                shapeRenderer.line(startPosition.x, startPosition.y,
                        cursorPos.x,
                        cursorPos.y,
                        REMOVE_COLOR,
                        REMOVE_COLOR);
                break;
        }
    }

    // TODO simplify
    private void drawLineFromHandler(HandlerButton handlerButton, Vector3 cursorPos) {
        Vector2 startPosition = clampToActor(handlerButton, cursorPos);
        switch (mode) {
            case ADD:
                shapeRenderer.line(startPosition.x, startPosition.y,
                        cursorPos.x,
                        cursorPos.y,
                        HANDLER_COLOR_LINE,
                        EVENT_COLOR_LINE);
                break;
            case REMOVE:
                shapeRenderer.line(startPosition.x, startPosition.y,
                        cursorPos.x,
                        cursorPos.y,
                        REMOVE_COLOR,
                        REMOVE_COLOR);
                break;
        }
    }

    Vector2 clampToActor(Actor actor, Vector3 position) {
        return clampToActor(actor, new Vector2(position.x, position.y));
    }

    Vector2 clampToActor(Actor actor, Vector2 position) {
        Vector2 clamped = position.cpy();

        Vector2 actorStagePos = new Vector2(0, 0);
        actorStagePos = actor.localToStageCoordinates(actorStagePos);

        if (position.x < actorStagePos.x + CLAMP_PAD) {
            clamped.x = actorStagePos.x + CLAMP_PAD;
        } else if (position.x > actorStagePos.x + actor.getWidth() - CLAMP_PAD) {
            clamped.x = actorStagePos.x + actor.getWidth() - CLAMP_PAD;
        }

        if (position.y < actorStagePos.y + CLAMP_PAD) {
            clamped.y = actorStagePos.y + CLAMP_PAD;
        } else if (position.y > actorStagePos.y + actor.getHeight() - CLAMP_PAD) {
            clamped.y = actorStagePos.y + actor.getHeight() - CLAMP_PAD;
        }

        return clamped;
    }

    private void drawLines() {
        shapeRenderer.setColor(Color.WHITE);

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

        Vector3 cursorPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cursorPos = getCamera().unproject(cursorPos);

        if (eventButton != null && handlerButton == null) {
            drawLineFromEvent(eventButton, cursorPos);
        } else if (eventButton == null && handlerButton != null) {
            drawLineFromHandler(handlerButton, cursorPos);
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

        size.set(rightmost.getX() - leftmost.getX(),
                top.getY() - bottom.getY());
        size.add(rightmost.getWidth(), top.getHeight());

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

    public Mode getMode() {
        return mode;
    }

    enum Mode {ADD, REMOVE}
}
