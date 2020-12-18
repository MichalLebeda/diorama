package cz.shroomware.diorama;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Utils {
    public static final boolean RAIN = false;

    public static final boolean SSAO = false;

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

    public static final int PLAYER_ID = -2;

    public static final float CAMERA_LEVEL = 1f;

    public static final float Z_OFFSET_PER_METER = 0;

    public static final float AIM_SENSITIVITY = 0.5f;

    public static final Color color = new Color();

    public static float round(float input, float step) {
        return ((Math.round(input / step)) * step);
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
