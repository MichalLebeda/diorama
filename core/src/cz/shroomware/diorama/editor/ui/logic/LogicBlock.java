package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.component.LogicComponent;
import cz.shroomware.diorama.engine.level.logic.component.LogicOperator;
import cz.shroomware.diorama.ui.DFLabel;

public abstract class LogicBlock extends VerticalGroup {
    protected LogicComponent logicComponent;
    protected Vector2 relativeDragPos = new Vector2();
    protected Drawable background;
    protected Button deleteButton = null;

    HashMap<Handler, HandlerButton> handlerButtonHashMap = new HashMap<>();
    HashMap<Event, EventButton> eventButtonHashMap = new HashMap<>();
    boolean draggedBefore = false;

    public LogicBlock(LogicComponent logicComponent,
                      final EditorResources editorResources,
                      Color eventColor,
                      Color handlerColor) {
        this.logicComponent = logicComponent;

        setTouchable(Touchable.enabled);
        pad(15);
        space(10);

        Identifier identifier = logicComponent.getIdentifier();
        DFLabel label = new DFLabel(editorResources.getSkin(),
                editorResources.getDfShader(),
                identifier.isSet() ? identifier.getIdString() : logicComponent.toString());
        label.setFontScale(0.4f);
        Table topTable = null;
        if (logicComponent instanceof LogicOperator) {
            topTable = new Table();
            label.setAlignment(Align.center);
            topTable.setWidth(getWidth());
            topTable.add(label).center().grow();

            //TODO SEPARATE CLASS
            deleteButton = new Button(editorResources.getSkin()) {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    super.draw(batch, parentAlpha);

                    TextureRegion crossRegion = editorResources.getUiAtlas().findRegion("cross");
                    float PAD = 20;
                    batch.draw(crossRegion, getX() + PAD,
                            getY() + PAD,
                            getWidth() - 2 * PAD,
                            getHeight() - 2 * PAD);
                }
            };

            deleteButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!draggedBefore) {
                        onRequestDelete();
                    }
                }
            });

            topTable.add(deleteButton);
            addActor(topTable);

        } else {
            addActor(label);
        }

        final HorizontalGroup horizontalGroup = new HorizontalGroup();
        horizontalGroup.space(30);

        final VerticalGroup handlerVerticalGroup = new VerticalGroup();
        handlerVerticalGroup.columnAlign(Align.left);
        Array<Handler> handlers = logicComponent.getHandlers();
        if (handlers != null) {
            for (Handler handler : handlers) {
                final HandlerButton handlerButton = new HandlerButton(editorResources, handler, handlerColor);
                handlerButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        onHandlerClicked(handlerButton);
                    }
                });
                handlerButtonHashMap.put(handler, handlerButton);
                handlerVerticalGroup.addActor(handlerButton);
            }
        }
        horizontalGroup.addActor(handlerVerticalGroup);

        final VerticalGroup eventVerticalGroup = new VerticalGroup();
        eventVerticalGroup.columnAlign(Align.right);
        Array<Event> events = logicComponent.getEvents();
        if (events != null) {
            for (Event event : events) {
                final EventButton eventButton = new EventButton(editorResources, event, eventColor);
                eventButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        onEventClicked(eventButton);
                    }
                });
                eventButtonHashMap.put(event, eventButton);
                eventVerticalGroup.addActor(eventButton);
            }
        }
        horizontalGroup.addActor(eventVerticalGroup);

        addActor(horizontalGroup);

        pack();
        layout();

        if (topTable != null) {
            topTable.setWidth(horizontalGroup.getWidth());
            topTable.setX(horizontalGroup.getX());
        }

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

    public HashMap<Event, EventButton> getEventButtonHashMap() {
        return eventButtonHashMap;
    }

    public HashMap<Handler, HandlerButton> getHandlerButtonHashMap() {
        return handlerButtonHashMap;
    }

    public LogicComponent getLogicComponent() {
        return logicComponent;
    }

    public abstract void onEventClicked(EventButton button);

    public abstract void onHandlerClicked(HandlerButton button);

    public abstract void onRequestDelete();
}
