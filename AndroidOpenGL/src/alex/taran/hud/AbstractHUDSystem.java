/*package alex.taran.hud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import alex.taran.hud.animation.HUDAnimation;
import android.view.MotionEvent;

public abstract class AbstractHUDSystem {
	
	public void onUpdate(float deltaTime) { // time in seconds
		for (Entry<String, HUDAnimation> e: animations.entrySet()) {
			HUDElement elem = elements.get(e.getKey());
			if (elem == null || e.getValue().isCompleted(this, e.getKey())) {
				e.getValue().onFinished(this, e.getKey());
				animations.remove(e.getKey());
			} else {
				e.getValue().animate(this, e.getKey(), deltaTime);
			}
		}
	}
	
	public abstract boolean onTouchEvent(MotionEvent m);
	
	public Map<String, HUDElement> getElements() {
		return Collections.unmodifiableMap(elements);
	}
	
	public void addElement(String name, HUDElement elem) {
		elements.put(name, elem);
	}
	
	public HUDElement getElement(String name) {
		return elements.get(name);
	}
	
	public HUDElement removeElement(String name) {
		return elements.remove(name);
	}
	
	public void cancelAnimationOnElement(String name) {
		animations.remove(name);
	}
	
	protected String getTouchedElementName(MotionEvent m) {
		for (Entry<String, HUDElement> e: elements.entrySet()) {
			if (e.getValue().isPointInElementArea(m.getX(), m.getY())) {
				return e.getKey();
			}
		}
		return null;
	}
	
	protected List<String> getTouchedElementsNames(float x, float y) {
		List<String> touchedElementsNames = new ArrayList<String>();
		for (Entry<String, HUDElement> e: elements.entrySet()) {
			if (e.getValue().isPointInElementArea(x, y)) {
				touchedElementsNames.add(e.getKey());
			}
		}
		return touchedElementsNames;
	}
	
	protected List<String> getTouchedElementsNames(MotionEvent m) {
		return getTouchedElementsNames(m.getX(), m.getY());
	}
	
	protected void setAnimation(String elementName, HUDAnimation animation) {
		animations.put(elementName, animation);
	}
	
	protected void cleanup() {
		elements.clear();
		animations.clear();
	}
	
	private final Map<String, HUDElement> elements = new HashMap<String, HUDElement>();
	private final Map<String, HUDAnimation> animations = new HashMap<String, HUDAnimation>();
}*/
