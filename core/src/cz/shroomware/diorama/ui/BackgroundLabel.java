package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Null;

import cz.shroomware.diorama.DioramaGame;

import static cz.shroomware.diorama.Utils.DARK_BACKGROUND_NAME;

public class BackgroundLabel extends DFLabel {
    private static final float PAD = 20;

    protected Drawable background;

    public BackgroundLabel(CharSequence text, DioramaGame game) {
        super(text, game);
        background = game.getSkin().getDrawable(DARK_BACKGROUND_NAME);
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
    public @Null
    Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() != Touchable.enabled) return null;
        if (!isVisible()) return null;
        return x >= -PAD && x < getWidth() + PAD && y >= -PAD && y < getHeight() + PAD ? this : null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a);
        background.draw(batch, getXWithPadding(), getYWithPadding(), getWidthWithPadding(), getHeightWithPadding());
        super.draw(batch, parentAlpha);
    }
}
