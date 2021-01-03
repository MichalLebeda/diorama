package cz.michallebeda.diorama.engine.level.object;

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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import cz.michallebeda.diorama.Utils;
import cz.michallebeda.diorama.engine.ColorUtil;
import cz.michallebeda.diorama.engine.Identifier;
import cz.michallebeda.diorama.engine.ObjectShadowPair;
import cz.michallebeda.diorama.engine.RegionAnimation;
import cz.michallebeda.diorama.engine.ai.AStar;
import cz.michallebeda.diorama.engine.ai.Node;
import cz.michallebeda.diorama.engine.level.Floor;
import cz.michallebeda.diorama.engine.level.Tile;
import cz.michallebeda.diorama.engine.level.prototype.EnemyPrototype;
import cz.michallebeda.diorama.engine.physics.BoxFactory;

import static cz.michallebeda.diorama.Utils.PIXELS_PER_METER;

public class Enemy extends GameObject {
    protected RegionAnimation animation;
    protected RegionAnimation shotAnimation;
    protected AStar aStar;
    protected float time;
    protected Array<Node> path = null;
    protected int untilNextAstar = MathUtils.random(0, 60);
    protected Vector2 velocity = null;
    protected int health = 100;
    protected State state = State.WANDER;
    //TODO: pass one instance of the plane as a parameter
    protected Plane plane = new Plane();
    protected boolean raycastedPlayer = false;

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

    Vector2 wanderVel = new Vector2(0, 2);

    @Override
    public void update(float delta) {
        super.update(delta);

        time += delta;
        if (state == State.HIT) {
            decal.setTextureRegion(shotAnimation.getKeyFrame(time).getObject());
            if (shotAnimation.isAnimationFinished(time)) {
                state = State.FOLLOW;
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

        state.update(this, player);

        switch (state) {
            case WANDER: {
                if (time > 2) {
                    time = 0;

                    wanderVel.rotate(MathUtils.random(-180, 180));
                }
                body.setLinearVelocity(wanderVel);
                break;
            }
            case HIT:
                break;
            case FOLLOW:
                if (aStar == null) {
                    aStar = new AStar(floor);
                }

                Vector2 enemyBodyPos = this.getBody().getPosition();
                Vector2 playerBodyPos = player.getBody().getPosition();

                Vector3 position = getPosition();
                Tile currentTile = floor.getTileAtWorld(position.x, position.y);

                Vector3 playerPosition = player.getPosition();
                Tile playerTile = floor.getTileAtWorld(playerPosition.x, playerPosition.y);

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
                    Vector2 vel = playerBodyPos.cpy().sub(enemyBodyPos).nor().scl(2);
                    body.setLinearVelocity(vel);
                }

                float distance = enemyBodyPos.dst(playerBodyPos);

                if (distance < 1) {
                    player.subtractHealth(0.1f);
                }
        }
    }

    @Override
    public void hit(Vector2 playerPosition) {
        Vector2 velocity = body.getPosition().cpy().sub(playerPosition);
        velocity.nor().scl(40);
        body.setLinearVelocity(velocity);
        health -= 40;
        time = 0;
        setState(State.HIT);
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

    public void setState(State state) {
        if (this.state != state) {
            this.state = state;
            state.onStart(this);
        }
    }

    private enum State {
        WANDER,
        FOLLOW,
        HIT,
        ATTACK;


        void onStart(Enemy enemy) {

        }

        void update(final Enemy enemy, Player player) {
            switch (this) {
                case WANDER:
                    Vector2 enemyBodyPos = enemy.getBody().getPosition();
                    Vector2 playerBodyPos = player.getBody().getPosition();
                    float distance = enemyBodyPos.dst(playerBodyPos);

                    final boolean hitPlayer = false;
                    if (distance < 15) {
                        World world = player.body.getWorld();

                        RayCastCallback rayCastCallback = new RayCastCallback() {

                            @Override
                            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                                enemy.raycastedPlayer = false;

                                Utils.raycastHit.set(point);

                                Object userData = fixture.getBody().getUserData();


                                //Ignore
                                if (userData instanceof Enemy) {
                                    return -1;
                                }

                                if (userData instanceof Tree) {
                                    return -1;
                                }

                                //Block
                                if (userData instanceof Wall) {
                                    return 0;
                                }

                                if (userData instanceof Player) {
                                    enemy.raycastedPlayer = true;
                                } else {
                                    enemy.raycastedPlayer = false;
                                }

                                return fraction;
                            }
                        };

                        if (enemy.raycastedPlayer) {
                            enemy.setState(FOLLOW);
                            Gdx.app.log("Enemy", "Saw player");
                        }

                        world.rayCast(rayCastCallback, enemyBodyPos, playerBodyPos);
                    }

                    break;
                case FOLLOW:
                    break;
                case ATTACK:
                    break;
            }
        }
    }
}
