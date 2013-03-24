/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.utils;

import java.util.HashMap;
import java.util.Map;

import android.opengl.GLES20;

public class FrameBufferHolder extends Holder {
	private final int[] temp = new int[1]; // for temporary FBO ID
	private Map<String,Integer> frameBuffers = new HashMap<String,Integer>();
	
	public FrameBufferHolder() {
	}
	
	public void create(String name) {
		delete(name);
		GLES20.glGenFramebuffers(1, temp, 0);
		frameBuffers.put(name, temp[0]);
	}
	
	public void delete(String name) {
		if (frameBuffers.containsKey(name)) {
			temp[0] = frameBuffers.get(name);
			GLES20.glDeleteFramebuffers(1, temp, 0);
			frameBuffers.remove(name);
		}
	}
	
	public void bind(String s) {
		Integer i = frameBuffers.get(s);
		if (i != null) {
			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, i);
		}
	}
	
	public void unbind() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}
	
	// status-checking methods
	
	public int getCurrentStatus() {
		return GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
	}
	
	public boolean isCurrentStatusComplete() {
		return getCurrentStatus() == GLES20.GL_FRAMEBUFFER_COMPLETE;
	}
	
	public String getCurrentStatusString() {
		int status = getCurrentStatus();
		switch(status) {
		case GLES20.GL_FRAMEBUFFER_COMPLETE: 
			return "GL_FRAMEBUFFER_COMPLETE";
		case GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT: 
			return "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
		case GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT: 
			return "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
		case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
			return "GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS";
		case GLES20.GL_FRAMEBUFFER_UNSUPPORTED:
			return "GL_FRAMEBUFFER_UNSUPPORTED";
		default: return "Unknown FrameBuffer status: " + status;
		}
	}

	// from Holder
	
	@Override
	public void clear() {
		for (Integer i: frameBuffers.values()) {
			temp[0] = i;
			GLES20.glDeleteFramebuffers(1, temp, 0);
		}
		frameBuffers.clear();

	}

}
