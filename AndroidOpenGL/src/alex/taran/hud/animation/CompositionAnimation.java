package alex.taran.hud.animation;

import alex.taran.hud.AbstractHUDSystem;

public class CompositionAnimation implements HUDAnimation {
	private final HUDAnimation anim1;
	private final HUDAnimation anim2;
	private boolean firstAnimating;

	public CompositionAnimation(HUDAnimation anim1, HUDAnimation anim2) {
		this.anim1 = anim1;
		this.anim2 = anim2;
		firstAnimating = true;
	}
	
	@Override
	public boolean isCompleted(AbstractHUDSystem hudSystem, String elementName) {
		// TODO Auto-generated method stub
		if (firstAnimating) {
			return false;
		} else {
			return anim2.isCompleted(hudSystem, elementName);
		}
	}

	@Override
	public void animate(AbstractHUDSystem hudSystem, String elementName,
			float deltaTime) {
		if (firstAnimating) {
			anim1.animate(hudSystem, elementName, deltaTime);
			if (anim1.isCompleted(hudSystem, elementName)) {
				anim1.onFinished(hudSystem, elementName);
				firstAnimating = false;
			}
		} else {
			anim2.animate(hudSystem, elementName, deltaTime);
		}
	}

	@Override
	public void onFinished(AbstractHUDSystem hudSystem, String elementName) {
		anim2.onFinished(hudSystem, elementName);
	}

}
