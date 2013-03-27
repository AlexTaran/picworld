/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.opengl.GLES20;

public class Mesh {
	public final Map<VertexAttribute, List<Float>> data = new HashMap<VertexAttribute, List<Float>>();
	public int primitiveType;
	
	public boolean validate() {
		int primitiveSize = getPrimitiveSize();
		if (primitiveSize == 0) {
			return false;
		}
		if (data.size() == 0) {
			return false;
		}
		int primitiveCount = -1;
		
		for (Entry<VertexAttribute, List<Float>> e: data.entrySet()) {
			if (e.getValue().size() % (primitiveSize * e.getKey().size()) != 0) {
				return false;
			} else if (primitiveCount < 0) { // calculate first time by any attribute
				primitiveCount = e.getValue().size() / (primitiveSize * e.getKey().size());
			} else { // compare with already calculatedPrimitiveCount
				if (e.getValue().size() / (primitiveSize * e.getKey().size()) != primitiveCount) {
					return false;
				}
			}
		}
		return true;
	}
	
	public List<Float> generateVertexBuffer() {
		if (!validate()) {
			throw new RuntimeException("Bad Mesh");
		}
		List<Float> buffer = new ArrayList<Float>();
		for (Entry<VertexAttribute, List<Float>> e: data.entrySet()) {
			buffer.addAll(e.getValue());
		}
		return buffer;
	}
	
	public MeshMeta generateMeshMeta() {
		if (!validate()) {
			throw new RuntimeException("Bad Mesh");
		}
		int primitiveSize = getPrimitiveSize();
		Entry<VertexAttribute, List<Float>> anAttribute = data.entrySet().iterator().next();
		int primitiveCount = anAttribute.getValue().size() / (primitiveSize * anAttribute.getKey().size());
		
		MeshMeta meshMeta = new MeshMeta(primitiveCount * primitiveSize, primitiveType);
		int offset = 0;
		for (Entry<VertexAttribute, List<Float>> e: data.entrySet()) {
			VertexAttributeParams params = new VertexAttributeParams(e.getKey().size(), offset);
			meshMeta.attributes.put(e.getKey().type(), params);
			// Size of a "float" * number of floats
			offset += 4 * e.getValue().size();
		}
		return meshMeta;
	}
	
	private int getPrimitiveSize() {
		switch(primitiveType) {
		case GLES20.GL_POINTS:    return 1;
		case GLES20.GL_LINES:     return 2;
		case GLES20.GL_TRIANGLES: return 3;
		default: return 0;
		}
	}
}
