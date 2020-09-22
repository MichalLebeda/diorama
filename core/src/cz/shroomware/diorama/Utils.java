package cz.shroomware.diorama;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final String DARK_BACKGROUND_DRAWABLE = "dark-background";

    public static final String ITEM_MODE_ICON_DRAWABLE = "item_mode_icon";

    public static final String TILE_MODE_ICON_DRAWABLE = "tile_mode_icon";

    public static final String TILE_BUCKET_MODE_ICON_DRAWABLE = "tile_bucket_mode_icon";

    public static final String DELETE_MODE_ICON_DRAWABLE = "delete_mode_icon";

    public static final float PIXELS_PER_METER = 16f;

    public static final float SHADOW_SCALE = 2;

    public static final String PROJECT_FOLDER = "Documents/PixelLab/";

    public static float round(float input, float step) {
        return ((Math.round(input / step)) * step);
    }

    public static int pixel  = 0;

    //https://stackoverflow.com/questions/29451787/libgdx-textureregion-to-pixmap
    public static Pixmap extractPixmapFromTextureRegion(TextureRegion textureRegion) {
        TextureData textureData = textureRegion.getTexture().getTextureData();
        if (!textureData.isPrepared()) {
            textureData.prepare();
        }
        Pixmap pixmap = new Pixmap(
                textureRegion.getRegionWidth(),
                textureRegion.getRegionHeight(),
                textureData.getFormat()
        );
        pixmap.drawPixmap(
                textureData.consumePixmap(), // The other Pixmap
                0, // The target x-coordinate (top left corner)
                0, // The target y-coordinate (top left corner)
                textureRegion.getRegionX(), // The source x-coordinate (top left corner)
                textureRegion.getRegionY(), // The source y-coordinate (top left corner)
                textureRegion.getRegionWidth(), // The width of the area from the other Pixmap in pixels
                textureRegion.getRegionHeight() // The height of the area from the other Pixmap in pixels
        );
        return pixmap;
    }

    public static Color getDominantColor(Pixmap pixmap) {
        HashMap<Integer, Integer> occurrences = new HashMap<>();

        // Iterate through pixmap and recorde every occurence of color
        for (int x = 0; x < pixmap.getWidth(); x++) {
            for (int y = 0; y < pixmap.getHeight(); y++) {
                int pixel = pixmap.getPixel(x, y);
                if (occurrences.containsKey(pixel)) {
                    Integer occurrence = occurrences.get(pixel);
                    //TODO: could be done better but looks clean so...
                    occurrence++;
                    occurrences.put(pixel, occurrence);
                } else {
                    occurrences.put(pixel, 1);
                }
            }
        }

        // Pick color with most occurrences
        Map.Entry<Integer, Integer> max = null;
        for (Map.Entry<Integer, Integer> entry : occurrences.entrySet()) {
            if (max == null || entry.getValue() > max.getValue()) {
                max = entry;
            }
        }

        return new Color(max.getKey());
    }

    public static Color getDominantOpaqueColor(Pixmap pixmap) {
        HashMap<Integer, Integer> occurrences = new HashMap<>();

        // Iterate through pixmap and recorde every occurence of color
        for (int x = 0; x < pixmap.getWidth(); x++) {
            for (int y = 0; y < pixmap.getHeight(); y++) {
                int pixel = pixmap.getPixel(x, y);
                if (pixel == 0) {
                    continue;
                }
                Gdx.app.log("color", Integer.toHexString(pixel));
                if (occurrences.containsKey(pixel)) {
                    Integer occurrence = occurrences.get(pixel);
                    //TODO: could be done better but looks clean so...
                    occurrence++;
                    occurrences.put(pixel, occurrence);
                } else {
                    occurrences.put(pixel, 1);
                }
            }
        }

        // Pick color with most occurrences
        Map.Entry<Integer, Integer> max = null;
        for (Map.Entry<Integer, Integer> entry : occurrences.entrySet()) {
            if (max == null || entry.getValue() > max.getValue()) {
                max = entry;
            }
        }

        return new Color(max.getKey());
    }

    public static FileHandle getFileHandle(String filename) {
        return Gdx.files.external(Utils.PROJECT_FOLDER + filename);
    }
}
