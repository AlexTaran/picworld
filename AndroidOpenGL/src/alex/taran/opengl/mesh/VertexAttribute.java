/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.mesh;

public class VertexAttribute {
	private final VertexAttributeType attributeType;
	private final int attributeSize;
	
	public VertexAttribute(VertexAttributeType attributeType, int attributeSize) {
		this.attributeType = attributeType;
		this.attributeSize = attributeSize;
	}
	
	public VertexAttributeType type() {
		return attributeType;
	}
	
	public int size() {
		return attributeSize;
	}
	
	@Override
	public int hashCode() {
		return attributeType.getValue();
	}
}
