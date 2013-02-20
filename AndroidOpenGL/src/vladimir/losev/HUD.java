package vladimir.losev;


import android.view.MotionEvent;

public interface HUD extends Iterable<HUDElement> {
	void update(long time);
	boolean onTouchEvent(MotionEvent m);
	
	//HUDElement getTouchedElementsNames(float x, float y);
	//HUDElement getTouchedElementName(MotionEvent m);
}
