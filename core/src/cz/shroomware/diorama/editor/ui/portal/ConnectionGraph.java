package cz.shroomware.diorama.editor.ui.portal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
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
    private static final float LINE_WIDTH = 4;
    private static final float CROSS_SIZE = 40;
    private static final Color CROSS_COLOR = new Color(0x252525FF);
    private static final Color COLOR = new Color(0xF0F0F0FF);

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

    float zoom;
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

    Vector2 tmpVec = new Vector2();

    public void update(boolean savePositions) {
        if (savePositions) {
            save();
        }

        blocks.clear();
        portalButtons.clear();
        clear();

        Collection<MetaLevel> metaLevels = project.getMetaLevels();
        for (MetaLevel metaLevel : metaLevels) {
            MetaLevelBlock block = new MetaLevelBlock(metaLevel, resources, COLOR) {
                @Override
                public void onPortalClicked(PortalButton button) {
                    button.highlight();
                    if (portalButtonA == null) {
                        portalButtonA = button;
                        portalButtonA.setColor(2, 2, 2, 1);

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

        OrthographicCamera camera = (OrthographicCamera) getCamera();
        camera.update();
        zoom = camera.zoom;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(CROSS_COLOR);
        shapeRenderer.rectLine(-CROSS_SIZE, 0, CROSS_SIZE, 0, LINE_WIDTH * zoom);
        shapeRenderer.rectLine(0, -CROSS_SIZE, 0, CROSS_SIZE, LINE_WIDTH * zoom);

        shapeRenderer.end();

        super.draw();

        shapeRenderer.setProjectionMatrix(getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (connectionEditor.getMode() == ConnectionEditor.Mode.DISCONNECT) {
            cancelConnection();
        }

        drawLines();
        shapeRenderer.end();
    }

    private void drawLine(Vector2 start, Vector2 end, float bezierA, float bezierB) {
        Vector2 p1 = new Vector2(start);
        Vector2 p2 = new Vector2(start.cpy().add(bezierA, 0));
        Vector2 p3 = new Vector2(end.cpy().add(bezierB, 0));
        Vector2 p4 = new Vector2(end);
        Bezier<Vector2> bezier = new Bezier<>(p1, p2, p3, p4);

        float sample = 1 / 20f;

        float state = 0;

        Vector2 bezierPos = new Vector2();
        bezier.valueAt(bezierPos, state);

        Vector2 oldPos = bezierPos.cpy();
        while (state < 1) {
            state += sample;
            if (state > 1) {
                state = 1;
            }

            bezier.valueAt(bezierPos, state);
            shapeRenderer.rectLine(oldPos.x, oldPos.y, bezierPos.x, bezierPos.y, LINE_WIDTH * zoom);
            oldPos.set(bezierPos);
        }
    }

    private void drawLines() {
        shapeRenderer.setColor(COLOR);

        Set<Map.Entry<MetaPortal, MetaPortal>> connections = project.getPortalConnector().getConnections();
        for (Map.Entry<MetaPortal, MetaPortal> entry : connections) {

            PortalButton portalButtonA = portalButtons.get(entry.getKey());
            PortalButton portalButtonB = portalButtons.get(entry.getValue());

            Actor parentA = portalButtonA.getMetaLevelBlock();
            Actor parentB = portalButtonB.getMetaLevelBlock();

            if (parentA.getX() + parentA.getWidth() / 2 > parentB.getX() + parentB.getWidth() / 2) {
                Vector2 firstSlotPosition = getGlobalCenterPos(portalButtonA);
                Vector2 secondSlotPosition = getGlobalCenterPos(portalButtonB);

                firstSlotPosition.x -= portalButtonA.getWidthPad() / 2;
                secondSlotPosition.x += portalButtonB.getWidthPad() / 2;

                drawLine(firstSlotPosition, secondSlotPosition, -200, 200);
                drawCircleAt(firstSlotPosition);
                drawCircleAt(secondSlotPosition);
            } else {
                Vector2 firstSlotPosition = getGlobalCenterPos(portalButtonA);
                Vector2 secondSlotPosition = getGlobalCenterPos(portalButtonB);

                firstSlotPosition.x += portalButtonA.getWidthPad() / 2;
                secondSlotPosition.x -= portalButtonB.getWidthPad() / 2;

                drawLine(firstSlotPosition, secondSlotPosition, 200, -200);
                drawCircleAt(firstSlotPosition);
                drawCircleAt(secondSlotPosition);
            }
        }

        Vector3 cursorPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cursorPos = getCamera().unproject(cursorPos);
        Vector2 cursorPos2 = new Vector2(cursorPos.x, cursorPos.y);

        if (portalButtonA != null) {
            Vector2 portalButtonCenter = getGlobalCenterPos(portalButtonA);

            Vector2 firstSlotPosition;
            float bezierA;
            float bezierB;
            if (portalButtonCenter.x > cursorPos.x) {
                firstSlotPosition = getGlobalCenterPos(portalButtonA);
                firstSlotPosition.x -= portalButtonA.getWidthPad() / 2;
                bezierA = -200;
                bezierB = 200;
            } else {
                firstSlotPosition = getGlobalCenterPos(portalButtonA);
                firstSlotPosition.x += portalButtonA.getWidthPad() / 2;
                bezierA = 200;
                bezierB = -200;
            }

            if (cursorPos.x > portalButtonCenter.x + portalButtonA.getWidthPad() / 2
                    || cursorPos.x < portalButtonCenter.x - portalButtonA.getWidthPad() / 2
                    || cursorPos.y > portalButtonCenter.y + portalButtonA.getHeightPad() / 2
                    || cursorPos.y < portalButtonCenter.y - portalButtonA.getHeightPad() / 2) {
                drawLine(firstSlotPosition, cursorPos2, bezierA, bezierB);
                drawCircleAt(firstSlotPosition);
            }
        }
        shapeRenderer.end();
    }

    protected void drawCircleAt(Vector2 pos) {
        shapeRenderer.circle(pos.x, pos.y, 8);
    }

    protected Vector2 getGlobalCenterPos(Actor actor) {
        tmpVec.set(0, 0);
        actor.localToStageCoordinates(tmpVec);
        tmpVec.add(actor.getWidth() * actor.getScaleX() / 2,
                actor.getHeight() * actor.getScaleY() / 2);

        return tmpVec.cpy();
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
