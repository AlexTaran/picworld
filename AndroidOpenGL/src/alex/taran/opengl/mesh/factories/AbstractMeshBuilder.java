/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.mesh.factories;

import alex.taran.opengl.mesh.Mesh;

public abstract class AbstractMeshBuilder {
	protected final Mesh mesh = new Mesh();
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public AbstractMeshBuilder generateTangentsAndBinormals() {
		if(!mesh.validate()) {
			throw new RuntimeException("Cannot generate Tangents and Binormals because mesh is not valid");
		}
		throw new RuntimeException("Method is not implemented yet");
		// return this;
	}
}
