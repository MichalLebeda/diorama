package cz.shroomware.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;


public abstract class ParticleEmitter {
    float x;
    float y;
    float z;
    float width;
    float height;
    float depth;

    float particlesPerSecond;
    float timeFromLastParticle = 0;

    int particleLimit = 0;

    float delta;

    boolean emission = true;

    //TODO GLOBAL PARTICLE LIMIT
    Array<Particle> particleArray = new Array();

    public ParticleEmitter(Vector3 position, Vector3 dimensions, float particlesPerSecond) {
        this(position.x, position.y, position.z, dimensions.x, dimensions.y, dimensions.z, particlesPerSecond);
    }

    private ParticleEmitter(float x,
                            float y,
                            float z,
                            float width,
                            float depth,
                            float height,
                            float particlesPerSecond
    ) {
        this.particlesPerSecond = particlesPerSecond;
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public void update(float delta) {
        // Performance tweak, wont have to iterate particles twice
        this.delta = delta;
//        Gdx.app.log("ParticleEmitter", "size: " + particleArray.size);
        timeFromLastParticle += delta;

        if (emission && timeFromLastParticle >= 1f / particlesPerSecond) {
            spawn();
        }
    }

    public void draw(MinimalisticDecalBatch decalBatch) {
        for (int i = 0; i < particleArray.size; i++) {
            Particle particle = particleArray.get(i);
            if (particle.toRemove()) {
                particleArray.removeIndex(i);
                i--;
            } else {
                particle.draw(decalBatch, delta);
            }
        }
    }

    protected void spawn() {
        Vector3 position = new Vector3(MathUtils.randomTriangular(x - width / 2, x + width / 2),
                MathUtils.randomTriangular(y - delta / 2, y + depth / 2),
                MathUtils.randomTriangular(z, z + height)
        );
        Particle particle = createParticle(position);
//        particle.setPosition(position);

        particleArray.add(particle);
        timeFromLastParticle = 0;

        while (particleLimitExceeded()) {
            particleArray.removeIndex(0);
        }
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setEmission(boolean emission) {
        this.emission = emission;
    }

    public boolean isEmitting() {
        return emission;
    }

    protected boolean particleLimitExceeded() {
        if (particleLimit == 0) {
            return false;
        }

        return particleArray.size > particleLimit;
    }

    public int getParticleLimit() {
        return particleLimit;
    }

    public void setParticleLimit(int particleLimit) {
        this.particleLimit = particleLimit;
    }

    protected abstract Particle createParticle(Vector3 position);
}
