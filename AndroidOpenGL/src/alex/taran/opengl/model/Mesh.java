/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alex.taran.opengl.model.VertexAttribute.AttributeType;

public class Mesh {
	private final Map<AttributeType, VertexAttribute> attributes = new HashMap<VertexAttribute.AttributeType, VertexAttribute>();
	private final List<Integer> indexes = new ArrayList<Integer>();
	
	public int countVertexSizeInBytes(){
		int size = 0;
		for (VertexAttribute attr: attributes.values()) {
			size += attr.getAttributeSize();
		}
		return size;
	}
	
	public float[] generateBuffer() {
		//float[] buffer = new float[10];
		List<Float> buffer = new ArrayList<Float>();
		for (AttributeType type: attributes.keySet()) {
			//attributes.get(type).
		}
		return null;
	}
	
	public Map<AttributeType, VertexAttribute> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}
}
