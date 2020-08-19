package cz.shroomware.diorama.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import cz.shroomware.diorama.DioramaGame;
import cz.shroomware.diorama.SelectedItemIndicator;

public abstract class Hud extends Stage {
    TextureAtlas atlas;
    SelectedItemIndicator selectedItemIndicator;
    ScrollPane scrollPane;

    public Hud(DioramaGame game, TextureAtlas sourceAtlas) {
        super();
//        setDebugAll(true);

        setViewport(new ScreenViewport());
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        getCamera().translate(getViewport().getWorldWidth() / 2,
                getViewport().getWorldHeight() / 2,
                0);

        selectedItemIndicator = new SelectedItemIndicator() {
            @Override
            public void onSelectedItemRegion(TextureAtlas.AtlasRegion region) {
                Hud.this.onSelectedItemRegion(region);
            }
        };

        VerticalGroup itemGroup = new VerticalGroup();
        for (int i = 0; i < sourceAtlas.getRegions().size; i++) {
            itemGroup.addActor(new Item(game, sourceAtlas.getRegions().get(i), selectedItemIndicator));
        }
        itemGroup.columnAlign(Align.right);
        itemGroup.pad(20);
        itemGroup.space(20);

        scrollPane = new ScrollPane(itemGroup);
        scrollPane.getStyle().background = new TextureRegionDrawable(game.getUiAtlas().findRegion("translucent")); //TODO FIX IN STYLE
        scrollPane.pack();
        scrollPane.setHeight(getViewport().getWorldHeight());
        scrollPane.setPosition(
                getViewport().getWorldWidth() - scrollPane.getWidth(),
                0);

        addActor(scrollPane);

        selectedItemIndicator.setPosition(scrollPane.getX() - selectedItemIndicator.getWidth(),
                getHeight() - selectedItemIndicator.getHeight());

        addActor(selectedItemIndicator);

    }

    public Vector3 getMenuScreenPosition() {
        Vector3 position = new Vector3();
        position.set(scrollPane.getX(), scrollPane.getY(), 0);
        return getCamera().project(position);
    }

    public abstract void onSelectedItemRegion(TextureAtlas.AtlasRegion region);

    public TextureRegion getSelectedRegion() {
        return selectedItemIndicator.getSelectedItemRegion();
    }

    public boolean hasSelectedRegion() {
        return selectedItemIndicator.getSelectedItemRegion() != null;
    }

    @Override
    public void dispose() {
        super.dispose();
        atlas.dispose();
    }
}
