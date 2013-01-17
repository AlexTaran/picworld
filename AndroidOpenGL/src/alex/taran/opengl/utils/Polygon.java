package alex.taran.opengl.utils;

import java.nio.FloatBuffer;

import android.graphics.PointF;

public class Polygon {
	public PointF[] v = new PointF[3];

	public Polygon() {
		for (int i = 0; i < 3; ++i) {
			v[i] = new PointF();
		}
	}

	public Polygon(float x0, float y0, float x1, float y1, float x2, float y2) {
		for (int i = 0; i < 3; ++i) {
			v[i] = new PointF();
		}
		v[0].x = x0;
		v[0].y = y0;
		v[1].x = x1;
		v[1].y = y1;
		v[2].x = x2;
		v[2].y = y2;
	}

	public void putToBuffer(FloatBuffer buf, int ofs) {
		buf.put(ofs + 0, v[0].x);
		buf.put(ofs + 1, v[0].y);
		buf.put(ofs + 2, v[1].x);
		buf.put(ofs + 3, v[1].y);
		buf.put(ofs + 4, v[2].x);
		buf.put(ofs + 5, v[2].y);
	}

	public void putToBuffer(FloatBuffer buf) {
		putToBuffer(buf, 0);
	}

	public boolean isPointInside(PointF pt) {
		int sgns[] = new int[3];
		for (int i = 0; i < 3; ++i) {
			PointF v1 = new PointF(v[(i + 1) % 3].x - v[i].x, v[(i + 1) % 3].y
					- v[i].y);
			PointF v2 = new PointF(pt.x - v[i].x, pt.y - v[i].y);
			float cp = MathUtils.crossProduct(v1, v2);
			if (cp < 0.0f) {
				sgns[i] = -1;
			} else {
				sgns[i] = 1;
			}
		}
		if(sgns[0]==sgns[1] && sgns[1]==sgns[2]){
			return true;
		}
		return false;
	}
}
