package cz.shroomware.diorama.editor.ui.portal;

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

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.editor.ui.IconButton;
import cz.shroomware.diorama.engine.level.MetaLevel;
import cz.shroomware.diorama.engine.level.portal.MetaPortal;
import cz.shroomware.diorama.ui.DFLabel;

public abstract class MetaLevelBlock extends VerticalGroup {
    protected MetaLevel metaLevel;
    protected Vector2 relativeDragPos = new Vector2();
    protected Drawable background;
    protected IconButton deleteButton = null;

    HashMap<MetaPortal, PortalButton> portalButtonHashMap = new HashMap<>();
    boolean draggedBefore = false;

    public MetaLevelBlock(MetaLevel metaLevel,
                          final EditorResources editorResources,
                          Color color) {
        this.metaLevel = metaLevel;

        setTouchable(Touchable.enabled);
        pad(15);
        space(10);

        DFLabel label = new DFLabel(editorResources.getSkin(), editorResources.getDfShader(), metaLevel.getName());
        label.setFontScale(0.4f);
        addActor(label);

        final VerticalGroup portalVerticalGroup = new VerticalGroup();
        portalVerticalGroup.columnAlign(Align.left);

        Collection<MetaPortal> portals = metaLevel.getMetaPortals().getValues();
        for (MetaPortal portal : portals) {
            final PortalButton portalButton = new PortalButton(editorResources, portal, color);
            portalButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onPortalClicked(portalButton);
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

                if (deleteButton != null) {
                    Vector2 click = new Vector2(event.getStageX(), event.getStageY());
                    click = deleteButton.stageToLocalCoordinates(click);
                    if (deleteButton.hit(click.x, click.y, false) != null) {
                        Gdx.app.error("LogicBlock", "possible interception");
                        return true;
                    }
                }

                return false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);

                if (!draggedBefore) {
                    Gdx.app.error("LogicBlock", "Button will intercept click");
                }
                draggedBefore = true;
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
