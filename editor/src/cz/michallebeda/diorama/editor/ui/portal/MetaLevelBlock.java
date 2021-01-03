package cz.michallebeda.diorama.editor.ui.portal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import java.util.Collection;
import java.util.HashMap;

import cz.michallebeda.diorama.Utils;
import cz.michallebeda.diorama.editor.EditorResources;
import cz.michallebeda.diorama.editor.ui.DFLabel;
import cz.michallebeda.diorama.engine.level.MetaLevel;
import cz.michallebeda.diorama.engine.level.portal.MetaPortal;

public abstract class MetaLevelBlock extends VerticalGroup {
    private static final float MAX_DRAG_FOR_CLICK = 20;
    protected MetaLevel metaLevel;
    protected Vector2 relativeDragPos = new Vector2();
    protected Drawable background;

    HashMap<MetaPortal, PortalButton> portalButtonHashMap = new HashMap<>();
    boolean draggedBefore = false;

    float dragged = 0;
    Vector2 lastDraggedPos = new Vector2();

    public MetaLevelBlock(MetaLevel metaLevel,
                          final EditorResources editorResources,
                          Color color) {
        this.metaLevel = metaLevel;

        setTouchable(Touchable.enabled);
        pad(30);
        space(20);

        DFLabel label = new DFLabel(editorResources.getSkin(), editorResources.getDfShader(), metaLevel.getName());
        label.setFontScale(0.4f);
        addActor(label);

        final VerticalGroup portalVerticalGroup = new VerticalGroup();
        portalVerticalGroup.columnAlign(Align.center);
        portalVerticalGroup.space(24);

        Collection<MetaPortal> portals = metaLevel.getMetaPortals().getValues();
        for (MetaPortal portal : portals) {
            final PortalButton portalButton = new PortalButton(this, editorResources, portal, color);
            portalButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!draggedBefore || dragged < MAX_DRAG_FOR_CLICK) {
                        onPortalClicked(portalButton);
                    }
                }
            });
            portalButtonHashMap.put(portal, portalButton);
            portalVerticalGroup.addActor(portalButton);
        }
        addActor(portalVerticalGroup);

        pack();
        layout();

        addListener(new DragListener() {
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                relativeDragPos.set(event.getStageX() - getX(), event.getStageY() - getY());
                event.handle();
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                setPosition(getX() - relativeDragPos.x + x, getY() - relativeDragPos.y + y);
                event.handle();
            }
        });

        addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                draggedBefore = false;
                dragged = 0;
                lastDraggedPos.set(x, y);

                Vector2 click = new Vector2(event.getStageX(), event.getStageY());
                click = portalVerticalGroup.stageToLocalCoordinates(click);
                if (portalVerticalGroup.hit(click.x, click.y, false) != null) {
                    Gdx.app.log("LogicBlock", "possible drag");
                    return true;
                }

                return false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);

                if (!draggedBefore) {
                    Gdx.app.log("LogicBlock", "Button drag, no click will be registered");
                }
                lastDraggedPos.sub(x, y);
                dragged += lastDraggedPos.len();
                lastDraggedPos.set(x, y);
                draggedBefore = true;

                Gdx.app.log("dragged", dragged + "");
            }
        });

        background = editorResources.getSkin().getDrawable(Utils.DARK_BACKGROUND_DRAWABLE);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        background.draw(batch, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }

    public HashMap<MetaPortal, PortalButton> getPortalButtonHashMap() {
        return portalButtonHashMap;
    }

    public MetaLevel getMetaLevel() {
        return metaLevel;
    }

    public abstract void onPortalClicked(PortalButton button);

    public abstract void onRequestDelete();
}
