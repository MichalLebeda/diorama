package cz.shroomware.diorama.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.HashMap;
import java.util.Map;

import cz.shroomware.diorama.Utils;

public class ColorUtil implements Disposable {
    protected HashMap<Texture, Pixmap> textureToPixmapMap = new HashMap<>();

    public ColorUtil(TextureAtlas atlas) {
        ObjectSet<Texture> textures = atlas.getTextures();

        for (Texture texture : textures) {
            textureToPixmapMap.put(texture, textureToPixmap(texture));
        }
    }

    public int getColorByLocalUv(TextureRegion region, float u, float v) {
        Pixmap pixmap = textureToPixmapMap.get(region.getTexture());

        if (pixmap == null) {
            Gdx.app.error("ColorUtil", "texture not registered");
            return 0;
        }

        if (u < 0 || u > 1 || v < 0 || v > 1) {
            Gdx.app.error("ColorUtil", "bad UV coords");
            return 0;
        }

        int color = pixmap.getPixel(
                (int) (region.getRegionX() + u * region.getRegionWidth()),
                (int) (region.getRegionY() + v * region.getRegionHeight())
        );

        Utils.color.set(color);

        return color;
    }

    public Color getDominantColor(TextureRegion region) {
        Pixmap pixmap = textureToPixmapMap.get(region.getTexture());
        if (pixmap == null) {
            Gdx.app.error("ColorUtil", "texture not registered");
            return Color.WHITE;
        }

        return getDominantColor(pixmap, region);
    }

    public Color getDominantOpaqueColor(TextureRegion region) {
        Pixmap pixmap = textureToPixmapMap.get(region.getTexture());
        if (pixmap == null) {
            Gdx.app.error("ColorUtil", "texture not registered");
            return Color.WHITE;
        }

        return getDominantOpaqueColor(pixmap, region);
    }

    public Color getDominantOpaqueColor(Pixmap pixmap, TextureRegion region) {
        HashMap<Integer, Integer> occurrences = new HashMap<>();

        // Iterate through pixmap and record every occurence of color
        for (int x = region.getRegionX(); x < region.getRegionX() + region.getRegionWidth(); x++) {
            for (int y = region.getRegionY(); y < region.getRegionY() + region.getRegionHeight(); y++) {
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

    public Color getDominantColor(Pixmap pixmap, TextureRegion region) {
        HashMap<Integer, Integer> occurrences = new HashMap<>();

        // Iterate through pixmap and record every occurence of color
        for (int x = region.getRegionX(); x < region.getRegionX() + region.getRegionWidth(); x++) {
            for (int y = region.getRegionY(); y < region.getRegionY() + region.getRegionHeight(); y++) {
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

    @Override
    public void dispose() {
        for (Pixmap pixmap : textureToPixmapMap.values()) {
            pixmap.dispose();
        }

        textureToPixmapMap.clear();
    }

    private Pixmap textureToPixmap(Texture texture) {
        TextureData textureData = texture.getTextureData();

        if (!textureData.isPrepared()) {
            textureData.prepare();
        }

        return textureData.consumePixmap();
    }
}
