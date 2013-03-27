/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.mesh;

public enum VertexAttributeType {
	POSITION(1),
	TEXCOORD(2),
	NORMAL(3),
	TANGENT(4),
	BINORMAL(5);
	
	private final int value;
	
	private VertexAttributeType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
