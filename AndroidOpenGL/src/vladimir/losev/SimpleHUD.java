package vladimir.losev;

import java.util.ArrayList;
import java.util.List;

import vladimir.losev.HUDDraggableElement.Command;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

public class SimpleHUD extends ArrayList<HUDElement> implements HUD {	
	
	public SimpleHUD(int[] capacities) {
		this.capacities = capacities.clone();
		
		// OMFG!!! Sometimes I do hate Java
		this.slots = new ArrayList<ArrayList<HUDDraggableElement>>();
		for (int i = 0; i < capacities.length; ++i) {
			ArrayList<HUDDraggableElement> slotGroup = new ArrayList<HUDDraggableElement>();
			for (int j = 0; j < capacities[i]; ++j) {
				slotGroup.add(null);
			}
			
			this.slots.add(slotGroup);			
		}
		
		//elements = Collections.synchronizedList(new ArrayList<HUDElement>());
		elements = this;
	}
	
	public void init(float screenWidth, float screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		this.elementZoneSize = screenWidth / 4 / SLOT_GROUP_WIDTH;
		this.elementSize   = this.elementZoneSize * 0.8f;
		this.elementMargin = this.elementZoneSize * 0.1f;
		
		this.elements.clear();
		
		// Creating static elements to where you can drag elements
		float xOffset = screenWidth * 0.75f;
		float yOffset = elementZoneSize;
		float x = 0;
		float y = 0;
		
		for (int capacity : capacities) {
			for (int i = 0; i < capacity; ++i) {			
				x = xOffset + elementZoneSize * (i % 5);
				y = yOffset + elementZoneSize * (i / 5);
				
				HUDSlotElement newSlot = new HUDSlotElement(x, y, x + elementSize, y + elementSize);
				elements.add(newSlot);
			}
			yOffset +=  elementSize *  (2 + (capacity - 1) / 5);
		}
		// Creating panel from where you can drag elements
		x = elementMargin;
		y = elementMargin;
		
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.LEFT, false));
		x += elementZoneSize;
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.FORWARD, false));
		x += elementZoneSize;
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.RIGHT, false));
		x += elementZoneSize;
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.LIGHT, false));
		x += elementZoneSize;
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.RUN_A, false));
		x += elementZoneSize;
		elements.add(new HUDDraggableElement(x, y, x + elementSize, y + elementSize, Command.RUN_B, false));
		
		
	}	

	@Override
	public synchronized HUDElement get(int index) {
		return super.get(index);
	}
		
	@Override
	public void update(long time) {
		this.currTime = time;
		Log.d("Time", "ct="+time);
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
		originalPos = new RectF(element);
		elementOffsetX = element.left - x;
		elementOffsetY = element.top  - y;
		element.animator = null;
	}

	
	private void continueDragging(float x, float y) {
		draggedElement.offsetTo(x + elementOffsetX, y + elementOffsetY);
		float centerX = draggedElement.centerX();
		float centerY = draggedElement.centerY();
		
		// Fitting into slots
		for (HUDElement element : this) {
			if (element instanceof HUDSlotElement && element.contains(centerX, centerY)) {	
				HUDSlotElement se = ((HUDSlotElement) element);
				ArrayList<HUDElement> shiftedPositions = shift(se.group, se.index);
				
				for (int i = 0; i < shiftedPositions.size(); ++i) {
					HUDElement inSlotElement = shiftedPositions.get(i);
					if (inSlotElement != null) {
						float left = screenWidth * 0.75f + elementZoneSize * (i % 5);
						float top = elementZoneSize * (1 + i / 5);
						RectF endPos = new RectF(left, top, left + elementSize, top + elementSize);

						inSlotElement.animator = new HUDLinearAnimator(new RectF(inSlotElement), endPos, currTime, currTime);
					}
				}
			}
		}
	}
	
	
	/**	 */
	private ArrayList<HUDElement> shift(int group, int slot) {
		ArrayList<HUDElement> slotGroup = new ArrayList<HUDElement>(slots.get(group));

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
			return null;
		}

		if (nullIndex < slot) {
			for (int i = slot; i < group; ++i) {
				slotGroup.set(i, slotGroup.get(i + 1));
			}
		} else {
			for (int i = slot; i > group; --i) {
				slotGroup.set(i, slotGroup.get(i - 1));
			}
		}

		return slotGroup;
	}	
	
	
	private boolean onReleaseDragging() {
		float centerX = draggedElement.centerX();
		float centerY = draggedElement.centerY();
		RectF startPos = new RectF(draggedElement);
		RectF endPos   = new RectF(originalPos); 

		// Fitting into slots
		for (HUDElement element : this) {
			if (!(element instanceof HUDSlotElement)) {
				continue;
			}
			
			HUDSlotElement se = ((HUDSlotElement) element);
			if (se.contains(centerX, centerY)) {
				// Creating new element instead of the used one
				if (!draggedElement.isInSlot) {
					draggedElement.isInSlot = true;
					HUDDraggableElement de = new HUDDraggableElement(draggedElement);
					de.set(originalPos);
					de.isInSlot = false;
					elements.add(de);
				}
	
				slots.get(se.group).set(se.index, draggedElement);
				
				endPos = new RectF(se); 
				draggedElement.animator = new HUDLinearAnimator(startPos , endPos, currTime, currTime + 100);
				draggedElement = null;
				return true;
			}			
		}
		
		
		if (!draggedElement.isInSlot) {
			draggedElement.animator = new HUDLinearAnimator(startPos , endPos, currTime, currTime + 100);
		} else {
			elements.remove(draggedElement);
		}
		draggedElement = null;
		return true;			
	}
	
	
	/*private ArrayList<HUDElement> getSlotId(float x, float y)  {
	  if (x < offsetX) return null;
	  
	  int xIndex = (int) ((x - offsetX) / elementZoneSize);
	  int yIndex = 0;
	  
	  for (int capacity : capacities) {
		  capacity / SLOT_GROUP_WIDTH;
	  }
	  
	  return null;
	  
	  /*
	   * 	  if (x - offsetX < 0) return null;
	  
	  int globalXIndex = (int)((x - offsetX) / elementZoneSize);
	  int globalYIndex = (int)(y / elementZoneSize);

	  for (int slotGroupIndex = 0; int capacity : capacities) {
		  // MAGIC HAPENS HERE
		  int heightIndex = (int)(capacity + SLOT_GROUP_WIDTH - 2) / SLOT_GROUP_WIDTH;
		  if (globalYIndex < heightIndex) {
			  
		  } 
	  }
	  
	  return null;
	}*/
	
	private List<HUDElement> elements;
	private ArrayList<ArrayList<HUDDraggableElement>> slots;
	
	private final int SLOT_GROUP_WIDTH = 5;
	
	private HUDDraggableElement draggedElement = null;
	private float elementOffsetX;
	private float elementOffsetY;
	private RectF originalPos;
	
	long currTime;
	private int[] capacities;
	
	private float offsetX;
	private float offsetY;
	private float elementZoneSize;
	private float elementSize;
	private float elementMargin;
	private float screenWidth;
	private float screenHeight;
	
	private static final long serialVersionUID = -1610354679438821198L;



}
