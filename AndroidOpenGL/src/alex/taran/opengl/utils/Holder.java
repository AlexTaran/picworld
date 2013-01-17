package alex.taran.opengl.utils;

import android.content.Context;

public abstract class Holder {
	protected Context context;
	
	public Holder(Context theContext){
		context = theContext;
	}
	
	public abstract void clear();
	
	@Override
	public void finalize(){
		clear();
	}
}
