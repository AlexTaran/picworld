package alex.taran.opengl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.graphics.PointF;

public class ControlState {
	public float scale = 1.0f;
	public PointF offset = new PointF();
	public PointF surfaceSizes = new PointF();
	public Set<Integer> selectedPoints = new HashSet<Integer>();
	public List<PointF> selectionCurve = new LinkedList<PointF>();
	
	public enum ControlMode{SELECT,SCALING, DECLARE_GO};
	public ControlMode controlMode = ControlMode.SCALING;
	
	public float countButtonSize(){
		return Math.min(surfaceSizes.x,surfaceSizes.y)/5.0f;
	}
}