package alex.taran.utils;

public interface ImmutableVector3 {
	public float getX();
	public float getY();
	public float getZ();
	
	public float dot(Vector3 other);
	public float dot(float x, float y, float z);
	public Vector3 cross(Vector3 other);
	public Vector3 cross(float x, float y, float z);
	
	public float len2();
	public float len();
	public float dist2(Vector3 other);
	public float dist(Vector3 other);
	
	public Vector3 clone();
}
