/* COPYRIGHT (C) 2013 Vladimir Losev. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package vladimir.losev;


import java.util.List;

import android.view.MotionEvent;

public interface HUD {
	void update(long time);
	boolean onTouchEvent(MotionEvent m);
	public void getElements(List<HUDElement> e);
}
