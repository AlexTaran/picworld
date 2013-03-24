/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.utils;

import android.content.Context;

public abstract class Holder {
	//protected Context context;
	
	public Holder(/*Context theContext*/) {
		//context = theContext;
	}
	
	public abstract void clear();
	
	@Override
	public void finalize(){
		clear();
	}
}
