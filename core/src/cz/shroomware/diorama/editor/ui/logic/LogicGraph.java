package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Interpolation;
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
import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;

public class LogicGraph extends Stage {
    private static final float CROSS_SIZE = 40;
    private static final Color CROSS_COLOR = new Color(0x050505FF);
    private static final Color EVENT_COLOR = new Color(0x57E389FF);
    private static final Color HANDLER_COLOR = new Color(0x613583FF);
    private static final Color ARROW_COLOR = new Color(0xFAFAFAFF);
    private static final Color REMOVE_COLOR = new Color(0xED333BFF);

    EditorResources resources;
    LogicEditor logicEditor;

    HashMap<LogicComponent, LogicBlock> blocks = new HashMap<>();
    HashMap<Handler, HandlerButton> handlerButtons = new HashMap<>();
    HashMap<Event, EventButton> eventButtons = new HashMap<>();

    EventButton eventButton = null;
    HandlerButton handlerButton = null;

    ShapeRenderer shapeRenderer;
    Preferences preferences;

    public LogicGraph(LogicEditor logicEditor, EditorResources resources, ShapeRenderer shapeRenderer) {
        super(new ScreenViewport());
        this.logicEditor = logicEditor;
        this.resources = resources;
        this.shapeRenderer = shapeRenderer;

        preferences = Gdx.app.getPreferences(logicEditor.getName());

        OrthographicCamera camera = (OrthographicCamera) getCamera();
        camera.position.x = preferences.getFloat("camera.x", 0);
        camera.position.y = preferences.getFloat("camera.y", 0);
        camera.zoom = preferences.getFloat("camera.zoom", 1);
        camera.update();

        update(false);

        preferences.flush();
    }

    float anim = 0;
    float ANIM_DURATION = 4;
    Color arrowActualArrowColor;

    public void setPosToCamera(LogicComponent component) {
        LogicBlock block = blocks.get(component);
        Vector3 cameraPosition = getCamera().position;
        block.setPosition(cameraPosition.x - block.getWidth() / 2, cameraPosition.y - block.getHeight() / 2);
    }

    public void cancelConnection() {
        eventButton = null;
        handlerButton = null;
    }

    private void finishAction() {
        switch (logicEditor.getMode()) {
            case CONNECT:
                addConnection();
                break;
            case DISCONNECT:
                removeConnection();
                break;
        }
    }

    private void addConnection() {
        logicEditor.getLogic().connect(eventButton.getEvent(), handlerButton.getHandler());
        eventButton = null;
        handlerButton = null;
    }

    private void removeConnection() {
        logicEditor.getLogic().disconnect(eventButton.getEvent(), handlerButton.getHandler());
        eventButton = null;
        handlerButton = null;
    }

    public void save() {
        preferences.clear();

        preferences.putFloat("camera.x", getCamera().position.x);
        preferences.putFloat("camera.y", getCamera().position.y);
        preferences.putFloat("camera.zoom", ((OrthographicCamera) getCamera()).zoom);

        for (LogicBlock logicBlock : blocks.values()) {
            LogicComponent logicComponent = logicBlock.getLogicComponent();

            preferences.putFloat(logicComponent.getIdentifier().getId() + ".x", logicBlock.getX());
            preferences.putFloat(logicComponent.getIdentifier().getId() + ".y", logicBlock.getY());
        }

        preferences.flush();
    }

    public void update(boolean savePositions) {
        if (savePositions) {
            save();
        }

        blocks.clear();
        handlerButtons.clear();
        eventButtons.clear();
        clear();

        Collection<LogicComponent> parentsSet = logicEditor.getLogic().getAllParents();
        for (LogicComponent logicComponent : parentsSet) {
            LogicBlock block = new LogicBlock(logicComponent, resources, EVENT_COLOR, HANDLER_COLOR) {
                @Override
                public void onEventClicked(EventButton button) {
                    if (eventButton == null) {
                        eventButton = button;
                        eventButton.getSlot().highlight();
                        if (handlerButton != null) {
                            finishAction();
                        }
                    }
                }

                @Override
                public void onHandlerClicked(HandlerButton button) {
                    if (handlerButton == null) {
                        handlerButton = button;
                        handlerButton.getSlot().highlight();
                        if (eventButton != null) {
                            finishAction();
                        }
                    }
                }

                @Override
                public void onRequestDelete() {
                    logicEditor.getLogic().unregister(getLogicComponent());
                    cancelConnection();
                    update(true);
                }
            };

            float x = preferences.getFloat(logicComponent.getIdentifier().getId() + ".x", 0);
            float y = preferences.getFloat(logicComponent.getIdentifier().getId() + ".y", 0);
            block.setPosition(x, y);
            blocks.put(logicComponent, block);
            eventButtons.putAll(block.getEventButtonHashMap());
            handlerButtons.putAll(block.getHandlerButtonHashMap());
            addActor(block);
        }
    }

    @Override
    public void draw() {
        anim += Gdx.graphics.getDeltaTime();
        if (anim >= ANIM_DURATION) {
            anim = 0;
        }

        shapeRenderer.setProjectionMatrix(getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(CROSS_COLOR);
        shapeRenderer.line(-CROSS_SIZE, 0, CROSS_SIZE, 0);
        shapeRenderer.line(0, -CROSS_SIZE, 0, CROSS_SIZE);

        shapeRenderer.end();

        super.draw();

        shapeRenderer.setProjectionMatrix(getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        arrowActualArrowColor = ARROW_COLOR.cpy();
        if (anim < ANIM_DURATION / 2) {
            arrowActualArrowColor.a = Interpolation.circleOut.apply(0, 1, anim / ANIM_DURATION * 2);
        } else {
            arrowActualArrowColor.a = Interpolation.circleOut.apply(1, 0, (anim - ANIM_DURATION / 2) / ANIM_DURATION * 2);
        }
        drawLines();
        shapeRenderer.end();
    }

    private void drawLine(Actor actorA, Actor actorB) {
        Vector2 aPosition = new Vector2(0, 0);
        Vector2 bPosition = new Vector2(0, 0);

        aPosition = actorA.localToStageCoordinates(aPosition);
        aPosition.add(actorA.getWidth() * actorA.getScaleX() / 2, actorA.getHeight() * actorA.getScaleY() / 2);

        bPosition = actorB.localToStageCoordinates(bPosition);
        bPosition.add(actorB.getWidth() * actorB.getScaleX() / 2, actorB.getHeight() * actorB.getScaleY() / 2);

        Vector2 lineStart = aPosition;
        Vector2 lineEnd = bPosition;

        Vector2 p1 = new Vector2(lineStart);
        Vector2 p2 = new Vector2(lineStart.cpy().add(200, 0));
        Vector2 p3 = new Vector2(lineEnd.cpy().add(-200, 0));
        Vector2 p4 = new Vector2(lineEnd);
        Bezier<Vector2> bezier = new Bezier<>(p1, p2, p3, p4);

        float sample = 1 / 20f;

        float state = 0;

        Vector2 bezierPos = new Vector2();
        bezier.valueAt(bezierPos, state);

        Color oldColor = EVENT_COLOR.cpy();
        Vector2 oldPos = bezierPos.cpy();
        while (state < 1) {
            state += sample;
            if (state > 1) {
                state = 1;
            }

            bezier.valueAt(bezierPos, state);
            Color color = HANDLER_COLOR.cpy().mul(state).add(EVENT_COLOR.cpy().mul(1 - state));
            shapeRenderer.line(oldPos.x, oldPos.y, bezierPos.x, bezierPos.y, oldColor, color);
            oldPos.set(bezierPos);
            oldColor.set(color);
        }

        float theta = Interpolation.circleOut.apply(0, 1, anim / ANIM_DURATION);
        Vector2 direction = new Vector2();
        bezier.derivativeAt(direction, theta);
        direction.nor();
        direction.scl(30);

        Vector2 arrowPos = new Vector2();
        bezier.valueAt(arrowPos, (theta));
        Vector2 arrowEnd = arrowPos.cpy().sub(direction);

        shapeRenderer.setColor(arrowActualArrowColor);
        arrowEnd.rotateAround(arrowPos, 30);
        shapeRenderer.line(arrowPos, arrowEnd);

        arrowEnd.rotateAround(arrowPos, -60);
        shapeRenderer.line(arrowPos, arrowEnd);
    }


    // TODO simplify
    private void drawLineFromActor(Actor actor, Vector3 cursorPos, boolean eventFirst) {
        Vector2 startPosition = new Vector2(0, 0);
        startPosition = actor.localToStageCoordinates(startPosition);
        startPosition.add(actor.getWidth() * actor.getScaleX() / 2, actor.getHeight() * actor.getScaleY() / 2);

        Vector2 cursorPosition2 = new Vector2(cursorPos.x, cursorPos.y);
        Vector2 p1 = new Vector2(startPosition);
        Vector2 p2 = new Vector2();
        Vector2 p3 = new Vector2();
        Vector2 p4 = new Vector2(cursorPosition2);

        switch (logicEditor.getMode()) {

            case CONNECT: {
                Color startColor;
                Color endColor;

                if (eventFirst) {
                    startColor = EVENT_COLOR;
                    endColor = HANDLER_COLOR;
                    p2.set(startPosition.cpy().add(200, 0));
                    p3.set(cursorPosition2.cpy().add(-200, 0));
                } else {
                    startColor = HANDLER_COLOR;
                    endColor = EVENT_COLOR;
                    p2.set(startPosition.cpy().add(-200, 0));
                    p3.set(cursorPosition2.cpy().add(200, 0));
                }

                Bezier<Vector2> bezier = new Bezier<>(p1, p2, p3, p4);

                float sample = 1 / 20f;
                float state = 0;

                Color oldColor = startColor.cpy();
                Vector2 bezierPos = new Vector2();
                Vector2 oldPos = new Vector2(startPosition);
                while (state < 1) {
                    state += sample;
                    if (state > 1) {
                        state = 1;
                    }

                    bezier.valueAt(bezierPos, state);
                    Color color = endColor.cpy().mul(state).add(startColor.cpy().mul(1 - state));
                    shapeRenderer.line(oldPos.x, oldPos.y, bezierPos.x, bezierPos.y, oldColor, color);
                    oldPos.set(bezierPos);
                    oldColor.set(color);
                }

                float theta = 0.5f;
                Vector2 direction = new Vector2();
                bezier.derivativeAt(direction, theta);
                direction.nor();
                if (eventFirst) {
                    direction.scl(30);
                } else {
                    direction.scl(-30);
                }

                Vector2 arrowPos = new Vector2();
                bezier.valueAt(arrowPos, (theta));
                Vector2 arrowEnd = arrowPos.cpy().sub(direction);

                shapeRenderer.setColor(ARROW_COLOR);
                arrowEnd.rotateAround(arrowPos, 30);
                shapeRenderer.line(arrowPos, arrowEnd);

                arrowEnd.rotateAround(arrowPos, -60);
                shapeRenderer.line(arrowPos, arrowEnd);
                break;
            }
            case DISCONNECT:
                if (eventFirst) {
                    p2.set(startPosition.cpy().add(200, 0));
                    p3.set(cursorPosition2.cpy().add(-200, 0));
                } else {
                    p2.set(startPosition.cpy().add(-200, 0));
                    p3.set(cursorPosition2.cpy().add(200, 0));
                }

                Bezier<Vector2> bezier = new Bezier<>(p1, p2, p3, p4);

                float sample = 1 / 20f;
                float state = 0;

                Vector2 bezierPos = new Vector2();
                Vector2 oldPos = new Vector2(startPosition);
                while (state < 1) {
                    state += sample;
                    if (state > 1) {
                        state = 1;
                    }

                    bezier.valueAt(bezierPos, state);
                    shapeRenderer.line(oldPos.x, oldPos.y, bezierPos.x, bezierPos.y, REMOVE_COLOR, REMOVE_COLOR);
                    oldPos.set(bezierPos);
                }
                break;
        }
    }

    private void drawLines() {
        shapeRenderer.setColor(Color.WHITE);
        Gdx.gl20.glLineWidth(3);

        Set<Map.Entry<Event, ArrayList<Handler>>> eventToHandlersConnectionsSet = logicEditor.getLogic().getEventToHandlersConnections().entrySet();
        for (Map.Entry<Event, ArrayList<Handler>> entry : eventToHandlersConnectionsSet) {
            HandlerButton handlerButton;
            EventButton eventButton;

            eventButton = eventButtons.get(entry.getKey());
            ArrayList<Handler> handlers = entry.getValue();
            for (Handler handler : handlers) {
                handlerButton = handlerButtons.get(handler);
                drawLine(eventButton.getSlot(), handlerButton.getSlot());
            }
        }

        Vector3 cursorPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cursorPos = getCamera().unproject(cursorPos);

        if (eventButton != null && handlerButton == null) {
            drawLineFromActor(eventButton.getSlot(), cursorPos, true);
        } else if (eventButton == null && handlerButton != null) {
            drawLineFromActor(handlerButton.getSlot(), cursorPos, false);
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
}
