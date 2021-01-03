package cz.shroomware.diorama.engine.level.fx;

import com.badlogic.gdx.graphics.g3d.decals.MinimalisticDecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public abstract class ParticleEmitter {
    float delta;
    int particleLimit = 0;

    //TODO GLOBAL PARTICLE LIMIT
    Array<Particle> particleArray = new Array();

    public ParticleEmitter() {
    }

    public void update(float delta) {
        // Performance tweak, wont have to iterate particles twice
        this.delta = delta;
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

    public void spawn(Vector3 position) {
        Particle particle = createParticle(position);

        particleArray.add(particle);

        while (particleLimitExceeded()) {
            particleArray.removeIndex(0);
        }
    }

    public void spawn(Particle particle) {
        particleArray.add(particle);

        while (particleLimitExceeded()) {
            particleArray.removeIndex(0);
        }
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
