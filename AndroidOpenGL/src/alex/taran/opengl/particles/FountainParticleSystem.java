package alex.taran.opengl.particles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import alex.taran.utils.Matrix4;
import alex.taran.utils.Vector3;
import android.util.Log;

public class FountainParticleSystem {
	private static final Random random = new Random(System.currentTimeMillis());
	
	public final List<FountainParticle> particles = new ArrayList<FountainParticle>();
	public String textureName;
	public boolean resurrection;
	public float particleSize;
	public float minFade;
	public float maxFade;
	public float minVelocity;
	public float maxVelocity;
	public float emitAngle;
	public final Vector3 position = new Vector3();
	public final Vector3 gravity = new Vector3();
	
	private final Vector3 direction = new Vector3();
	
	public void update(float deltaTime) {
		for (FountainParticle p :particles) {
			if (p.exists) {
				p.life -= p.fade * deltaTime;
				if (p.life < 0.0f) {
					if (resurrection) {
						resurrectParticle(p);
					} else {
						p.exists = false;
					}
				} else {
					p.position.addmul(p.velocity, deltaTime);
					p.velocity.addmul(gravity, deltaTime);
				}
			} else if (resurrection) {
				resurrectParticle(p);
			}
		}
	}
	
	private void resurrectParticle(FountainParticle particle) {
		particle.exists = true;
		particle.fade = minFade + (maxFade - minFade) * random.nextFloat();
		particle.life = 1.0f;
		particle.size = particleSize;
		particle.position.set(position);
		Vector3 v = Vector3.createOrthoUnitRandom(direction).mul((float)Math.tan(emitAngle)).add(direction).normalize();
		v.mul(minVelocity + (maxVelocity - minVelocity) * random.nextFloat());
		particle.velocity.set(v);
		//Log.d("FUCK", "Resurrected particle: " + particle);
	}
	
	public FountainParticleSystem initialize(int numParticles) {
		particles.clear();
		for (int i = 0; i < numParticles; ++i) {
			FountainParticle p = new FountainParticle();
			resurrectParticle(p);
			particles.add(p);
		}
		return this;
	}
	
	public FountainParticleSystem setVelocities(float minVelocity, float maxVelocity) {
		this.minVelocity = minVelocity;
		this.maxVelocity = maxVelocity;
		return this;
	}
	
	public FountainParticleSystem setFades(float minFade, float maxFade) {
		this.minFade = minFade;
		this.maxFade = maxFade;
		return this;
	}
	
	public FountainParticleSystem setParticleSize(float particleSize) {
		this.particleSize = particleSize;
		return this;
	}
	
	public FountainParticleSystem setEmitAngle(float emitAngle) {
		this.emitAngle = emitAngle;
		return this;
	}
	
	public FountainParticleSystem setResurrection(boolean resurrection) {
		this.resurrection = resurrection;
		return this;
	}
	
	public FountainParticleSystem setPosition(float x, float y, float z) {
		position.set(x, y, z);
		return this;
	}
	
	public FountainParticleSystem setPosition(Vector3 v) {
		position.set(v);
		return this;
	}
	
	public FountainParticleSystem setDirection(float x, float y, float z) {
		direction.set(x, y, z).normalize();
		return this;
	}
	
	public FountainParticleSystem setGravity(Vector3 v) {
		gravity.set(v);
		return this;
	}
	
	public FountainParticleSystem setGravity(float x, float y, float z) {
		gravity.set(x, y, z);
		return this;
	}
	
	public Vector3 getDirection() {
		return direction.clone();
	}
	
	public FountainParticleSystem setDirection(Vector3 v) {
		direction.set(v);
		return this;
	}
	
	public void sortParticles(float cameraDirectionX, float cameraDirectionY, float cameraDirectionZ) {
		comparator.cameraDirection.set(cameraDirectionX, cameraDirectionY, cameraDirectionZ);
		Collections.sort(particles, comparator);
	}
	
	public void sortParticles(Vector3 cameraPosition) {
		sortParticles(cameraPosition.x, cameraPosition.y, cameraPosition.z);
	}
	
	public class FountainParticleComparator implements Comparator<FountainParticle> {
		public final Vector3 cameraDirection = new Vector3();
		
		@Override
		public int compare(FountainParticle lhs, FountainParticle rhs) {
			float dl = lhs.position.dot(cameraDirection);
			float dr = rhs.position.dot(cameraDirection);
			if (dl < dr) {
				return 1;
			} else if (dl > dr) {
				return -1;
			} else {
				return 0;
			}
		}
		
	};
	private FountainParticleComparator comparator = new FountainParticleComparator();
}
