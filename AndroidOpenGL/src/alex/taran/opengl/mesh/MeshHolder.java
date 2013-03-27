/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.mesh;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import alex.taran.opengl.utils.Holder;
import alex.taran.opengl.mesh.BufferInfo;
import android.opengl.GLES20;
import android.util.Log;

public class MeshHolder extends Holder {
	private final int BUFFER_CAPACITY = 20000; // in floats. It's size is 80kb = 20K x 4
	private final FloatBuffer buffer;
	
	private int[] temp = new int[1]; // for temporary ID
	Map<String, BufferInfo> buffers = new HashMap<String, BufferInfo>();
	
	public MeshHolder() {
		buffer = genBuffer(BUFFER_CAPACITY);
	}
	
	private static FloatBuffer genBuffer(int numFloats) {
		Log.d("FUCK", "AllocateDirect: " + numFloats * 4 + " bytes");
		return ByteBuffer.allocateDirect(numFloats * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
	
	private void loadToBuffer(float[] values, int start, int size) {
		buffer.clear();
		for (int i = 0; i < size; ++i) {
			buffer.put(values[start + i]);
		}
		buffer.position(0);
		buffer.limit(size);
	}
	
	public void load(String name, Mesh mesh) {
		delete(name);
		List<Float> bufferData = mesh.generateVertexBuffer();
		float[] buf = new float[bufferData.size()];
		for (int i = 0; i < bufferData.size(); ++i) {
			buf[i] = bufferData.get(i);
		}
		MeshMeta meshMeta = mesh.generateMeshMeta();
		
		GLES20.glGenBuffers(1, temp, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, temp[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bufferData.size() * 4, null, GLES20.GL_STATIC_DRAW);
		int start = 0;
		while (true) {
			int sz = Math.min(buf.length - start, 20000);
			if (sz == 0) {
				break;
			}
			loadToBuffer(buf, start, sz);
			GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, start * 4, sz * 4, buffer);
			start += sz;
		}
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		buffers.put(name, new BufferInfo(meshMeta, temp[0]));
	}
	
	public void bindBuffer(String name, int target) {
		BufferInfo bi = buffers.get(name);
		if (bi != null) {
			GLES20.glBindBuffer(target, bi.bufferId);
		}else{
			Log.e("OpenGL","Cannot find vertex buffer: "+name);
		}
	}
	
	public static void unBindBuffer(int target) {
		GLES20.glBindBuffer(target, 0);
	}
	
	public int getAttribSize(String name, VertexAttributeType attribType) {
		return ensureAttribParamsForMesh(name, attribType).size;
	}
	
	public int getAttribOffset(String name, VertexAttributeType attribType) {
		return ensureAttribParamsForMesh(name, attribType).offset;
	}
	
	private VertexAttributeParams ensureAttribParamsForMesh(String name, VertexAttributeType attribType) {
		BufferInfo bi = buffers.get(name);
		if (bi != null) {
			VertexAttributeParams attribParams = bi.meshMeta.attributes.get(attribType);
			if (attribParams != null) {
				return attribParams;
			} else {
				throw new RuntimeException("MeshHolder: no such vertex attribute "+ attribType.toString() +
						" in mesh '" + name + "'");
			}
		} else {
			throw new RuntimeException("MeshHolder: no such buffer with name'" + name + "'");
		}
	}
	
	public void delete(String s) {
		BufferInfo bi = buffers.get(s);
		if (bi != null) {
			temp[0] = bi.bufferId;
			GLES20.glDeleteBuffers(1, temp, 0);
			buffers.remove(s);
		}
	}

	@Override
	public void clear() {
		for (Entry<String, BufferInfo> e: buffers.entrySet()) {
			temp[0] = e.getValue().bufferId;
			GLES20.glDeleteBuffers(1, temp, 0);
		}
		buffers.clear();
	}
	
	@Override
	public void finalize() {
		clear();
	}

}
