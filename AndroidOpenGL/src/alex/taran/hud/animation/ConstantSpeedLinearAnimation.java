package alex.taran.hud.animation;

import alex.taran.hud.AbstractHUDSystem;
import alex.taran.hud.HUDElement;
import android.util.Log;

public class ConstantSpeedLinearAnimation implements HUDAnimation {
	private final HUDElement destinationElement;
	private final float speed; 
	
	public ConstantSpeedLinearAnimation(float speed, HUDElement destinationElement) {
		this.destinationElement = destinationElement.clone();
		this.speed = speed;
	}
	
	@Override
	public boolean isCompleted(AbstractHUDSystem hudSystem, String elementName) {
		HUDElement element = hudSystem.getElement(elementName);
		if (Math.abs(element.positionX - destinationElement.positionX) > 0.001) {
			return false;
		}
		if (Math.abs(element.positionY - destinationElement.positionY) > 0.001) {
			return false;
		}
		return true;
	}

	@Override
	public void animate(AbstractHUDSystem hudSystem, String elementName, float deltaTime) {
		HUDElement element = hudSystem.getElement(elementName);
		float vx = destinationElement.positionX - element.positionX;
		float vy = destinationElement.positionY - element.positionY;
		float l = (float)Math.sqrt(vx * vx + vy * vy);
		//Log.d("TEST", "dt = " + deltaTime + " vx = " + vx + " vy = " + vy + " l = " + l);
		if (l > 0.001) {
			float dx = vx / l * speed * deltaTime;
			float dy = vy / l * speed * deltaTime;
			//Log.d("TEST", "dx = " + dx + " dy = " + dy);
			if (Math.abs(vx) < Math.abs(dx)) {
				element.positionX = destinationElement.positionX;
			} else {
				element.positionX +=dx;
			}
			if (Math.abs(vy) < Math.abs(dy)) {
				element.positionY = destinationElement.positionY;
			} else {
				element.positionY +=dy;
			}
		} else {
			element.positionX = destinationElement.positionX;
			element.positionY = destinationElement.positionY;
		}
	}
	
	@Override
	public void onFinished(AbstractHUDSystem hudSystem, String elementName) {
		
	}
}
