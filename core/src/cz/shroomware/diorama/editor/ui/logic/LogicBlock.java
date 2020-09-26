package cz.shroomware.diorama.editor.ui.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.editor.EditorResources;
import cz.shroomware.diorama.engine.level.logic.Event;
import cz.shroomware.diorama.engine.level.logic.Handler;
import cz.shroomware.diorama.engine.level.logic.LogicComponent;
import cz.shroomware.diorama.ui.DFLabel;

public abstract class LogicBlock extends VerticalGroup {
    protected LogicComponent logicComponent;
    protected Vector2 relativeDragPos = new Vector2();
    protected Drawable background;

    HashMap<Handler, HandlerButton> handlerButtonHashMap = new HashMap<>();
    HashMap<Event, EventButton> eventButtonHashMap = new HashMap<>();

    public LogicBlock(LogicComponent logicComponent,
                      EditorResources editorResources,
                      Color eventColor,
                      Color handlerColor) {
        this.logicComponent = logicComponent;

        setTouchable(Touchable.enabled);
        pad(15);
        space(10);

        DFLabel label = new DFLabel(editorResources.getSkin(), editorResources.getDfShader(), logicComponent.hasId() ? logicComponent.getId() : logicComponent.toString()
        );
        addActor(label);
        label.setFontScale(0.4f);

        HorizontalGroup horizontalGroup = new HorizontalGroup();

        VerticalGroup verticalGroup = new VerticalGroup();
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
                handlerButton.addListener(new DragListener() {
                    @Override
                    public void dragStart(InputEvent event, float x, float y, int pointer) {
                        onButtonDragStart();
                    }

                    @Override
                    public void dragStop(InputEvent event, float x, float y, int pointer) {
                        event.cancel();
                        onButtonDragStart();
                    }
                });
                handlerButtonHashMap.put(handler, handlerButton);
                verticalGroup.addActor(handlerButton);
            }
        }

        horizontalGroup.addActor(verticalGroup);

        verticalGroup = new VerticalGroup();
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
                eventButton.addListener(new DragListener() {
                    @Override
                    public void dragStart(InputEvent event, float x, float y, int pointer) {
                        onButtonDragStart();
                    }

                    @Override
                    public void dragStop(InputEvent event, float x, float y, int pointer) {
                        event.cancel();
                        onButtonDragStart();
                    }
                });
                eventButtonHashMap.put(event, eventButton);
                verticalGroup.addActor(eventButton);
            }
        }
        horizontalGroup.addActor(verticalGroup);

        addActor(horizontalGroup);

        pack();
        layout();

        addListener(new DragListener() {
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                relativeDragPos.set(event.getStageX() - getX(), event.getStageY() - getY());
                onButtonDragStart();
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                setPosition(getX() - relativeDragPos.x + x, getY() - relativeDragPos.y + y);
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

    public abstract void onEventClicked(EventButton button);

    public abstract void onHandlerClicked(HandlerButton button);

    public abstract void onButtonDragStart();
}
