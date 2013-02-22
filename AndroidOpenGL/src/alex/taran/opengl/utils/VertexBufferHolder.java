package alex.taran.opengl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class VertexBufferHolder extends Holder {
	private int[] temp = new int[1]; // for temporary ID
	Map<String, BufferInfo> buffers = new HashMap<String, BufferInfo>();

	public VertexBufferHolder(Context theContext) {
		super(theContext);
	}
	
	private static FloatBuffer genBuffer(int numFloats) {
		return ByteBuffer.allocateDirect(numFloats * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
	
	private static FloatBuffer genBuffer(float[] values) {
		FloatBuffer fb = genBuffer(values.length);
		for (int i = 0; i < values.length; ++i) {
			fb.put(i, values[i]);
		}
		fb.position(0);
		return fb;
	}

	public void load(String name, float[] buf) {
		delete(name);
		GLES20.glGenBuffers(1, temp, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, temp[0]);
		FloatBuffer fb = genBuffer(buf);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, fb.capacity() * 4, fb,
				GLES20.GL_STATIC_DRAW);
		fb.clear();
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		buffers.put(name, new BufferInfo(temp[0]));
	}

	public void load(String name, float[] buf, Map<String, Integer> offsets) {
		load(name,buf);
		BufferInfo bi = buffers.get(name);
		bi.namedOffsets.putAll(offsets);
	}

	public void bind(String name, int target) {
		BufferInfo bi = buffers.get(name);
		if (bi != null) {
			GLES20.glBindBuffer(target, bi.id);
		}else{
			Log.e("OpenGL","Cannot find vertex buffer: "+name);
		}
	}

	public int getNamedOffset(String buf, String ofs) { // get named offset
		BufferInfo bi = buffers.get(buf);
		if (bi != null) {
			Integer i = bi.namedOffsets.get(ofs);
			if (i != null) {
				return i;
			} else {
				Log.e("OpenGL","Cannot find named offset in vertex buffer: "+ofs);
				return 0;
			}
		} else {
			Log.e("OpenGL","Cannot find vertex buffer: "+buf);
			return 0;
		}
	}

	public void setNamedOffset(String buf, String ofs, int value) {
		BufferInfo bi = buffers.get(buf);
		if (bi != null) {
			bi.namedOffsets.put(ofs, value);
		}
	}

	public void removeNamedOffset(String buf, String ofs) {
		BufferInfo bi = buffers.get(buf);
		if (bi != null) {
			bi.namedOffsets.remove(ofs);
		}
	}

	public static void unBind(int target) {
		GLES20.glBindBuffer(target, 0);
	}

	public void delete(String s) {
		BufferInfo bi = buffers.get(s);
		if (bi != null) {
			temp[0] = bi.id;
			GLES20.glDeleteBuffers(1, temp, 0);
			buffers.remove(s);
		}
	}

	@Override
	public void clear() {
		for (BufferInfo bi : buffers.values()) {
			temp[0] = bi.id;
			GLES20.glDeleteBuffers(1, temp, 0);
		}
		buffers.clear();
	}

	private class BufferInfo {
		public int id;
		Map<String, Integer> namedOffsets = new HashMap<String, Integer>();

		public BufferInfo(int theId) {
			id = theId;
		}
	}
}
