package cz.michallebeda.diorama.editor.history.actions;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import cz.michallebeda.diorama.engine.level.Tile;

public class BucketTileAction implements HistoryAction {
    Array<PlaceTileAction> affectedTilesActions = new Array<>();

    public void add(Tile tile, TextureRegion from, TextureRegion to) {
        affectedTilesActions.add(new PlaceTileAction(tile, from, to));
    }

    @Override
    public void undo() {
        for (PlaceTileAction placeTileAction : affectedTilesActions) {
            placeTileAction.undo();
        }
    }

    @Override
    public void redo() {
        for (PlaceTileAction placeTileAction : affectedTilesActions) {
            placeTileAction.redo();
        }
    }

    @Override
    public String getText() {
        return "bucket " + affectedTilesActions.size + " tiles";
    }
}
