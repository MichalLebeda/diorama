package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import cz.shroomware.diorama.DioramaGame;

public class BackgroundLabel extends DFLabel {
    private static final float PAD = 10;
    protected TextureRegion textureBackgroundRegion;

    public BackgroundLabel(CharSequence text, DioramaGame game) {
        super(text, game);
        this.textureBackgroundRegion = game.getDarkBackground();
    }

    public float getXWithPadding() {
        return super.getX() - getPad();
    }

    public float getYWithPadding() {
        return super.getY() - getPad();
    }

    public float getWidthWithPadding() {
        return super.getWidth() + 2 * getPad();
    }

    public float getHeightWithPadding() {
        return super.getHeight() + 2 * getPad();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x + getPad(), y + getPad());
    }

    @Override
    public void setX(float x) {
        super.setX(x + getPad());
    }

    @Override
    public void setY(float y) {
        super.setY(y + getPad());
    }

    public float getPad() {
        return PAD;
    }

    @Override
    protected void drawDebugBounds(ShapeRenderer shapes) {
        if (!getDebug()) return;
        shapes.set(ShapeRenderer.ShapeType.Line);
        if (getStage() != null) shapes.setColor(getStage().getDebugColor());
        shapes.rect(getXWithPadding(), getYWithPadding(), getOriginX(), getOriginY(), getWidthWithPadding(), getHeightWithPadding(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() != Touchable.enabled) return null;
        if (!isVisible()) return null;
        return x >= -getPad() && x < getWidth() + 2 * getPad() && y >= -getPad() && y < getHeight() + 2 * getPad() ? this : null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        batch.draw(
                textureBackgroundRegion,
                getX() - PAD,
                getY() - PAD,
                getWidth() + 2 * PAD,
                getHeight() + 2 * PAD);
        super.draw(batch, parentAlpha);
    }
}
