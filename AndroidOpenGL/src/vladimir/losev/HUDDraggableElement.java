package vladimir.losev;


public class HUDDraggableElement extends HUDElement {
	public HUDDraggableElement(float left, float top, float right, float bottom, Command command, boolean isInSlot) {
		super(left, top, right, bottom);
		this.command = command;
		this.isInSlot = isInSlot;
	}

	public HUDDraggableElement(HUDElement e, Command command, boolean isInSlot) {
		super(e);
		this.command = command;
		this.isInSlot = isInSlot;
	}

	public HUDDraggableElement(HUDDraggableElement e) {
		this(e, e.command, e.isInSlot);
	}

	
	public enum Command {
		NONE,
		FORWARD,
		LIGHT,
		LEFT,
		RIGHT,
		RUN_A,
		RUN_B
	}
	
	public Command command;
	public boolean isInSlot;
}
