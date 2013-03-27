/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.utils;

import java.util.HashMap;
import java.util.Map;

import alex.taran.opengl.AndroidOpenGLActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class TextureHolder extends Holder {
	private final int[] temp = new int[1]; // for temporary texture ID
	private Map<String,Integer> textures = new HashMap<String,Integer>();
	
	public TextureHolder() {
		super();
	}
	
	public TextureHolder bind(String s){
		Integer i = textures.get(s);
		if(i!=null){
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, i);
		}
		return this;
	}
	
	public void unbind(){
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	}
		
	public TextureHolder load(String s,int resId) {
		delete(s);
		Bitmap bitmap = null;
		//Log.d("FUCK", "trying to load texture " + s + " from R.id = " + resId);
		try {
			bitmap = BitmapFactory.decodeResource(AndroidOpenGLActivity.getContext().getResources(), resId);
		} catch (Exception e) {
			Log.e("FUCK", "Error while loading texture " + s + " from R.id = "+resId);
		}
		
		GLES20.glGenTextures(1, temp, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, temp[0]);
		//GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		//GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		//setClamping(clamping);
		//GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

		bitmap.recycle();
		textures.put(s, temp[0]);
		//unbind();
		return setLinearFiltering();
	}
	
	public TextureHolder setClamping(boolean clamping) {
		if (clamping) {
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
		} else {
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
		    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
		}
		return this;
	}
	
	public TextureHolder setNearestFiltering() {
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		return this;
	}
	
	public TextureHolder setLinearFiltering() {
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		return this;
	}
	
	public TextureHolder setMipmapFiltering() {
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		return this;
	}
	
	public TextureHolder setMipmapFilteringAndGenerateMipmaps() {
		setMipmapFiltering();
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		return this;
	}
	
	public void createEmpty(String s, int sizeX, int sizeY, int internalFormat, int format) {
		delete(s);
		GLES20.glGenTextures(1, temp, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, temp[0]);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
	    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, internalFormat, sizeX, sizeY, 0, format,
	    		GLES20.GL_UNSIGNED_BYTE, null);
		textures.put(s, temp[0]);
		unbind();
	}
	
	public void attachToCurrentFrameBuffer(String tex, int attachmentType) {
		Integer i = textures.get(tex);
		if (i != null) {
			GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, attachmentType, GLES20.GL_TEXTURE_2D, i, 0);
		}
	}
	
	public void delete(String s){
		Integer i = textures.get(s);
		if (i != null) {
			temp[0] = i;
			GLES20.glDeleteTextures(1, temp, 0);
			textures.remove(s);
		}
	}
	
	@Override
	public void clear(){
		for (Integer i: textures.values()) {
			temp[0] = i;
			GLES20.glDeleteTextures(1, temp, 0);
		}
		textures.clear();
	}
}
