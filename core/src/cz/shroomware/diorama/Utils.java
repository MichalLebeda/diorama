package cz.shroomware.diorama;

public class Utils {
    public static float round(float input, float step)
    {
        return ((Math.round(input / step)) * step);
    }
}
