/*package alex.taran;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import alex.taran.hud.AbstractHUDSystem;
import alex.taran.hud.HUDElement;
import alex.taran.hud.animation.CompositionAnimation;
import alex.taran.hud.animation.ConstantSpeedLinearAnimation;
import alex.taran.hud.animation.HUDAnimation;
import alex.taran.hud.animation.InstantCreateElementAnimation;
import alex.taran.hud.animation.ResizeAnimation;
import alex.taran.picworld.Program;
import android.view.MotionEvent;

public class ProgrammingUI extends AbstractHUDSystem {	
	private boolean dragging = false;
	private String draggingElementName = null;
	private float previousX;
	private float previousY;
	
	private final ArrayList<Integer> functionCapacities;
	
	private float elementSize;
	private float marginSize;
	
	public ProgrammingUI(List<Integer> functionCapacities) {
		this.functionCapacities = new ArrayList<Integer>(functionCapacities);
	}
	
	public void generateElements(float screenWidth, float screenHeight) {
		cleanup();

		elementSize = screenWidth / 4f / 5f;
		marginSize = elementSize / 10f;
		float xOffset = screenWidth * 0.75f;
		float yOffset = elementSize;
		float dimention = elementSize - marginSize * 2;
		Random rand = new Random();
		
		for (int capacity : functionCapacities) {
			for (int i = 0; i < capacity; ++i) {			
				float x = xOffset + marginSize + elementSize * (i % 5);
				float y = yOffset + marginSize + elementSize * (i / 5);
				
				HUDElement elem = new HUDElement(x, y, dimention, dimention);
				addElement("slots/procA/" +  rand.nextInt() + '/' + i, elem);
			}
			yOffset +=  elementSize *  (2 + (capacity - 1) / 5);
		}
		

		// buttons
		addElement("commands/forward", makeInitialHUDElementForCommand("commands/forward"));
		addElement("commands/left", makeInitialHUDElementForCommand("commands/left"));
		addElement("commands/right", makeInitialHUDElementForCommand("commands/right"));
		addElement("commands/light", makeInitialHUDElementForCommand("commands/light"));
		addElement("commands/run_a", makeInitialHUDElementForCommand("commands/run_a"));
		addElement("commands/run_b", makeInitialHUDElementForCommand("commands/run_b"));
	}
	
	private HUDElement makeInitialHUDElementForCommand(String command) {
		if (command.equals("commands/forward")) {
			return new HUDElement(marginSize, marginSize, elementSize - marginSize * 2, elementSize - marginSize * 2);
		} else if (command.equals("commands/left")) {
			return new HUDElement(marginSize + elementSize, marginSize, elementSize - marginSize * 2, elementSize - marginSize * 2);
		} else if (command.equals("commands/right")) {
			return new HUDElement(marginSize + elementSize * 2, marginSize, elementSize - marginSize * 2, elementSize - marginSize * 2);
		} else if (command.equals("commands/light")) {
			return new HUDElement(marginSize + elementSize * 3, marginSize, elementSize - marginSize * 2, elementSize - marginSize * 2);
		} else if (command.equals("commands/run_a")) {
			return new HUDElement(marginSize + elementSize * 4, marginSize, elementSize - marginSize * 2, elementSize - marginSize * 2);
		} else if (command.equals("commands/run_b")) {
			return new HUDElement(marginSize + elementSize * 5, marginSize, elementSize - marginSize * 2, elementSize - marginSize * 2);
		} else {
			throw new RuntimeException("Trying to generate a HUDElement for unknown commandName");
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent m) {
		if (m.getAction() == MotionEvent.ACTION_DOWN) {
			if (dragging) {
				return false;
			}
			
			List<String> touchedElementsNames = getTouchedElementsNames(m);
			for (String s : touchedElementsNames) {
				if (s.startsWith("slots/")) {
					continue;
				}
				dragging = true;
				draggingElementName = s;
				cancelAnimationOnElement(draggingElementName);
				previousX = m.getX();
				previousY = m.getY();
				return true;
			}
			return false;
		} else if (m.getAction() == MotionEvent.ACTION_MOVE) {
			if (!dragging) { 
				return false;
			}
			HUDElement elem = getElement(draggingElementName);
			elem.positionX += m.getX() - previousX;
			elem.positionY += m.getY() - previousY;
			previousX = m.getX();
			previousY = m.getY();
		} if (m.getAction() == MotionEvent.ACTION_UP) {
			if (!dragging) { 
				return false;
			}
			HUDElement elem = getElement(draggingElementName);
			float centerX = elem.positionX + elem.sizeX / 2;
			float centerY = elem.positionY + elem.sizeY / 2;
			String slotName = getSlotNameAtPoint(centerX, centerY);
			if (slotName != null) {
				
				HUDAnimation animation = new ConstantSpeedLinearAnimation(200.0f, getElement(slotName));
				HUDAnimation removing = new RemoveCommandsOnSlotAnimation(slotName);
				HUDAnimation comp1 = new CompositionAnimation(animation, removing);
				HUDAnimation creation = new InstantCreateElementAnimation(makeInitialHUDElementForCommand(draggingElementName).setSizeX(0.0f).setSizeY(0.0f), draggingElementName + "/" + slotName);
				HUDAnimation resize = new ResizeAnimation(5.0f, makeInitialHUDElementForCommand(draggingElementName));
				HUDAnimation comp2 = new CompositionAnimation(creation, resize);
				HUDAnimation comp = new CompositionAnimation(comp1, comp2);
				setAnimation(draggingElementName, comp);
				
				//setAnimation(draggingElementName, animation);
			} else if (draggingElementName.startsWith("commands/") && slotName == null) {
				setAnimation(draggingElementName, new ConstantSpeedLinearAnimation(1500.0f, makeInitialHUDElementForCommand(draggingElementName)));
			}
			dragging = false;
			draggingElementName = null;
		}
		return true;
	}
	
	private String getSlotNameAtPoint(float x, float y) {
		List<String> names = getTouchedElementsNames(x, y);
		for (String s : names) {
			if (s.startsWith("slots/")) {
				return s;
			}
		}
		return null;
	}
	
	private List<String> getCommandsAtSlot(String slotName) {
		HUDElement slot = getElement(slotName);
		float centerX = slot.positionX + slot.sizeX / 2;
		float centerY = slot.positionY + slot.sizeY / 2;
		List<String> commands = new ArrayList<String>();
		List<String> allElements = getTouchedElementsNames(centerX, centerY);
		for (String s: allElements) {
			if (s.startsWith("commands/")) {
				commands.add(s);
			}
		}
		return commands;
	}
	
	public Program getProgram() {
		return null;
	}
	
	private class RemoveCommandsOnSlotAnimation implements HUDAnimation {
		private String slotName;
		public RemoveCommandsOnSlotAnimation(String slotName) {
			this.slotName = slotName;
		}
		@Override
		public boolean isCompleted(AbstractHUDSystem hudSystem, String elementName) {
			return true;
		}

		@Override
		public void animate(AbstractHUDSystem hudSystem, String elementName,
				float deltaTime) {
		}

		@Override
		public void onFinished(AbstractHUDSystem hudSystem, String elementName) {
			List<String> commands = getCommandsAtSlot(slotName);
				for (String cmd: commands) {
					if (!cmd.equals(elementName)) {
					removeElement(cmd);
				}
			}
			addElement(elementName, makeInitialHUDElementForCommand(elementName));
		}
	}
}*/
