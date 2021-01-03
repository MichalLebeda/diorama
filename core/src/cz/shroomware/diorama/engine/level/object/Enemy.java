package cz.shroomware.diorama.engine.level.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import cz.shroomware.diorama.Utils;
import cz.shroomware.diorama.engine.ColorUtil;
import cz.shroomware.diorama.engine.Identifier;
import cz.shroomware.diorama.engine.ObjectShadowPair;
import cz.shroomware.diorama.engine.RegionAnimation;
import cz.shroomware.diorama.engine.ai.AStar;
import cz.shroomware.diorama.engine.ai.Node;
import cz.shroomware.diorama.engine.level.Floor;
import cz.shroomware.diorama.engine.level.Tile;
import cz.shroomware.diorama.engine.level.prototype.EnemyPrototype;
import cz.shroomware.diorama.engine.physics.BoxFactory;

import static cz.shroomware.diorama.Utils.PIXELS_PER_METER;

public class Enemy extends GameObject {
    protected RegionAnimation animation;
    protected RegionAnimation shotAnimation;
    protected AStar aStar;
    protected float time;
    protected Array<Node> path = null;
    protected int untilNextAstar = MathUtils.random(0, 60);
    protected Vector2 velocity = null;
    protected int health = 100;
    protected boolean hit = false;
    //TODO: pass one instance of the plane as a parameter
    protected Plane plane = new Plane();

    public Enemy(Vector3 position, EnemyPrototype prototype, Identifier identifier, BoxFactory boxFactory) {
        super(position, prototype.getAnimation().first().getObject(), prototype, identifier);
        animation = prototype.getAnimation();
        shotAnimation = prototype.getShotAnimation();
        createShadowSprite();
        setRandomAnimOffset();

        Body body = boxFactory.addDynCircle(position.x, position.y, 0.2f);

        attachToBody(body);
        decal.setBillboard(true);
    }

    private void createShadowSprite() {
        ObjectShadowPair pair = animation.first();
        if (pair.getShadow() != null) {
            shadowSprite = new Sprite(pair.getShadow());
            shadowSprite.setSize(decal.getWidth() * Utils.SHADOW_SCALE, -((float) shadowSprite.getRegionHeight() / (float) shadowSprite.getRegionWidth() * decal.getWidth() * Utils.SHADOW_SCALE));
            shadowSprite.setPosition(decal.getX() - shadowSprite.getWidth() / 2, decal.getY() - shadowSprite.getHeight() - 0.01f / PIXELS_PER_METER);
        }
    }

    public void setRandomAnimOffset() {
        time = MathUtils.random(0f, animation.getAnimationDuration());
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        time += delta;
        if (hit) {
            decal.setTextureRegion(shotAnimation.getKeyFrame(time).getObject());
            if (shotAnimation.isAnimationFinished(time)) {
                hit = false;
                time = 0;
            }
        } else {

            decal.setTextureRegion(animation.getKeyFrame(time).getObject());
            TextureRegion shadowRegion = animation.getKeyFrame(time).getShadow();
            if (shadowRegion != null) {
                shadowSprite.setRegion(shadowRegion);
            }
        }
    }

    @Override
    public void updateBasedOnPlayer(float delta, Floor floor, Player player) {
        super.updateBasedOnPlayer(delta, floor, player);

        if (aStar == null) {
            aStar = new AStar(floor);
        }

        Vector3 position = getPosition();
        Tile currentTile = floor.getTileAtWorld(position.x, position.y);

        Vector3 playerPosition = player.getPosition();
        Tile playerTile = floor.getTileAtWorld(playerPosition.x, playerPosition.y);

        if (hit) {

        } else {
            untilNextAstar--;
            if (untilNextAstar < 0 && currentTile != null && playerTile != null) {
                path = aStar.findPath(currentTile.getXIndex(), currentTile.getYIndex(),
                        playerTile.getXIndex(), playerTile.getYIndex());

                untilNextAstar = MathUtils.random(10, 60);
            }

            Utils.path = path;

            if (path != null && !path.isEmpty()) {
                Node node = path.first();
                Vector2 targetTilePosition = new Vector2(node.getX(), node.getY());

                Vector2 targetVelocity = targetTilePosition.cpy().sub(body.getPosition());
                targetVelocity.nor().scl(4);

                if (velocity == null) {
                    velocity = targetVelocity.cpy();
                } else {
                    velocity.scl(0.96f).add(targetVelocity.scl(0.04f));
                }

                body.setLinearVelocity(velocity);

                if (getBody().getPosition().dst(targetTilePosition) < 0.86) {
                    path.removeIndex(0);
                }
            } else {
                body.setLinearVelocity(0, 0);
            }
        }
    }

    @Override
    public void hit(Vector2 playerPosition) {
        Vector2 velocity = body.getPosition().cpy().sub(playerPosition);
        velocity.nor().scl(40);
        body.setLinearVelocity(velocity);
        health -= 40;
        hit = true;
        time = 0;
        Gdx.app.log("health", "" + health);
    }

    @Override
    public boolean shouldBeRemoved() {
        return health <= 0;
    }

    @Override
    public void drawShadow(Batch spriteBatch) {
        super.drawShadow(spriteBatch);
    }

    @Override
    public boolean intersectsWithOpaque(ColorUtil colorUtil, Ray ray, Vector3 boundsIntersection) {
        findIntersectionRayDecalPlane(ray, decal, boundsIntersection);
        return super.intersectsWithOpaque(colorUtil, ray, boundsIntersection);
    }
}
