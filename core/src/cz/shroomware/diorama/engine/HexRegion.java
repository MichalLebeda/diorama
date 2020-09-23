package cz.shroomware.diorama.engine;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class HexRegion {
    protected HashMap<String, TextureRegion> topRegions = new HashMap<>();

    public HexRegion(TextureAtlas atlas, String name) {
        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    for (int d = 0; d < 2; d++) {
                        String code = (a == 0 ? "x" : "o") +
                                (b == 0 ? "x" : "o") +
                                (c == 0 ? "x" : "o") +
                                (d == 0 ? "x" : "o");

                        TextureRegion region = atlas.findRegion(name + code);

                        topRegions.put(code, region);
                    }
                }
            }
        }
    }

    public int size() {
        return topRegions.size();
    }

    public TextureRegion get(String code) {
        return topRegions.get(code);
    }

    public TextureRegion get(boolean a, boolean b, boolean c, boolean d) {
        String code = (a ? "x" : "o") +
                (b ? "x" : "o") +
                (c ? "x" : "o") +
                (d ? "x" : "o");
        return topRegions.get(code);
    }
}
