package cz.shroomware.diorama.editor.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

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

    public LogicGraph(Viewport viewport, Logic logic, EditorResources editorResources, ShapeRenderer shapeRenderer) {
        super(viewport);
        this.logic = logic;
        this.shapeRenderer = shapeRenderer;
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        Set<Identifiable> parentsSet = logic.getAllParents();
        for (Identifiable object : parentsSet) {
            LogicBlock block = new LogicBlock(logic, object, editorResources) {
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
            blocks.put(object, block);
            eventButtonHashMap.putAll(block.getEventButtonHashMap());
            handlerButtonHashMap.putAll(block.getHandlerButtonHashMap());
            addActor(block);
        }
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

    private void drawLine(EventButton actorA, HandlerButton actorB) {
        shapeRenderer.line(
                actorA.getParent().getX() + actorA.getX() + actorA.getWidth() / 2,
                actorA.getParent().getY() + actorA.getHeight() / 2 + actorA.getY(),
                actorB.getParent().getX() + actorB.getX() + actorB.getWidth() / 2,
                actorB.getParent().getY() + actorB.getHeight() / 2 + actorB.getY());
    }

    private void drawLines() {
        shapeRenderer.setProjectionMatrix(getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        switch (mode) {
            case ADD:
                shapeRenderer.setColor(Color.WHITE);
                break;
            case REMOVE:
                shapeRenderer.setColor(Color.RED);
                break;
        }

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

    enum Mode {ADD, REMOVE}
}
