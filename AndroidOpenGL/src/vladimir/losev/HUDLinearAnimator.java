package vladimir.losev;

import android.graphics.RectF;
import android.util.Log;

public class HUDLinearAnimator implements HUDElementAnimator {
	public HUDLinearAnimator(RectF startPos, RectF endPos,long startTime, long endTime) {
		this.startPos = startPos;
		this.endPos = endPos;
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = endTime - startTime;
	}
	
	@Override
	public void update(HUDElement element, long time) {
		if (time <= startTime) {
			element.set(startPos);
		} else if (time >= endTime) {
			element.set(endPos);
		} else {
			float t = 1f * (time - startTime) / duration;
			element.left   = (1 - t) * startPos.left   + t * endPos.left;
			element.right  = (1 - t) * startPos.right  + t * endPos.right;
			element.top    = (1 - t) * startPos.top    + t * endPos.top;
			element.bottom = (1 - t) * startPos.bottom + t * endPos.bottom;
		}
	}	
	
	public RectF startPos;
	public RectF endPos;
	protected long startTime;
	protected long endTime;
	protected long duration;
}
