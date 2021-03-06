/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.utils;

import java.util.Random;

public class Vector3 implements Cloneable, ImmutableVector3{
	private static Random random = new Random(System.currentTimeMillis());
	
	public float x;
	public float y;
	public float z;
	
	// ctors
	
	public Vector3() {
		x = y = z = 0.0f;
	}

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	// generators
	
	public static Vector3 createRandom() {
		return new Vector3(random.nextFloat() * 2.0f - 1.0f,
				random.nextFloat() * 2.0f - 1.0f,
				random.nextFloat() * 2.0f - 1.0f);
	}
	
	public static Vector3 createUnitRandom() {
		Vector3 v = createRandom();
		while(v.len2() < 0.0001f) {
			v.x = random.nextFloat() * 2.0f - 1.0f;
			v.y = random.nextFloat() * 2.0f - 1.0f;
			v.z = random.nextFloat() * 2.0f - 1.0f;
		}
		v.normalize();
		return v;
	}
	
	public static Vector3 createOrthoUnitRandom(float x, float y, float z) {
		Vector3 v = createUnitRandom();
		while(v.cross(x, y, z).len2() < 0.0001f) {
			v = createUnitRandom();
		}
		return v.sub(new Vector3(x, y, z).mul(v.dot(x, y, z)));
	}
	
	public static Vector3 createOrthoUnitRandom(Vector3 v) {
		return createOrthoUnitRandom(v.x, v.y, v.z);
	}
	// setters
	
	public Vector3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public Vector3 set(Vector3 other) {
		return set(other.x, other.y, other.z);
	}

	// operations
	
	public Vector3 add(Vector3 other) {
		return add(other.x, other.y, other.z);
	}
	
	public Vector3 add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public Vector3 sub(Vector3 other) {
		return sub(other.x, other.y, other.z);
	}
	
	public Vector3 sub(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}
	
	public Vector3 mul(float k) {
		this.x *= k;
		this.y *= k;
		this.z *= k;
		return this;
	}
	
	public Vector3 div(float k) {
		this.x /= k;
		this.y /= k;
		this.z /= k;
		return this;
	}
	
	public Vector3 addmul(Vector3 other, float k) {
		this.x += other.x * k;
		this.y += other.y * k;
		this.z += other.z * k;
		return this;
	}
	
	public float dot(Vector3 other) {
		return x * other.x + y * other.y + z * other.z;
	}
	
	public float dot(float x, float y, float z) {
		return x * this.x + y * this.y + z * this.z;
	}
	
	public Vector3 cross(Vector3 other) {
		return cross(other.x, other.y, other.z);
	}
	
	public Vector3 cross(float x, float y, float z) {
		return new Vector3(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}
	
	// lens and dists
	
	@Override
	public float len2() {
		return x*x + y*y + z*z;
	}
	
	@Override
	public float len() {
		return (float)Math.sqrt(len2());
	}
	
	@Override
	public float dist2(Vector3 other) {
		return (new Vector3(x-other.x, y-other.y, z-other.z)).len2();
	}
	
	@Override
	public float dist(Vector3 other) {
		return (float)Math.sqrt(dist2(other));
	}
	
	public Vector3 normalize() {
		float l = len();
		x /= l;
		y /= l;
		z /= l;
		return this;
	}
	
	// interpolations
	
	public Vector3 lerp(Vector3 other, float t) {
		return lerp(other.x, other.y, other.z, t);
	}
	
	public Vector3 lerp(float x, float y, float z, float t) {
		return new Vector3(
				this.x + (x - this.x) * t,
				this.y + (y - this.y) * t,
				this.z + (z - this.z) * t);
	}
	
	public static Vector3 bezier(Vector3 p0, Vector3 p1, Vector3 p2, Vector3 p3, float t) {
		return p0.lerp(p1, t).lerp(p2.lerp(p3, t), t);
	}
	
	// cloneable
	
	@Override
	public Vector3 clone() {
		return new Vector3(x, y, z);
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getZ() {
		return z;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	// interaction with matrix
	public Vector3 transformBy(Matrix4 m) {
		float nx = m.data[0] * x + m.data[4] * y + m.data[8] * z + m.data[12];
		float ny = m.data[1] * x + m.data[5] * y + m.data[9] * z + m.data[13];
		float nz = m.data[2] * x + m.data[6] * y + m.data[10] * z + m.data[14];
		float nw = m.data[3] * x + m.data[7] * y + m.data[11] * z + m.data[15];
		x = nx / nw;
		y = ny / nw;
		z = nz / nw;
		return this;
	}
}
