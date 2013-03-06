package vladimir.losev;

public class HUDButtonElement extends HUDElement implements Runnable {

	public HUDButtonElement(float left, float top, float right, float bottom, Runnable function) {
		super(left, top, right, bottom);
		this.function = function;
	}
	
	public void run() {
		function.run();
	}
	
	public Runnable function;
}
