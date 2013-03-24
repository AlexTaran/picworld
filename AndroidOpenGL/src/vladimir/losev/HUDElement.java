/* COPYRIGHT (C) 2013 Vladimir Losev. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package vladimir.losev;

import android.graphics.RectF;

public class HUDElement extends RectF {
	
	public HUDElement(float left, float top, float right, float bottom) {
		super(left, top,right, bottom);
	}
	
	public HUDElement(RectF rect) {
		super(rect);
	}
	
	public HUDElement(HUDElement another) {
		this.animator = another.animator;
	}
	
	public void update(long time) {
		if (animator != null) {
			this.animator.update(this, time);
		}
	}
	
	public HUDElementAnimator animator;
}
