package alex.taran.opengl.particles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import alex.taran.utils.Matrix4;
import alex.taran.utils.Vector3;

public class FountainParticleSystem {
	private static final Random random = new Random(System.currentTimeMillis());
	
	public final List<FountainParticle> particles = new ArrayList<FountainParticle>();
	public String textureName;
	public boolean resurrection;
	public float particleSize;
	public float minFade;
	public float maxFade;
	public float emitAngle;
	public Vector3 position;
	
	private Vector3 direction;
	
	
	public void update(float deltaTime) {
		for (int i = 0; i < particles.size(); ++i) {
		}
	}
	
	private void resurrectParticle(FountainParticle particle) {
		particle.exists = true;
		particle.fade = minFade + (maxFade - minFade) * random.nextFloat();
		particle.life = 1.0f;
		particle.size = particleSize;
		particle.position.set(position);
		// TODO: fix it
		Vector3 v = Vector3.createOrthoUnitRandom(direction).mul(/**/emitAngle).add(direction).normalize();
		particle.velocity = v;
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
	
	public Vector3 getDirection() {
		return direction.clone();
	}
	
	public FountainParticleSystem setDirection(Vector3 v) {
		direction.set(v);
		return this;
	}
	
	public void sortParticles(Vector3 lookDirection) {
		comparator.setLookDirection(lookDirection);
		Collections.sort(particles, comparator);
	}
	
	public class FountainParticleComparator implements Comparator<FountainParticle> {
		public Vector3 lookDirection;
		public void setLookDirection(Vector3 lookDirection) {
			this.lookDirection = lookDirection;
		}
		@Override
		public int compare(FountainParticle lhs, FountainParticle rhs) {
			float dl = lhs.position.dot(lookDirection);
			float dr = rhs.position.dot(lookDirection);
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
