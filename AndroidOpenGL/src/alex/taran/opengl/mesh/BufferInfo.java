/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.mesh;

public class BufferInfo {
	public final MeshMeta meshMeta;
	public final int bufferId;
	
	public BufferInfo(MeshMeta meshMeta, int bufferId) {
		this.meshMeta = meshMeta;
		this.bufferId = bufferId;
	}
}
