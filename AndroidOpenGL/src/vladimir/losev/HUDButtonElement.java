/* COPYRIGHT (C) 2013 Vladimir Losev. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
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
