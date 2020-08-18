package cz.shroomware.diorama;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public abstract class SelectedItemIndicator extends Image {
    TextureAtlas.AtlasRegion region = null;
    TextureAtlas atlas;

    public SelectedItemIndicator() {
        setSize(100, 100);
    }

    public void setItemRegion(TextureAtlas.AtlasRegion region) {
        this.region = region;
        setDrawable(new TextureRegionDrawable(region));
        onSelectedItemRegion(region);
    }

    public TextureAtlas.AtlasRegion getSelectedItemRegion(){
        return region;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (region != null)
            super.draw(batch, parentAlpha);
    }

    public abstract void onSelectedItemRegion(TextureAtlas.AtlasRegion region);
}
