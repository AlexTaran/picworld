/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.mesh.factories;

public class CubeMeshBuilder extends AbstractMeshBuilder {
	private CubeMeshBuilder() {
		
	}
	
	public static CubeMeshBuilder createCube(float centerX, float centerY, float centerZ) {
		CubeMeshBuilder builder = new CubeMeshBuilder();
		return builder;
	}
}
