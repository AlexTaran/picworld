/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.mesh;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.opengl.GLES20;

public class MeshMeta {
	public final Map<VertexAttributeType, VertexAttributeParams> attributes = new HashMap<VertexAttributeType, VertexAttributeParams>();
	public final int vertexCount;
	public final int primitiveType;
	
	public MeshMeta(int vertexCount, int primitiveType) {
		this.vertexCount = vertexCount;
		this.primitiveType = primitiveType;
	}
	
	public void callDrawArrays() {
		GLES20.glDrawArrays(primitiveType, 0, vertexCount);
	}
}
