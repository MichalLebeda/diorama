package cz.michallebeda.diorama.editor.history.actions;


import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cz.michallebeda.diorama.engine.level.Tile;

public class PlaceTileAction implements HistoryAction {
    Tile tile;
    TextureRegion from, to;

    public PlaceTileAction(Tile tile, TextureRegion from, TextureRegion to) {
        this.tile = tile;
        this.from = from;
        this.to = to;
    }

    @Override
    public void undo() {
        tile.setRegion(from);
    }

    @Override
    public void redo() {
        tile.setRegion(to);
    }

    @Override
    public String getText() {
        return "setTile " + tile.getX() + " " + tile.getY();
    }
}
