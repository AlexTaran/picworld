package alex.taran.hud;

public class HUDElement implements Cloneable {
	public float positionX, positionY;
	public float sizeX, sizeY;
	
	public HUDElement () {
		positionX = positionY = 0.0f;
		sizeX = sizeY = 0.0f;
	}
	
	public HUDElement (float positionX, float positionY,
			float sizeX, float sizeY) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	public boolean isPointInElementArea(float x, float y) {
		return x >= positionX && y >= positionY &&
			   x <= positionX + sizeX &&
			   y <= positionY + sizeY;
	}
	
	public HUDElement setPositionX(float positionX) {
		this.positionX = positionX;
		return this;
	}
	
	public HUDElement setPositionY(float positionY) {
		this.positionY = positionY;
		return this;
	}
	
	public HUDElement setSizeX(float sizeX) {
		this.sizeX = sizeX;
		return this;
	}
	
	public HUDElement setSizeY(float sizeY) {
		this.sizeY = sizeY;
		return this;
	}
	
	@Override
	public HUDElement clone() {
		HUDElement elem = new HUDElement(positionX, positionY,
				sizeX, sizeY);
		return elem;
	}
}
