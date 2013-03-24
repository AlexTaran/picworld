/* COPYRIGHT (C) 2013 Vladimir Losev. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package vladimir.losev;


public class HUDSlotElement extends HUDElement{
	public HUDSlotElement(float left, float top, float right, float bottom) {
		super(left, top, right, bottom);
	}

	public HUDSlotElement(HUDElement element) {
		super(element);
	}
	
	public int group;
	public int index;
}
