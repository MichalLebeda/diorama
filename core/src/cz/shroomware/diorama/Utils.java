package cz.shroomware.diorama;

public class Utils {
    public static final float PIXELS_PER_METER = 16f;

    public static float round(float input, float step)
    {
        return ((Math.round(input / step)) * step);
    }
}
