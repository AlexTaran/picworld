package vladimir.losev;

import alex.taran.picworld.Command;


public class HUDDraggableElement extends HUDElement {
	public HUDDraggableElement(float left, float top, float right, float bottom, Command command, int[] slot) {
		super(left, top, right, bottom);
		this.command = command;
		this.slot = slot;
	}

	public HUDDraggableElement(HUDElement e, Command command, int[] slot) {
		super(e);
		this.command = command;
		this.slot = slot;
	}

	public HUDDraggableElement(HUDDraggableElement e) {
		this(e, e.command, e.slot);
	}

	
	public Command command;
	public int[] slot;
}
