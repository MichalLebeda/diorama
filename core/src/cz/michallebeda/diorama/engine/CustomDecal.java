package cz.michallebeda.diorama.engine;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalMaterial;

/**
 * Hack to ensure decal is updated when getting vertices
 */
public class CustomDecal extends Decal {

    protected boolean billboard = false;

    public CustomDecal() {
        super();
    }

    public CustomDecal(DecalMaterial material) {
        super(material);
    }

    /**
     * Creates a decal assuming the dimensions of the texture region
     *
     * @param textureRegion Texture region to use
     * @return Created decal
     */
    public static CustomDecal newDecal(TextureRegion textureRegion) {
        return newDecal(textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), textureRegion, DecalMaterial.NO_BLEND,
                DecalMaterial.NO_BLEND);
    }

    /**
     * Creates a decal assuming the dimensions of the texture region and adding transparency
     *
     * @param textureRegion   Texture region to use
     * @param hasTransparency Whether or not this sprite will be treated as having transparency (transparent png, etc.)
     * @return Created decal
     */
    public static CustomDecal newDecal(TextureRegion textureRegion, boolean hasTransparency) {
        return newDecal(textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), textureRegion,
                hasTransparency ? GL20.GL_SRC_ALPHA : DecalMaterial.NO_BLEND, hasTransparency ? GL20.GL_ONE_MINUS_SRC_ALPHA
                        : DecalMaterial.NO_BLEND);
    }

    /**
     * Creates a decal using the region for texturing
     *
     * @param width         Width of the decal in world units
     * @param height        Height of the decal in world units
     * @param textureRegion TextureRegion to use
     * @return Created decal
     */
    // TODO : it would be convenient if {@link com.badlogic.gdx.graphics.Texture} had a getFormat() method to assume transparency
// from RGBA,..
    public static CustomDecal newDecal(float width, float height, TextureRegion textureRegion) {
        return newDecal(width, height, textureRegion, DecalMaterial.NO_BLEND, DecalMaterial.NO_BLEND);
    }

    /**
     * Creates a decal using the region for texturing
     *
     * @param width           Width of the decal in world units
     * @param height          Height of the decal in world units
     * @param textureRegion   TextureRegion to use
     * @param hasTransparency Whether or not this sprite will be treated as having transparency (transparent png, etc.)
     * @return Created decal
     */
    public static CustomDecal newDecal(float width, float height, TextureRegion textureRegion, boolean hasTransparency) {
        return newDecal(width, height, textureRegion, hasTransparency ? GL20.GL_SRC_ALPHA : DecalMaterial.NO_BLEND,
                hasTransparency ? GL20.GL_ONE_MINUS_SRC_ALPHA : DecalMaterial.NO_BLEND);
    }

    /**
     * Creates a decal using the region for texturing and the specified blending parameters for blending
     *
     * @param width          Width of the decal in world units
     * @param height         Height of the decal in world units
     * @param textureRegion  TextureRegion to use
     * @param srcBlendFactor Source blend used by glBlendFunc
     * @param dstBlendFactor Destination blend used by glBlendFunc
     * @return Created decal
     */
    public static CustomDecal newDecal(float width, float height, TextureRegion textureRegion, int srcBlendFactor, int dstBlendFactor) {
        CustomDecal decal = new CustomDecal();
        decal.setTextureRegion(textureRegion);
        decal.setBlending(srcBlendFactor, dstBlendFactor);
        decal.dimensions.x = width;
        decal.dimensions.y = height;
        decal.setColor(1, 1, 1, 1);
        return decal;
    }

    /**
     * Creates a decal using the region for texturing and the specified blending parameters for blending
     *
     * @param width          Width of the decal in world units
     * @param height         Height of the decal in world units
     * @param textureRegion  TextureRegion to use
     * @param srcBlendFactor Source blend used by glBlendFunc
     * @param dstBlendFactor Destination blend used by glBlendFunc
     * @param material       Custom decal material
     * @return Created decal
     */
    public static CustomDecal newDecal(float width, float height, TextureRegion textureRegion, int srcBlendFactor, int dstBlendFactor,
                                       DecalMaterial material) {
        CustomDecal decal = new CustomDecal(material);
        decal.setTextureRegion(textureRegion);
        decal.setBlending(srcBlendFactor, dstBlendFactor);
        decal.dimensions.x = width;
        decal.dimensions.y = height;
        decal.setColor(1, 1, 1, 1);
        return decal;
    }

    @Override
    public float[] getVertices() {
        super.update();
        return super.getVertices();
    }

    private void offsetVerticleY(int verticle, float offset) {
        vertices[verticle] += vertices[verticle + 1] * offset;
    }

    protected void transformVertices(float offset) {
        super.transformVertices();

        offsetVerticleY(Y1, offset);
        offsetVerticleY(Y2, offset);
        offsetVerticleY(Y3, offset);
        offsetVerticleY(Y4, offset);
    }

    public void update(float offset) {
        if (!updated) {
            resetVertices();
            transformVertices(offset);
        }
    }

    public void update() {
        super.update();
    }

    public boolean isBillboard() {
        return billboard;
    }

    public void setBillboard(boolean billboard) {
        this.billboard = billboard;
    }
}
