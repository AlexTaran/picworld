/*package alex.taran.hud.animation;

import alex.taran.hud.AbstractHUDSystem;
import alex.taran.hud.HUDElement;

public class ResizeAnimation implements HUDAnimation {
	private final HUDElement destinationElement;
	private final float speed; 
	
	public ResizeAnimation(float speed, HUDElement destinationElement) {
		this.destinationElement = destinationElement.clone();
		this.speed = speed;
	}
	
	@Override
	public boolean isCompleted(AbstractHUDSystem hudSystem, String elementName) {
		HUDElement element = hudSystem.getElement(elementName);
		if (Math.abs(element.sizeX - destinationElement.sizeX) > 0.001) {
			return false;
		}
		if (Math.abs(element.sizeY - destinationElement.sizeY) > 0.001) {
			return false;
		}
		return true;
	}

	@Override
	public void animate(AbstractHUDSystem hudSystem, String elementName, float deltaTime) {
		HUDElement element = hudSystem.getElement(elementName);
		float vx = destinationElement.sizeX - element.sizeX;
		float vy = destinationElement.sizeY - element.sizeY;
		float l = (float)Math.sqrt(vx * vx + vy * vy);
		if (l > 0.001) {
			float dx = vx / l * speed * deltaTime;
			float dy = vy / l * speed * deltaTime;
			if (Math.abs(vx) < Math.abs(dx)) {
				element.sizeX = destinationElement.sizeX;
			} else {
				element.sizeX +=dx;
			}
			if (Math.abs(vy) < Math.abs(dy)) {
				element.sizeY = destinationElement.sizeY;
			} else {
				element.sizeY +=dy;
			}
		} else {
			element.sizeX = destinationElement.sizeX;
			element.sizeY = destinationElement.sizeY;
		}

	}

	@Override
	public void onFinished(AbstractHUDSystem hudSystem, String elementName) {
		// TODO Auto-generated method stub

	}

}*/
