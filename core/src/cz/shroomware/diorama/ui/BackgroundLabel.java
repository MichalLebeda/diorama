package cz.shroomware.diorama.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Null;

import static cz.shroomware.diorama.Utils.DARK_BACKGROUND_DRAWABLE;

public class BackgroundLabel extends DFLabel {
    private float padHorizontal = 20;
    private float padVertical = 10;

    protected Drawable background;

    public BackgroundLabel(Skin skin, ShaderProgram dfShader, CharSequence text, Drawable background) {
        super(skin, dfShader, text);
        this.background = background;
    }

    public BackgroundLabel(Skin skin, ShaderProgram dfShader, CharSequence text) {
        super(skin, dfShader, text);
        background = skin.getDrawable(DARK_BACKGROUND_DRAWABLE);
    }

    public void setPad(float pad) {
        this.padHorizontal = pad;
        this.padVertical = pad;
        layout();
        pack();
    }

    public void setPad(float padHorizontal, float padVertical) {
        this.padHorizontal = padHorizontal;
        this.padVertical = padVertical;
        layout();
        pack();
    }

    public float getXWithPadding() {
        return super.getX() - getPadHorizontal();
    }

    public float getYWithPadding() {
        return super.getY() - getPadVertical();
    }

    public float getWidthWithPadding() {
        return super.getWidth() + 2 * getPadHorizontal();
    }

    public float getHeightWithPadding() {
        return super.getHeight() + 2 * getPadVertical();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x + getPadHorizontal(), y + getPadVertical());
    }

    @Override
    public void setX(float x) {
        super.setX(x + getPadHorizontal());
    }

    @Override
    public void setY(float y) {
        super.setY(y + getPadVertical());
    }

    public float getPadHorizontal() {
        return padHorizontal;
    }

    public void setPadHorizontal(float padHorizontal) {
        this.padHorizontal = padHorizontal;
        layout();
        pack();
    }

    public float getPadVertical() {
        return padVertical;
    }

    public void setPadVertical(float padVertical) {
        this.padVertical = padVertical;
        layout();
        pack();
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
        return x >= -padHorizontal && x < getWidth() + padHorizontal && y >= -padHorizontal && y < getHeight() + padHorizontal ? this : null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a);
        background.draw(batch, getXWithPadding(), getYWithPadding(), getWidthWithPadding(), getHeightWithPadding());
        super.draw(batch, parentAlpha);
    }
}
