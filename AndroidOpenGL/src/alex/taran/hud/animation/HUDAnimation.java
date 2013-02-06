package alex.taran.hud.animation;

import alex.taran.hud.AbstractHUDSystem;
import alex.taran.hud.HUDElement;

public interface HUDAnimation {
	public boolean isCompleted(AbstractHUDSystem hudSystem, String elementName);
	public void animate(AbstractHUDSystem hudSystem, String elementName, float deltaTime);
	public void onFinished(AbstractHUDSystem hudSystem, String elementName);
}
