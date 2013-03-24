/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.utils.buffers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import alex.taran.opengl.utils.Holder;
import android.opengl.GLES20;
import android.util.Log;

public class VertexBufferHolder extends Holder {
	private final int BUFFER_CAPACITY = 20000; // in floats. It's size is 80kb = 20K x 4
	private final FloatBuffer buffer;
	
	private int[] temp = new int[1]; // for temporary ID
	Map<String, BufferInfo> buffers = new HashMap<String, BufferInfo>();
	
	public VertexBufferHolder() {
		buffer = genBuffer(BUFFER_CAPACITY);
	}
	
	private static FloatBuffer genBuffer(int numFloats) {
		Log.d("FUCK", "AllocateDirect: " + numFloats * 4 + " bytes");
		return ByteBuffer.allocateDirect(numFloats * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
	
	private static FloatBuffer genBuffer(float[] values) {
		return genBuffer(values, 0, values.length);
	}
	
	private static FloatBuffer genBuffer(float[] values, int start, int size) {
		FloatBuffer fb = genBuffer(size);
		for (int i = 0; i < size; ++i) {
			fb.put(i, values[start + i]);
		}
		fb.position(0);
		return fb;
	}
	
	private void loadToBuffer(float[] values, int start, int size) {
		buffer.clear();
		for (int i = 0; i < size; ++i) {
			buffer.put(values[start + i]);
		}
		buffer.position(0);
		buffer.limit(size);
	}

	public void load(String name, float[] buf) {
		delete(name);
		GLES20.glGenBuffers(1, temp, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, temp[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buf.length * 4, null, GLES20.GL_STATIC_DRAW);
		int start = 0;
		while (true) {
			int sz = Math.min(buf.length - start, 20000);
			if (sz == 0) {
				break;
			}
			//FloatBuffer fb = genBuffer(buf, start, sz);
			/*GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, fb.capacity() * 4, fb,
					GLES20.GL_STATIC_DRAW);*/
			//GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, start * 4, sz * 4, fb);
			loadToBuffer(buf, start, sz);
			GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, start * 4, sz * 4, buffer);
			start += sz;
		}
		
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		buffers.put(name, new BufferInfo(temp[0]));
	}

	public void load(String name, float[] buf, Map<String, BufferOffset> offsets) {
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

	public BufferOffset getNamedOffset(String buf, String ofs) { // get named offset
		BufferInfo bi = buffers.get(buf);
		if (bi != null) {
			BufferOffset b = bi.namedOffsets.get(ofs);
			if (b != null) {
				return b;
			} else {
				Log.e("OpenGL","Cannot find named offset in vertex buffer: "+ofs);
				return null;
			}
		} else {
			Log.e("OpenGL","Cannot find vertex buffer: "+buf);
			return null;
		}
	}

	public void setNamedOffset(String bufName, String ofsName, int offset, int size) {
		BufferInfo bi = buffers.get(bufName);
		if (bi != null) {
			bi.namedOffsets.put(ofsName, new BufferOffset(offset,size));
		}
	}

	public void removeNamedOffset(String bufName, String ofsName) {
		BufferInfo bi = buffers.get(bufName);
		if (bi != null) {
			bi.namedOffsets.remove(ofsName);
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
		public Map<String, BufferOffset> namedOffsets = new HashMap<String, BufferOffset>();

		public BufferInfo(int theId) {
			id = theId;
		}
	}
}
