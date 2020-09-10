package cz.shroomware.diorama.engine;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile extends Sprite {
    protected TextureRegion region;
    private int xIndex, yIndex;

    public Tile(int x, int y, TextureRegion region) {
        super(region);
        this.xIndex = x;
        this.yIndex = y;
        this.region = region;
    }

    public int getXIndex() {
        return xIndex;
    }

    public int getYIndex() {
        return yIndex;
    }

    public TextureRegion getRegion() {
        return region;
    }

    @Override
    public void setRegion(TextureRegion region) {
        super.setRegion(region);
        this.region = region;
    }
}
