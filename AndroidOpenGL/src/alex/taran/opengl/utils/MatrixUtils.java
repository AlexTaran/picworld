package alex.taran.opengl.utils;

import android.opengl.Matrix;

public class MatrixUtils {
	// THIS CLASS IS OBSOLETE AND DECLARED DEPRECATED BY alextaran
	// IT MUST BE DELETED IN SOME NEXT COMMITS
	// ANYTHING MUST BE MOVED TO alex.taran.utils.Matrix4
	public static void perspectiveMatrix(float matrix[], float fovyInDegrees, float aspectRatio, float znear, float zfar) {
		 float ymax, xmax;
		 ymax = znear * (float)Math.tan(fovyInDegrees * Math.PI / 360.0f);
		 xmax = ymax * aspectRatio;
		 Matrix.frustumM(matrix, 0, -xmax, xmax, -ymax, ymax, znear, zfar);
	}
	
	public static float[] calcNormalMatrix(float matrix[]) {
		float[] tmp = new float[32];
		for (int i = 0; i < 15; ++i) {
			if (i % 4 != 3 && i < 12) {
				tmp[i] = matrix[i];
			} else {
				tmp[i] = 0.0f;
			}
		}
		tmp[15] = 1.0f;
		Matrix.invertM(tmp, 16, tmp, 0);
		Matrix.transposeM(tmp, 0, tmp, 0);
		float[] ret = new float[9];
		for (int i = 0; i < 9; ++i) {
			ret[i] = tmp[i + i / 3];
		}
		return ret;
	}
}
