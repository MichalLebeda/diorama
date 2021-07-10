package cz.michallebeda.diorama.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import cz.michallebeda.diorama.Utils;
import cz.michallebeda.diorama.engine.CameraController;
import cz.michallebeda.diorama.engine.level.Level;
import cz.michallebeda.diorama.engine.level.fx.AnimParticle;
import cz.michallebeda.diorama.engine.level.fx.GravityParticle;
import cz.michallebeda.diorama.engine.level.fx.Particle;
import cz.michallebeda.diorama.engine.level.fx.ParticleEmitter;
import cz.michallebeda.diorama.engine.level.object.Enemy;
import cz.michallebeda.diorama.engine.level.object.GameObject;
import cz.michallebeda.diorama.engine.level.object.Player;
import cz.michallebeda.diorama.engine.level.prototype.AtlasRegionPrototype;
import cz.michallebeda.diorama.engine.screen.BaseLevelScreen;

public class TestLevelScreen extends BaseLevelScreen implements InputProcessor {
    protected static final float SPEED = 8.0f;
    protected EditorEngineGame game;
    protected Player player;
    protected CameraController cameraController;
    protected InputMultiplexer inputMultiplexer;
    protected Animation<TextureAtlas.AtlasRegion> gunAnimation;
    protected Sprite gunSprite;
    protected Sprite healthBar;
    protected Sprite healthBarBg;
    protected float gunAnimTime = 0;
    protected boolean gunAnimPlaying = false;
    protected ParticleEmitter particleEmitter;
    protected Array<TextureAtlas.AtlasRegion> enemyParticles;

    public TestLevelScreen(EditorEngineGame game, Level level, float x, float y) {
        super(game.getResources(), level);
        this.game = game;

        updateBackgroundColor(game.getResources(), level);

        AtlasRegionPrototype playerPrototype = new AtlasRegionPrototype(
                game.getResources(),
                game.getResources().getObjectAtlas().findRegion("zombie"));
        player = new Player(new Vector3(x, y, 0), playerPrototype, level.getBoxFactory(), level.getCamera());
        level.getGameObjects().add(player);

        cameraController = new CameraController(level.getCamera());

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(cameraController);

        decalBatch.setOffset(Utils.Z_OFFSET_PER_METER);

        Array<TextureAtlas.AtlasRegion> frames = resources.getObjectAtlas().findRegions("gun");
        gunAnimation = new Animation<TextureAtlas.AtlasRegion>(0.05f, frames);
        gunSprite = new Sprite(gunAnimation.getKeyFrame(0));
        gunSprite.setOrigin(gunSprite.getWidth() / 2, 0);
        gunSprite.setScale(10);
        gunSprite.setPosition(Gdx.graphics.getWidth() / 2f - gunSprite.getWidth() / 2, 0);

        healthBarBg = new Sprite(resources.getObjectAtlas().findRegion("healthbar_bg"));
        healthBarBg.setSize(Gdx.graphics.getWidth(), 20);
        healthBarBg.setPosition(0, Gdx.graphics.getHeight() - healthBarBg.getHeight());

        healthBar = new Sprite(resources.getObjectAtlas().findRegion("healthbar"));
        updateHealthBar();
        healthBar.setPosition(0, Gdx.graphics.getHeight() - healthBar.getHeight());

        final Animation<TextureRegion> smokeAnim = new Animation<TextureRegion>(0.1f, resources.getObjectAtlas().findRegions("smoke"));
        final TextureRegion particleRegion = resources.getObjectAtlas().findRegion("aim");
        particleEmitter = new ParticleEmitter() {
            @Override
            protected Particle createParticle(Vector3 position) {
                AnimParticle particle = new AnimParticle(position, smokeAnim);
                particle.setVelocityZ(MathUtils.random(0, 1f));
                particle.setVelocityX(MathUtils.random(-1, 1f));
                return particle;
            }
        };

        enemyParticles = resources.getObjectAtlas().findRegions("enemy_particle");
    }

    @Override
    public void show() {
        Gdx.graphics.setTitle("Test: " + level.getMetaLevel().getName());

        Gdx.input.setInputProcessor(inputMultiplexer);
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void hide() {
        super.hide();
        Gdx.input.setCursorCatched(false);
    }

    @Override
    public void drawWorld(float delta) {
        PerspectiveCamera camera = level.getCamera();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.setVelocity(0, SPEED);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.setVelocity(0, -SPEED);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setVelocity(-SPEED, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setVelocity(SPEED, 0);
        }

        level.updatePhysics(delta);

        player.update(0);
        level.updateBasedOnPlayer(delta, level.getFloor(), player);

        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        spriteBatch.getShader().setUniformf("u_camera_pos", camera.position);
        spriteBatch.getShader().setUniformf("u_background_color", Color.RED);
        level.draw(spriteBatch, decalBatch, delta);

        //TODO: remove
//        if (path != null) {
//            for (Node node : path) {
//                spriteBatch.draw(resources.getObjectAtlas().findRegion("cursor"), node.getX(), node.getY(), 0.1f, 0.1f);
//            }
//        }
        particleEmitter.update(delta);
        particleEmitter.draw(decalBatch);
        spriteBatch.end();

        decalBatch.render(camera, backgroundColor, 0);

        spriteBatch.setShader(null);
        spriteBatch.setProjectionMatrix(screenCamera.combined);
        spriteBatch.begin();
        spriteBatch.draw(resources.getObjectAtlas().findRegion("aim"), screenCamera.viewportWidth / 2, screenCamera.viewportHeight / 2);
        if (gunAnimPlaying) {
            gunAnimTime += delta;
            if (gunAnimTime > gunAnimation.getAnimationDuration()) {
                gunAnimTime = 0;
                gunAnimPlaying = false;
            }
            gunSprite.setRegion(gunAnimation.getKeyFrame(gunAnimTime));
        }
        gunSprite.draw(spriteBatch);

        updateHealthBar();
        healthBarBg.draw(spriteBatch);
        healthBar.draw(spriteBatch);

        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        decalBatch.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 intersection = new Vector3();
        Ray ray = level.getCamera().getPickRay(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        GameObject object = level.findIntersectingWithRay(resources.getColorUtil(), ray, level.getCamera(), intersection);


        float offset = 0.1f;

        if (object != null) {
            Gdx.app.log("hit", object.getPrototype().getName());
            object.hit(player.getBody().getPosition());

            if (object instanceof Enemy) {
                int particles = MathUtils.random(1, 4);
                for (int i = 0; i < particles; i++) {
                    Particle particle = new GravityParticle(intersection.cpy().add(
                            MathUtils.random(-0.1f, 0.1f),
                            MathUtils.random(-0.1f, 0.1f),
                            MathUtils.random(-0.1f, 0.1f)),
                            enemyParticles.random());
                    final float vel = 3;
                    particle.setVelocity(
                            MathUtils.random(-vel, vel),
                            MathUtils.random(-vel, vel),
                            10
                    );
                    particleEmitter.spawn(particle);
                    particleEmitter.spawn(intersection.cpy().add(
                            MathUtils.random(-offset, offset),
                            MathUtils.random(-offset, offset),
                            MathUtils.random(-offset, offset)
                    ));
                }
            } else {
                particleEmitter.spawn(intersection.cpy().add(
                        MathUtils.random(-offset, offset),
                        MathUtils.random(-offset, offset),
                        MathUtils.random(-offset, offset)
                ));
                particleEmitter.spawn(intersection.cpy().add(
                        MathUtils.random(-offset, offset),
                        MathUtils.random(-offset, offset),
                        MathUtils.random(-offset, offset)
                ));
                particleEmitter.spawn(intersection.cpy().add(
                        MathUtils.random(-offset, offset),
                        MathUtils.random(-offset, offset),
                        MathUtils.random(-offset, offset)
                ));
            }
        } else {
            PerspectiveCamera camera = level.getCamera();
            if (camera.direction.z < 0) {
                Gdx.app.log("direction", "");
                float multiplier = camera.position.z / -camera.direction.z;
                Vector3 spawnPos = camera.position.cpy().add(camera.direction.cpy().scl(multiplier));
                particleEmitter.spawn(spawnPos.cpy().add(
                        MathUtils.random(-offset, offset),
                        MathUtils.random(-offset, offset),
                        MathUtils.random(-offset, offset)
                ));
                particleEmitter.spawn(spawnPos.cpy().add(
                        MathUtils.random(-offset, offset),
                        MathUtils.random(-offset, offset),
                        MathUtils.random(-offset, offset)
                ));
                particleEmitter.spawn(spawnPos.cpy().add(
                        MathUtils.random(-offset, offset),
                        MathUtils.random(-offset, offset),
                        MathUtils.random(-offset, offset)
                ));
            }
        }

        gunAnimPlaying = true;
        gunAnimTime = 0;
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (super.keyDown(keycode)) {
            return true;
        }

        switch (keycode) {
            case Input.Keys.ESCAPE:
                dispose();
                game.returnToEditor();
                return true;
            case Input.Keys.L:
                return true;
        }

        return false;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gunSprite.setPosition(Gdx.graphics.getWidth() / 2f - gunSprite.getWidth() / 2, 0);

        updateHealthBar();
        healthBarBg.setPosition(0, Gdx.graphics.getHeight() - healthBar.getHeight());
        healthBar.setPosition(0, Gdx.graphics.getHeight() - healthBar.getHeight());
    }

    public void updateHealthBar() {
        healthBar.setSize(player.getHealth() / player.getMaxHealth() * Gdx.graphics.getWidth(), 20);
    }
}
