/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
/*package alex.taran.hud.animation;

import alex.taran.hud.AbstractHUDSystem;
import alex.taran.hud.HUDElement;

public class InstantCreateElementAnimation implements HUDAnimation {
	private final HUDElement element;
	private final String elementName; 
	
	public InstantCreateElementAnimation(HUDElement element, String elementName) {
		this.element = element.clone();
		this.elementName = elementName;
	}

	@Override
	public boolean isCompleted(AbstractHUDSystem hudSystem, String elementName) {
		return true;
	}

	@Override
	public void animate(AbstractHUDSystem hudSystem, String elementName, float deltaTime) {
	}

	@Override
	public void onFinished(AbstractHUDSystem hudSystem, String elementName) {
		hudSystem.addElement(elementName, element);
	}

}
*/