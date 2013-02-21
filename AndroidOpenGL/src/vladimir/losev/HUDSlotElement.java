package vladimir.losev;


public class HUDSlotElement extends HUDElement{
	public HUDSlotElement(float left, float top, float right, float bottom) {
		super(left, top, right, bottom);
	}

	public HUDSlotElement(HUDElement element) {
		super(element);
	}
	
	public int group;
	public int index;
}
