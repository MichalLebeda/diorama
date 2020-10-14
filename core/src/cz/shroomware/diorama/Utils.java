package cz.shroomware.diorama;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final boolean RAIN = false;

    public static final String DARK_BACKGROUND_DRAWABLE = "dark-background";

    public static final String DARK_BACKGROUND_PRESSED_DRAWABLE = "dark-background-pressed";

    public static final String ITEM_MODE_ICON_DRAWABLE = "item_mode_icon";

    public static final String ITEM_MOVE_MODE_ICON_DRAWABLE = "item_move_mode_icon";

    public static final String TILE_MODE_ICON_DRAWABLE = "tile_mode_icon";

    public static final String TILE_BUCKET_MODE_ICON_DRAWABLE = "tile_bucket_mode_icon";

    public static final String DELETE_MODE_ICON_DRAWABLE = "delete_mode_icon";

    public static final String CONNECT_MODE_ICON_DRAWABLE = "connect_mode_icon";

    public static final String DISCONNECT_MODE_ICON_DRAWABLE = "disconnect_mode_icon";

    public static final String PORTAL_MODE_ICON_DRAWABLE = "portal_mode_icon";

    public static final String ID_ASSIGN_MODE_ICON_DRAWABLE = "id_assign_mode_icon";

    public static final String SNAP_ON_ICON_DRAWABLE = "snap_on_icon";

    public static final String SNAP_OFF_ICON_DRAWABLE = "snap_off_icon";

    public static final String LABELS_ON_ICON_DRAWABLE = "labels_on_icon";

    public static final String LABELS_OFF_ICON_DRAWABLE = "labels_off_icon";

    public static final String START_PORTAL = "start";

    public static final float PIXELS_PER_METER = 16f;

    public static final float SHADOW_SCALE = 2;

    public static final int INIT_ID = -1;

    public static float round(float input, float step) {
        return ((Math.round(input / step)) * step);
    }

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

    //TODO: handle rotation
    public static Vector2 roundPosition(Vector2 worldPos, float width) {
        worldPos = worldPos.cpy();
        worldPos.x = Utils.round(worldPos.x, 1f / 16f);
        if ((int) width % 2 == 1) {
            worldPos.x += 0.5f / 16f;
        }
        worldPos.y = Utils.round(worldPos.y, 1f / 16f);

        return worldPos;
    }

    public static float calculateCameraViewportHeight() {
        return 20;
    }

    public static float calculateCameraViewportWidth() {
        double ratio = (double) Gdx.graphics.getWidth() / (double) Gdx.graphics.getHeight();
        return (float) (calculateCameraViewportHeight() * ratio);
    }
}
