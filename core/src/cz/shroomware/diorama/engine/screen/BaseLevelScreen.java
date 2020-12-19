package cz.shroomware.diorama.engine.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.level.Level;
import cz.shroomware.diorama.engine.level.Resources;

public abstract class BaseLevelScreen implements Screen, InputProcessor {
    protected Level level;
    protected Resources resources;
    protected Color backgroundColor = Color.GRAY.cpy();
    protected SpriteBatch spriteBatch;
    protected MinimalisticDecalBatch decalBatch;
    protected Box2DDebugRenderer dr = new Box2DDebugRenderer();

    protected ShaderProgram aoShaderProgram;
    protected FrameBuffer fbo;
    protected OrthographicCamera screenCamera;

    protected boolean boxDebug = false;

    protected BaseLevelScreen(Resources resources, Level level) {
        this.level = level;

        this.resources = resources;
        spriteBatch = new SpriteBatch();
        decalBatch = new MinimalisticDecalBatch();

        screenCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        String vertexShader = Gdx.files.internal("shaders/ao.vert").readString();
        String fragmentShader = Gdx.files.internal("shaders/ao.frag").readString();

        aoShaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        if (!aoShaderProgram.isCompiled())
            throw new IllegalArgumentException("couldn't compileShader defaultShader: " + aoShaderProgram.getLog());

    }

    protected void updateBackgroundColor(Resources resources, Level level) {
        // Use dominant floor color as background
        Color color = resources.getColorUtil().getDominantColor(level.getFloor().getTileAtIndex(0, 0));
        backgroundColor = color;
        backgroundColor = new Color(0x121212ff);
//        backgroundColor.mul(0.9f);
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public void show() {

    }

    protected abstract void drawWorld(float delta);

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
                | GL20.GL_DEPTH_BUFFER_BIT);

        if (Utils.SSAO) {
            fbo.begin();
            Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }

        spriteBatch.setShader(resources.getSpriteBatchShader());
        drawWorld(delta);

        if (Utils.SSAO) {
            fbo.end();
            // Test purposes, bind directly
            Texture diffuseText = fbo.getTextureAttachments().get(0);
            Texture depthText = fbo.getTextureAttachments().get(1);


            PerspectiveCamera camera = level.getCamera();
            aoShaderProgram.bind();
            depthText.bind(1);
            diffuseText.bind(0);
//        ssaoShaderProgram.setUniformi("u_texture", 0); // batch uses u_texture by default
            aoShaderProgram.setUniformi("depthText", 1);
            aoShaderProgram.setUniformf("camerarange", camera.near, camera.far);
            aoShaderProgram.setUniformf("screensize", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            spriteBatch.setProjectionMatrix(screenCamera.combined);
            spriteBatch.setShader(aoShaderProgram);
            spriteBatch.begin();
            TextureRegion region = new TextureRegion(diffuseText);
            region.flip(false, true);
            spriteBatch.draw(region, 0, 0);
            spriteBatch.end();
            spriteBatch.setShader(null);
        }

        if (boxDebug) {
            dr.render(level.getWorld(), level.getCamera().combined);
        }
    }

    public FrameBuffer createFbo(int width, int height) {
        FrameBuffer fbo;
        GLFrameBuffer.FrameBufferBuilder frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder(width, height);
        frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);
        frameBufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT, GL30.GL_UNSIGNED_SHORT);
        fbo = frameBufferBuilder.build();

        return fbo;
    }


    @Override
    public void resize(int width, int height) {
        screenCamera.viewportWidth = width;
        screenCamera.viewportHeight = height;
        screenCamera.position.set(((float) width) / 2, ((float) height / 2), 0);
        screenCamera.update();

        if (Utils.SSAO) {
            if (fbo != null) {
                fbo.dispose();
            }

            fbo = createFbo(width, height);
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.O) {
            boxDebug = !boxDebug;
            return true;
        }

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
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
