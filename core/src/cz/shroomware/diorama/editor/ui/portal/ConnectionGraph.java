package cz.shroomware.diorama.editor.ui.portal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.Project;
import cz.shroomware.diorama.engine.level.MetaLevel;
import cz.shroomware.diorama.engine.level.portal.MetaPortal;

public class ConnectionGraph extends Stage {
    private static final float CROSS_SIZE = 40;
    private static final Color EVENT_COLOR = Color.WHITE.cpy().mul(0.9f, 0.9f, 0.9f, 1);
    private static final Color HANDLER_COLOR = Color.WHITE.cpy().mul(0.4f, 0.4f, 0.4f, 1);
    private static final Color ARROW_COLOR = Color.WHITE.cpy().mul((
                    EVENT_COLOR.r + HANDLER_COLOR.r) / 2f,
            (EVENT_COLOR.g + HANDLER_COLOR.g) / 2f,
            (EVENT_COLOR.b + HANDLER_COLOR.b) / 2f,
            1);
    private static final Color REMOVE_COLOR = new Color(1, 0.3f, 0.3f, 0.3f);

    ConnectionEditor connectionEditor;
    Project project;
    EditorResources resources;

    PortalButton portalButtonA = null;

    HashMap<MetaLevel, MetaLevelBlock> blocks = new HashMap<>();
    HashMap<MetaPortal, PortalButton> portalButtons = new HashMap<>();

    ShapeRenderer shapeRenderer;
    Preferences preferences;
    float anim = 0;
    float ANIM_DURATION = 4;
    Color arrowActualArrowColor;

    public ConnectionGraph(Project project, ConnectionEditor connectionEditor, EditorResources resources, ShapeRenderer shapeRenderer) {
        super(new ScreenViewport());
        this.project = project;
        this.connectionEditor = connectionEditor;
        this.resources = resources;
        this.shapeRenderer = shapeRenderer;

        preferences = Gdx.app.getPreferences(project.getName());

        OrthographicCamera camera = (OrthographicCamera) getCamera();
        camera.position.x = preferences.getFloat("camera.x", 0);
        camera.position.y = preferences.getFloat("camera.y", 0);
        camera.zoom = preferences.getFloat("camera.zoom", 1);
        camera.update();

        update(false);

        preferences.flush();
    }

    public void cancelConnection() {
        portalButtonA = null;
    }

    private void addConnection(PortalButton portalButtonB) {
        if (portalButtonA == portalButtonB) {
            return;
        }

        project.getPortalConnector().addConnection(portalButtonA.getMetaPortal(), portalButtonB.getMetaPortal());
        portalButtonA = null;
    }

    private void removeConnection() {
        project.getPortalConnector().removeConnectionWith(portalButtonA.getMetaPortal());
        portalButtonA = null;
    }

    public void save() {
        preferences.clear();

        preferences.putFloat("camera.x", getCamera().position.x);
        preferences.putFloat("camera.y", getCamera().position.y);
        preferences.putFloat("camera.zoom", ((OrthographicCamera) getCamera()).zoom);

        for (MetaLevelBlock metaLevelBlock : blocks.values()) {
            preferences.putFloat(metaLevelBlock.getMetaLevel().getName() + ".x", metaLevelBlock.getX());
            preferences.putFloat(metaLevelBlock.getMetaLevel().getName() + ".y", metaLevelBlock.getY());
        }

        preferences.flush();
    }

    public void update(boolean savePositions) {
        if (savePositions) {
            save();
        }

        blocks.clear();
        portalButtons.clear();
        clear();

        Collection<MetaLevel> metaLevels = project.getMetaLevels();
        for (MetaLevel metaLevel : metaLevels) {
            MetaLevelBlock block = new MetaLevelBlock(metaLevel, resources, Color.RED) {
                @Override
                public void onPortalClicked(PortalButton button) {
                    button.getSlot().highlight();
                    if (portalButtonA == null) {
                        portalButtonA = button;

                        if (connectionEditor.mode == ConnectionEditor.Mode.DISCONNECT) {
                            removeConnection();
                        }
                    } else {
                        if (connectionEditor.mode == ConnectionEditor.Mode.CONNECT) {
                            addConnection(button);
                        } else {
                            portalButtonA = null;
                        }
                    }
                }

                @Override
                public void onRequestDelete() {

                }
            };

            float x = preferences.getFloat(metaLevel.getName() + ".x", 0);
            float y = preferences.getFloat(metaLevel.getName() + ".y", 0);
            block.setPosition(x, y);
            blocks.put(metaLevel, block);
            portalButtons.putAll(block.getPortalButtonHashMap());
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

        shapeRenderer.setColor(Color.GRAY);
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

        shapeRenderer.line(
                lineStart.x,
                lineStart.y,
                lineEnd.x,
                lineEnd.y,
                EVENT_COLOR,
                HANDLER_COLOR);

        float length = bPosition.cpy().sub(aPosition).len();
        float theta = Interpolation.circleOut.apply(0, 1, anim / ANIM_DURATION);
        Vector2 direction = bPosition.cpy().sub(aPosition).nor();
        Vector2 arrowPos = direction.cpy().scl(length * (theta));
        arrowPos.add(aPosition);
        Vector2 arrowEnd = arrowPos.cpy().sub(direction.scl(30));

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


        switch (connectionEditor.getMode()) {
            case CONNECT:
                Vector2 cursorPosition2 = new Vector2(cursorPos.x, cursorPos.y);

                Vector2 arrowPos = startPosition.cpy().add(cursorPosition2).scl(0.5f);
                Vector2 direction = cursorPosition2.cpy().sub(startPosition).nor();
                Vector2 arrowEnd = arrowPos.cpy().add(direction.scl(eventFirst ? -30 : 30));

                Color startColor;
                Color endColor;

                if (eventFirst) {
                    startColor = EVENT_COLOR;
                    endColor = HANDLER_COLOR;
                } else {
                    startColor = HANDLER_COLOR;
                    endColor = EVENT_COLOR;
                }

                shapeRenderer.line(startPosition.x, startPosition.y,
                        cursorPos.x,
                        cursorPos.y,
                        startColor,
                        endColor);

                shapeRenderer.setColor(ARROW_COLOR);
                arrowEnd.rotateAround(arrowPos, 30);
                shapeRenderer.line(arrowPos, arrowEnd);

                arrowEnd.rotateAround(arrowPos, -60);
                shapeRenderer.line(arrowPos, arrowEnd);

                break;
            case DISCONNECT:
                shapeRenderer.line(startPosition.x, startPosition.y,
                        cursorPos.x,
                        cursorPos.y,
                        REMOVE_COLOR,
                        REMOVE_COLOR);
                break;
        }
    }

    private void drawLines() {
        shapeRenderer.setColor(Color.WHITE);
        Gdx.gl20.glLineWidth(3);

        Set<Map.Entry<MetaPortal, MetaPortal>> connections = project.getPortalConnector().getConnections();
        for (Map.Entry<MetaPortal, MetaPortal> entry : connections) {
            PortalButton portalButtonA;
            PortalButton portalButtonB;

            portalButtonA = portalButtons.get(entry.getKey());
            portalButtonB = portalButtons.get(entry.getValue());
            drawLine(portalButtonA.getSlot(), portalButtonB.getSlot());
        }

        Vector3 cursorPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cursorPos = getCamera().unproject(cursorPos);

        if (portalButtonA != null) {
            drawLineFromActor(portalButtonA.getSlot(), cursorPos, true);
        }
        shapeRenderer.end();
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
