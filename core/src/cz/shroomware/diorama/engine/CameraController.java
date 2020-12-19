package cz.shroomware.diorama.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import cz.shroomware.diorama.Utils;

public class CameraController implements InputProcessor {
    protected PerspectiveCamera camera;
    float verticalScroll = 0;

    public CameraController(PerspectiveCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mouseMoved(screenX, screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        camera.rotateAround(camera.position, Vector3.Z, -Gdx.input.getDeltaX() * Utils.AIM_SENSITIVITY);

        Vector3 axis = camera.direction.cpy().crs(camera.up);

        float verticalScrollDelta = -Gdx.input.getDeltaY() * Utils.AIM_SENSITIVITY;

        if (verticalScrollDelta > 0) {
            if (verticalScrollDelta + verticalScroll > 40) {
                verticalScrollDelta = 40 - verticalScroll;
            }
        } else {
            if (verticalScrollDelta + verticalScroll < -80) {
                verticalScrollDelta = -80 - verticalScroll;
            }
        }

        verticalScroll += verticalScrollDelta;
        camera.rotateAround(camera.position, axis, verticalScrollDelta);

        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
