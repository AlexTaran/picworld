package alex.taran.picworld;

import alex.taran.utils.Matrix4;
import alex.taran.utils.Vector3;

public class PicworldCamera {
	private final Vector3 center = new Vector3();
	private float radius;
	private float anglePhi;
	private float angleTheta;
	
	public PicworldCamera() {
		radius = 1.0f;
		anglePhi = 0.0f;
		angleTheta = 0.0f;
	}
	
	// general ops
	
	public Vector3 getPosition() {
		float camposx = (float)(radius * Math.cos(angleTheta) * Math.cos(anglePhi));
		float camposy = (float)(radius * Math.sin(angleTheta));
		float camposz = (float)(radius * Math.cos(angleTheta) * Math.sin(anglePhi));
		return new Vector3(camposx, camposy, camposz).add(center);
	}
	
	public Vector3 getDirection() {
		float camdirx = (float)(Math.cos(angleTheta) * Math.cos(anglePhi));
		float camdiry = (float)(Math.sin(angleTheta));
		float camdirz = (float)(Math.cos(angleTheta) * Math.sin(anglePhi));
		return new Vector3(-camdirx, -camdiry, -camdirz);
	}
	
	public Vector3 getUp() {
		float camposx = (float)(Math.cos(angleTheta) * Math.cos(anglePhi));
		float camposy = (float)(Math.sin(angleTheta));
		float camposz = (float)(Math.cos(angleTheta) * Math.sin(anglePhi));
		
		float camrightx = -(float) Math.sin(anglePhi);
		float camrightz = (float) Math.cos(anglePhi);
		
		float camupx = - camposy * camrightz;
		float camupy = - camposz * camrightx + camposx * camrightz;
		float camupz = + camposy * camrightx;
		return new Vector3(camupx, camupy, camupz);
	}
	
	public Matrix4 getViewMatrix() {
		Vector3 pos = getPosition();
		Vector3 up = getUp();
		return new Matrix4().setLookAt(pos.x, pos.y, pos.z, center.x,
				center.y, center.z, up.x, up.y, up.z);
	}
	
	// for skybox
	public Matrix4 getUnbiasedViewMatrix () {
		Vector3 dir = getDirection();
		Vector3 up = getUp();
		return new Matrix4().setLookAt(0.0f, 0.0f, 0.0f, dir.x,
				dir.y, dir.z, up.x, up.y, up.z);
	}
	
	// Getters
	
	public float getAnglePhi() {
		return anglePhi;
	}
	
	public float getAngleTheta() {
		return angleTheta;
	}
	
	public float getRadius() {
		return radius;
	}
	
	// we don't care about [im]mutability. 
	public Vector3 getCenter() {
		return center;
	}
	
	// Setters
	
	public PicworldCamera setAnglePhi(float anglePhi) {
		this.anglePhi = anglePhi;
		return this;
	}
	
	public PicworldCamera setAngleTheta(float angleTheta) {
		this.angleTheta = angleTheta;
		return this;
	}
	
	public PicworldCamera setRadius(float radius) {
		this.radius = radius;
		return this;
	}
	
	public PicworldCamera setCenter(Vector3 center) {
		this.center.set(center);
		return this;
	}
	
	public PicworldCamera setCenter(float x, float y, float z) {
		this.center.set(x, y, z);
		return this;
	}
}
