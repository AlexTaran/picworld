package alex.taran.opengl.utils;

import java.util.List;

import android.graphics.PointF;

public class MathUtils {
	public final static float EPS = 1e-5f;
	public static float sqr(float x) {
		return x * x;
	}

	public static boolean isPointInRect(float x, float y, float left,
			float top, float right, float bottom) {
		return x <= right && x >= left && y >= top && y <= bottom;

	}

	public static boolean isSegmentsIntersect(PointF p1, PointF p2, PointF p3,
			PointF p4) {
		float det = (p4.y-p3.y)*(p2.x-p1.x) - (p4.x-p3.x)*(p2.y-p1.y);
		if(Math.abs(det)<EPS){
			return false;
		}
		float ua = (p4.x-p3.x)*(p1.y-p3.y) - (p4.y-p3.y)*(p1.x-p3.x);
		float ub = (p2.x-p1.x)*(p1.y-p3.y) - (p2.y-p1.y)*(p1.x-p3.x);
		ua/=det;
		ub/=det;
		if(ua>=0.0f && ua<=1.0f && ub>=0.0f && ub<=1.0f){
			return true;
		}
		return false;
	}
	
	public static boolean isPointInsideCurve(PointF p,List<PointF> curve){
		if(curve.size()<3){
			return false;
		}
		PointF farAway = new PointF(p.x+15000.0f,p.y);
		PointF prev=null;
		int counter = 0;
		for(PointF curr:curve){
			if(prev==null){
				prev=curr; continue;
			}
			boolean b = isSegmentsIntersect(p, farAway, prev, curr);
			if(b){
				counter++;
			}
			prev=curr;
		}
		if(counter%2==0){
			return false;
		}else{
			return true;
		}
	}
	
	public static float distance(PointF p1,PointF p2){
		return (float) Math.sqrt(sqr(p1.x-p2.x)+sqr(p1.y-p2.y));
	}
	
	public static float crossProduct(PointF p1,PointF p2){
		return p1.x*p2.y-p2.x*p1.y;
	}
}
