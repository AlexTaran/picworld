package alex.taran.opengl.particles;

import alex.taran.utils.Vector3;

public class FountainParticle {
	public final Vector3 position = new Vector3();
	public final Vector3 velocity = new Vector3();
	public float life;
	public float fade;
	public float size;
	public boolean exists;
	
	@Override
	public String toString() {
		return "Position: " + position +
			   " Velocity: " + velocity + 
			   " Life: " + life +
			   " Fade: " + fade + 
			   " Size: " + size +
			   " Exists: " + exists;
	}
}
