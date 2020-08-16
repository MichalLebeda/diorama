package cz.shroomware.diorama.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import cz.shroomware.diorama.DioramaGame;

public class Hud extends Stage {

    TextureAtlas atlas;

    public Hud(DioramaGame game, TextureAtlas atlas) {
        super();
        setDebugAll(true);

        setViewport(new ScreenViewport());
        getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        getCamera().translate(getViewport().getWorldWidth() / 2,
                getViewport().getWorldHeight() / 2,
                0);

        VerticalGroup itemGroup = new VerticalGroup();

        for (int i = 0; i < atlas.getRegions().size; i++) {
            itemGroup.addActor(new Item(game, atlas.getRegions().get(i)));
        }

        itemGroup.columnAlign(Align.right);

        ScrollPane scrollPane = new ScrollPane(itemGroup);
        scrollPane.pack();
        scrollPane.setHeight(getViewport().getWorldHeight());
        scrollPane.setPosition(
                getViewport().getWorldWidth() - scrollPane.getWidth(),
                0);

        addActor(scrollPane);

        addActor(new Image(atlas.findRegion("floor")));
    }

    @Override
    public void dispose() {
        super.dispose();
        atlas.dispose();
    }
}
