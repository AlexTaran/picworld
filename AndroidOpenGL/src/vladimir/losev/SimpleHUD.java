package vladimir.losev;

import java.util.ArrayList;

import vladimir.losev.HUDDraggableElement.Command;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

public class SimpleHUD extends ArrayList<HUDElement> implements HUD {	
	
	public SimpleHUD(int[] capacities) {
		this.capacities = capacities.clone();
		
		// OMFG!!! Sometimes I do hate Java
		this.confimedSlots = new ArrayList<ArrayList<HUDDraggableElement>>();
		for (int i = 0; i < capacities.length; ++i) {
			ArrayList<HUDDraggableElement> slotGroup = new ArrayList<HUDDraggableElement>();
			for (int j = 0; j < capacities[i]; ++j) {
				slotGroup.add(null);
			}		
			this.confimedSlots.add(slotGroup);			
		}
		this.tmpSlots= new ArrayList<ArrayList<HUDDraggableElement>>(this.confimedSlots);		
	}
	
	public void init(float screenWidth, float screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		this.elementSize = screenWidth / 4 / SLOT_GROUP_WIDTH;
		
		clear();
		
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
				add(newSlot);
			}
			yOffset +=  elementSize *  (2 + (capacity - 1) / 5);
		}
		// Creating panel from where you can drag elements
		x = 0;
		y = elementMargin;
		
		add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.LEFT, null));
		x += elementSize;
		add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.FORWARD, null));
		x += elementSize;
		add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.RIGHT, null));
		x += elementSize;
		add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.LIGHT, null));
		x += elementSize;
		add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.RUN_A, null));
		x += elementSize;
		add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.RUN_B, null));
	}	

		
	@Override
	public void update(long time) {
		this.currTime = time;
		for (HUDElement element : this) {
			element.update(time);
		}
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
				onReleaseDragging();
				return true;
			}
		} else if (action == MotionEvent.ACTION_DOWN) {
			for (HUDElement element : this) {
				if (element instanceof HUDDraggableElement & element.contains(x, y)) {
					startDragging((HUDDraggableElement) element, x, y);
					return true;
				}
			}
		}

		return false;
	}
	

	/** Temporary method for backward compatibility **/
	public Iterable<HUDElement> getElements() {
		return new ArrayList<HUDElement>(this);
	}
	
	
	private void startDragging(HUDDraggableElement element, float x, float y) {
		draggedElement = element;
		elementOffsetX = element.left - x;
		elementOffsetY = element.top  - y;
		originalPos = new RectF(element);
		
		element.animator = null;
		
		tmpSlots = new ArrayList<ArrayList<HUDDraggableElement>>(confimedSlots);
	
		int[] gi = draggedElement.slot;
		if (gi != null) {
			tmpSlots.get(gi[0]).set(gi[1], null);
		}
	}
	
	private void continueDragging(float x, float y) {
		draggedElement.offsetTo(x + elementOffsetX, y + elementOffsetY);
		float centerX = draggedElement.centerX();
		float centerY = draggedElement.centerY();
		int[] gi;
		
		if (null != (gi = findGroupAndIndex(centerX, centerY))) {
			shift(tmpSlots.get(gi[0]), gi[1]);
			Log.i("Fuck", tmpSlots.get(0).toString());	
		}
		
		// TODO(losik): add support for multiple slotGroup
		animateSlotElements(tmpSlots.get(0), 0);


	}
	
	private void onReleaseDragging() {
		float centerX = draggedElement.centerX();
		float centerY = draggedElement.centerY();
		int[] gi;
		
		// If element is over some slot
		if (null != (gi = findGroupAndIndex(centerX, centerY))) {
			if (draggedElement.slot == null) {
				HUDDraggableElement elem = new HUDDraggableElement(draggedElement);
				elem.set(originalPos);
				add(elem);
			}

			if (shift(tmpSlots.get(gi[0]), gi[1])) {
				confimedSlots.set(gi[0], tmpSlots.get(gi[0]));
				confimedSlots.get(gi[0]).set(gi[1], draggedElement);
				draggedElement.slot = gi;
			}
		} else if (draggedElement.slot != null) {
			confimedSlots = new ArrayList<ArrayList<HUDDraggableElement>>(tmpSlots);
			draggedElement.slot = null;
			remove(draggedElement);
		}
		
		draggedElement.animator = new HUDLinearAnimator(new RectF(draggedElement) , originalPos, currTime, currTime + ANIMATION_TIME_MILLS);
		draggedElement = null;
		
		// TODO(losik): add support for multiple slotGroup
		animateSlotElements(confimedSlots.get(0), 0);
		for (int i = 0; i < confimedSlots.get(0).size(); ++i) {
			if (confimedSlots.get(0).get(i) != null) {
				confimedSlots.get(0).get(i).slot[1] = i;
			}
		}
		tmpSlots = null;
	}
	
	
	private void animateSlotElements(ArrayList<HUDDraggableElement> positions, float offsetTop) {
		for (int i = 0; i < positions.size(); ++i) {
			HUDDraggableElement inSlotElement = positions.get(i);
			if (inSlotElement != null) {
				float left = screenWidth * 0.75f + elementSize * (i % 5);
				float top = offsetTop + elementSize * (1 + i / 5);
				RectF endPos = new RectF(left, top, left + elementSize, top + elementSize);
				
				// TODO(losik): do not rely on animators here
				if (inSlotElement.animator == null || !((HUDLinearAnimator) inSlotElement.animator).endPos.equals(endPos)) {
					inSlotElement.animator = new HUDLinearAnimator(new RectF(inSlotElement), endPos, currTime, currTime + ANIMATION_TIME_MILLS);
				}
			}
		}
		
	}
	
	
	private int[] findGroupAndIndex(float centerX, float centerY) {
		for (HUDElement element : this) {
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
	
	
	
	private ArrayList<ArrayList<HUDDraggableElement>> confimedSlots;
	private ArrayList<ArrayList<HUDDraggableElement>> tmpSlots;
	
	private final int SLOT_GROUP_WIDTH = 5;
	private final int ANIMATION_TIME_MILLS = 100;
	
	private HUDDraggableElement draggedElement = null;

	private float elementOffsetX;
	private float elementOffsetY;
	private RectF originalPos;
	
	long currTime;
	private int[] capacities;
	
	private float elementSize;
	private float elementMargin;
	private float screenWidth;
	private float screenHeight;
	
	private static final long serialVersionUID = -1610354679438821198L;



}
