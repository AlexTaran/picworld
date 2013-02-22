package vladimir.losev;


import java.util.List;

import android.view.MotionEvent;

public interface HUD {
	void update(long time);
	boolean onTouchEvent(MotionEvent m);
	public void getElements(List<HUDElement> e);
}
