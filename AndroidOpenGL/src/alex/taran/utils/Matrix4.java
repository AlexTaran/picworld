package alex.taran.utils;

import android.opengl.Matrix;

public class Matrix4 {
	public final float data[] = new float[16];
	
	public Matrix4 setIdentity() {
		Matrix.setIdentityM(data, 0);
		return this;
	}
	
	public Matrix4 setLookAt(float posx, float posy, float posz,
			float eyex, float eyey, float eyez,
			float upx, float upy, float upz) {
		Matrix.setLookAtM(data, 0, posx, posy, posz, eyex, eyey, eyez, upx, upy, upz);
		return this;
	}
	
	public Matrix4 setPerspectiveMatrix(float fovyInDegrees, float aspectRatio, float znear, float zfar) {
		 float ymax, xmax;
		 ymax = znear * (float)Math.tan(fovyInDegrees * Math.PI / 360.0f);
		 xmax = ymax * aspectRatio;
		 Matrix.frustumM(data, 0, -xmax, xmax, -ymax, ymax, znear, zfar);
		 return this;
	}
	
	public Matrix4 setOrthoMatrix(float left, float right, float top, float bottom, float near, float far) {
		Matrix.orthoM(data, 0, left, right, top, bottom, near, far);
		return this;
	}
	
	public Matrix4 setProduction(Matrix4 m1, Matrix4 m2) {
		Matrix.multiplyMM(data, 0, m1.data, 0, m2.data, 0);
		return this;
	}
	
	public Matrix4 translate(float dx, float dy, float dz) {
		Matrix.translateM(data, 0, dx, dy, dz);
		return this;
	}
	
	public Matrix4 rotate(float degrees, float ax, float ay, float az) {
		Matrix.rotateM(data, 0, degrees, ax, ay, az);
		return this;
	}
	
	public Matrix4 rotateX(float degrees) {
		return rotate(degrees, 1.0f, 0.0f, 0.0f);
	}
	
	public Matrix4 rotateY(float degrees) {
		return rotate(degrees, 0.0f, 1.0f, 0.0f);
	}
	
	public Matrix4 rotateZ(float degrees) {
		return rotate(degrees, 0.0f, 0.0f, 1.0f);
	}
	
	public Matrix4 scale(float kx, float ky, float kz) {
		Matrix.scaleM(data, 0, kx, ky, kz);
		return this;
	}
	
	public Matrix4 scaleX(float kx) {
		return scale(kx, 1.0f, 1.0f);
	}
	
	public Matrix4 scaleY(float ky) {
		return scale(1.0f, ky, 1.0f);
	}
	
	public Matrix4 scaleZ(float kz) {
		return scale(1.0f, 1.0f, kz);
	}
	
	public Matrix4 invX() {
		return scaleX(-1.0f);
	}
	
	public Matrix4 invY() {
		return scaleY(-1.0f);
	}
	
	public Matrix4 invZ() {
		return scaleZ(-1.0f);
	}
}
