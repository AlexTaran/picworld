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
	
	public Matrix4 setBillboardMatrix(float camx, float camy, float camz,
			float posx, float posy, float posz, float upx, float upy, float upz) {
		float lookx = camx - posx;
		float looky = camy - posy;
		float lookz = camz - posz;
		Vector3 right = new Vector3(lookx, looky, lookz).cross(upx, upy, upz).normalize();
		Vector3 up = new Vector3(lookx, looky, lookz).cross(right).normalize();
		Vector3 look = right.cross(up);
		data[0] = right.x;
		data[1] = right.y;
		data[2] = right.z;
		data[3] = 0.0f;
		
		data[4] = up.x;
		data[5] = up.y;
		data[6] = up.z;
		data[7] = 0.0f;
		
		data[8] = look.x;
		data[9] = look.y;
		data[10] = look.z;
		data[11] = 0.0f;
		
		data[12] = posx;
		data[13] = posy;
		data[14] = posz;
		data[15] = 1.0f;
		return this;
	}
	
	public Matrix4 setBillboardMatrix(Vector3 campos, Vector3 bilpos, Vector3 up) {
		return setBillboardMatrix(campos.x, campos.y, campos.z,
				bilpos.x, bilpos.y, bilpos.z, up.x, up.y, up.z);
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
	
	public Matrix4 translate(Vector3 v) {
		return translate(v.x, v.y, v.z);
	}
	
	public Matrix4 rotate(float degrees, float ax, float ay, float az) {
		Matrix.rotateM(data, 0, degrees, ax, ay, az);
		return this;
	}
	
	public Matrix4 rotate(float degrees, Vector3 v) {
		return rotate(degrees, v.x, v.y, v.z);
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
