package vladimir.losev;

import java.util.ArrayList;
import java.util.List;

import alex.taran.picworld.Command;
import android.graphics.RectF;
import android.view.MotionEvent;

public class SimpleHUD implements HUD {	
	
	public SimpleHUD(int[] capacities) {
		this.elements = new ArrayList<HUDElement>();
		this.capacities = capacities.clone();		
		this.confirmedSlots = new ArrayList<ArrayList<HUDDraggableElement>>();
		this.commands = new ArrayList<ArrayList<Command>>();
		
		for (int i = 0; i < capacities.length; ++i) {
			ArrayList<Command> commandGroup = new ArrayList<Command>();
			ArrayList<HUDDraggableElement> slotGroup = new ArrayList<HUDDraggableElement>();
			
			for (int j = 0; j < capacities[i]; ++j) {
				slotGroup.add(null);
				commandGroup.add(null);
			}
			
			this.confirmedSlots.add(slotGroup);
			this.commands.add(null);
		}
		
		this.tmpSlots = new ArrayList<ArrayList<HUDDraggableElement>>(this.confirmedSlots);		
	}
	
	public synchronized void init(float screenWidth, float screenHeight, Runnable onStart, Runnable onPause) {
		this.yOffsets = new float[capacities.length];
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.elementSize = screenWidth / 4 / SLOT_GROUP_WIDTH;
		
		this.onStart = onStart;
		this.onPause = onPause;
		
		elements.clear();
		
		// Creating static elements to where you can drag elements
		float xOffset = screenWidth * 0.75f;
		float yOffset = elementSize;
		float x = 0;
		float y = 0;
		
		for (int i = 0; i < capacities.length; ++i) {
			int capacity = capacities[i];

			for (int j = 0; j < capacity; ++j) {			
				x = xOffset + elementSize * (j % 5);
				y = yOffset + elementSize * (j / 5);
				
				HUDSlotElement newSlot = new HUDSlotElement(x, y, x + elementSize, y + elementSize);
				newSlot.group = i;
				newSlot.index = j;
				elements.add(newSlot);				
			}
			yOffsets[i] = yOffset;
			yOffset +=  elementSize *  (2 + (capacity - 1) / 5);
		}
		// Creating panel from where you can drag elements
		x = 0;
		y = elementMargin;
		
		elements.add(new HUDButtonElement(0.75f * screenWidth - 2 * elementSize, screenHeight - 2 * elementSize,
				0.75f * screenWidth, screenHeight, onStart));
		
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.ROTATE_LEFT, null));
		x += elementSize;
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.MOVE_FORWARD, null));
		x += elementSize;
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.ROTATE_RIGHT, null));
		x += elementSize;
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.TOGGLE_LIGHT, null));
		x += elementSize;
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.CALL_A, null));
		x += elementSize;
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.CALL_B, null));
		x += elementSize;
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.JUMP, null));
	}	

		
	@Override
	public synchronized void update(long time) {
		this.currTime = time;
		for (HUDElement element : elements) {
			element.update(time);
		}
	}	

	public synchronized void getElements(List<HUDElement> e) {
		e.clear();
		e.addAll(elements);
	}
	
	public synchronized ArrayList<ArrayList<Command>> getCommands() {
		commands.clear();
		for (int i = 0; i < confirmedSlots.size(); ++i) {
			ArrayList<Command> proc = new ArrayList<Command>();
			for (int j = 0; j < confirmedSlots.get(i).size(); ++j) {
				//commands.get(i).set(j, confirmedSlots.get(i).get(j).command);
				if(confirmedSlots.get(i).get(j)!=null) {
					proc.add(confirmedSlots.get(i).get(j).command);
				}
			}
			commands.add(proc);
		}
		
		return commands;
	}

	public synchronized HUDElement get(int index) {
		return elements.get(index);
	}
	
	@Override
	public synchronized boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();

		if (draggedElement != null) {
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				continueDragging(x, y);
				return true;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_OUTSIDE:
				motionCaptured = false;
				onReleaseDragging();
				return true;
			}
		} else {
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				if (motionCaptured) {
					return true;
				} else {
					return false;
				}
			case MotionEvent.ACTION_DOWN:
				for (HUDElement element : elements) {
					if (element instanceof HUDDraggableElement & element.contains(x, y)) {
						motionCaptured = true;
						startDragging((HUDDraggableElement) element, x, y);
						return true;
					} else if (element instanceof HUDButtonElement & element.contains(x, y)) {
						motionCaptured = true;
						HUDButtonElement button = ((HUDButtonElement) element);
						button.run();
						if (button.function.equals(onStart)) {
							button.function = onPause;
						} else {
							button.function = onStart;
						}
						return true;
					}
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_OUTSIDE:
				motionCaptured = false;
				return true;
			
			}
			
		}

		return false;
	}
	
	private synchronized void startDragging(HUDDraggableElement element, float x, float y) {
		draggedElement = element;
		elementOffsetX = element.left - x;
		elementOffsetY = element.top  - y;
		originalPos = new RectF(element);
		
		element.animator = null;
		
		tmpSlots = new ArrayList<ArrayList<HUDDraggableElement>>(confirmedSlots);
	
		int[] gi = draggedElement.slot;
		if (gi != null) {
			tmpSlots.get(gi[0]).set(gi[1], null);
		}
	}
	
	private synchronized void continueDragging(float x, float y) {
		draggedElement.offsetTo(x + elementOffsetX, y + elementOffsetY);
		float centerX = draggedElement.centerX();
		float centerY = draggedElement.centerY();
		int[] gi;
		
		if (null != (gi = findGroupAndIndex(centerX, centerY))) {
			shift(tmpSlots.get(gi[0]), gi[1]);
		}
		
		for (int i = 0; i < confirmedSlots.size(); ++i) {
			animateSlotElements(confirmedSlots.get(i), yOffsets[i]);
		}
	}
	
	private synchronized void onReleaseDragging() {
		float centerX = draggedElement.centerX();
		float centerY = draggedElement.centerY();
		int[] gi;
		
		// If element is over some slot
		if (null != (gi = findGroupAndIndex(centerX, centerY)) &&
			shift(tmpSlots.get(gi[0]), gi[1])) {
				if (draggedElement.slot == null) {
					HUDDraggableElement elem = new HUDDraggableElement(draggedElement);
					elem.set(originalPos);
					elements.add(elem);
				}
				
				confirmedSlots = tmpSlots;
				confirmedSlots.get(gi[0]).set(gi[1], draggedElement);
				draggedElement.slot = gi;
		} else if (draggedElement.slot != null) {
			confirmedSlots = new ArrayList<ArrayList<HUDDraggableElement>>(tmpSlots);
			draggedElement.slot = null;
			elements.remove(draggedElement);
		}
		
		draggedElement.animator = new HUDLinearAnimator(new RectF(draggedElement) , originalPos, currTime, currTime + ANIMATION_TIME_MILLS);
		draggedElement = null;
		
		for (int i = 0; i < confirmedSlots.size(); ++i) {
			for (int j = 0; j < confirmedSlots.get(i).size(); ++j) {
				if (confirmedSlots.get(i).get(j) != null) {
					confirmedSlots.get(i).get(j).slot[0] = i;
					confirmedSlots.get(i).get(j).slot[1] = j;
				}	
			}
			animateSlotElements(confirmedSlots.get(i), yOffsets[i]);
		}
		tmpSlots = null;
	}
	
	
	private void animateSlotElements(ArrayList<HUDDraggableElement> positions, float offsetTop) {
		for (int i = 0; i < positions.size(); ++i) {
			HUDDraggableElement inSlotElement = positions.get(i);
			if (inSlotElement != null) {
				float left = screenWidth * 0.75f + elementSize * (i % 5);
				float top = offsetTop + elementSize * (i / 5);
				RectF endPos = new RectF(left, top, left + elementSize, top + elementSize);
				
				// TODO(losik): do not rely on animators here
				// or just take speed as an argument
				if (inSlotElement.animator == null || !((HUDLinearAnimator) inSlotElement.animator).endPos.equals(endPos)) {
					inSlotElement.animator = new HUDLinearAnimator(new RectF(inSlotElement), endPos, currTime, currTime + ANIMATION_TIME_MILLS);
				}
			}
		}
		
	}
	
	private int[] findGroupAndIndex(float centerX, float centerY) {
		for (HUDElement element : elements) {
			if (element instanceof HUDSlotElement && 
				element.contains(centerX, centerY)) 
			{	
				HUDSlotElement se = ((HUDSlotElement) element);
				return new int[] {se.group, se.index};
			}
		}
		
		return null;
	}
	
	/**	 */
	private boolean shift(ArrayList<HUDDraggableElement> slotGroup, int slot) {
		if (slotGroup.get(slot) == null) {
			return true;
		}
		
		// TODO(losik): implement different prefered directions shifting
		// Searching for empty slots
		int nullIndex = -1;
		for (int i = 0; i < slotGroup.size(); ++i) {
			if (slotGroup.get(i) == null) {
				nullIndex = i;
				break;
			}
		}

		// No free space left in given slot group
		if (nullIndex < 0) {
			return false;
		}

		if (nullIndex < slot) {
			for (int i = nullIndex; i < slot; ++i) {
				slotGroup.set(i, slotGroup.get(i + 1));
			}
		} else {
			for (int i = nullIndex; i > slot; --i) {
				slotGroup.set(i, slotGroup.get(i - 1));
			}
		}
		slotGroup.set(slot, null);
		
		return true;
	}	
	
	
	private ArrayList<HUDElement> elements;
	
	private float elementSize;
	private float elementMargin;
	private float screenWidth;
	@SuppressWarnings(value = {"unused"})
	private float screenHeight;

	private Runnable onStart;
	private Runnable onPause;
	
	private final int SLOT_GROUP_WIDTH = 5;
	private final int ANIMATION_TIME_MILLS = 100;
	
	private HUDDraggableElement draggedElement = null;
	private float elementOffsetX;
	private float elementOffsetY;
	private RectF originalPos;
	
	long currTime;
	private boolean motionCaptured = false;
	
	private int[] capacities;
	private float[] yOffsets;
	private ArrayList<ArrayList<Command>> commands;
	private ArrayList<ArrayList<HUDDraggableElement>> confirmedSlots;
	private ArrayList<ArrayList<HUDDraggableElement>> tmpSlots;
	
	@SuppressWarnings(value = {"unused"})
	private static final long serialVersionUID = -1610354679438821198L;
}
