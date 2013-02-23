package alex.taran.opengl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import alex.taran.opengl.utils.buffers.BufferOffset;
import android.graphics.PointF;

public class GLBuffers {

	

	public static float[] genQuadBuffer(float w,float h) {
		//FloatBuffer vbo = genBuffer(6 * 2);
		float[] vbo = {
			0.0f, 0.0f,
			w, 0.0f,
			w, h,
			0.0f, 0.0f,
			w, h,
			0.0f, h,
		};
		return vbo;
	}
	
	public static float[] genCenteredQuadBuffer(float halfW, float halfH) {
		//FloatBuffer vbo = genBuffer(6 * 2 * 2);
		float[] vbo = {
			-halfW, -halfH,
			halfW, -halfH,
			halfW, halfH,
			-halfW, -halfH,
			halfW, halfH,
			-halfW, halfH,
			0.0f,0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 1.0f,
		};
		return vbo;
	}
	
	public static float[] genQuadBuffer(PointF p) {
		return genQuadBuffer(p.x, p.y);
	}
	
	public static float[] genBoxBuffer(float szx, float szy,float szz) {
		float data[] = {
			-szx, -szy, -szz,
			 szx, -szy, -szz,
			 szx,  szy, -szz,
			-szx, -szy, -szz,
			 szx,  szy, -szz,
			-szx,  szy, -szz,
			
			-szx, -szy,  szz,
			 szx, -szy,  szz,
			 szx,  szy,  szz,
			-szx, -szy,  szz,
			 szx,  szy,  szz,
			-szx,  szy,  szz,
			
			-szx, -szy, -szz,
			 szx, -szy, -szz,
			 szx, -szy,  szz,
			-szx, -szy, -szz,
			 szx, -szy,  szz,
			-szx, -szy,  szz,
			
			-szx,  szy, -szz,
			 szx,  szy, -szz,
			 szx,  szy,  szz,
			-szx,  szy, -szz,
			 szx,  szy,  szz,
			-szx,  szy,  szz,
			
			-szx, -szy, -szz,
			-szx,  szy, -szz,
			-szx,  szy,  szz,
			-szx, -szy, -szz,
			-szx,  szy,  szz,
			-szx, -szy,  szz,
			
			 szx, -szy, -szz,
			 szx,  szy, -szz,
			 szx,  szy,  szz,
			 szx, -szy, -szz,
			 szx,  szy,  szz,
			 szx, -szy,  szz,
			
			0.5f, 0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
			0.5f, 0.0f,
			1.0f, 1.0f,
			0.5f, 1.0f,
				
			0.5f, 0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
			0.5f, 0.0f,
			1.0f, 1.0f,
			0.5f, 1.0f,
			
			0.0f, 0.0f,
			0.5f, 0.0f,
			0.5f, 1.0f,
			0.0f, 0.0f,
			0.5f, 1.0f,
			0.0f, 1.0f,
			
			0.0f, 0.0f,
			0.5f, 0.0f,
			0.5f, 1.0f,
			0.0f, 0.0f,
			0.5f, 1.0f,
			0.0f, 1.0f,
			
			0.5f, 0.0f,
			0.5f, 1.0f,
			1.0f, 1.0f,
			
			0.5f, 0.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,
				
			0.5f, 0.0f,
			0.5f, 1.0f,
			1.0f, 1.0f,
			
			0.5f, 0.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,
			
			0.0f, 0.0f,-1.0f,
			0.0f, 0.0f,-1.0f,
			0.0f, 0.0f,-1.0f,
			0.0f, 0.0f,-1.0f,
			0.0f, 0.0f,-1.0f,
			0.0f, 0.0f,-1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			
			0.0f,-1.0f, 0.0f,
			0.0f,-1.0f, 0.0f,
			0.0f,-1.0f, 0.0f,
			0.0f,-1.0f, 0.0f,
			0.0f,-1.0f, 0.0f,
			0.0f,-1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			
			
			-1.0f,0.0f, 0.0f,
			-1.0f,0.0f, 0.0f,
			-1.0f,0.0f, 0.0f,
			-1.0f,0.0f, 0.0f,
			-1.0f,0.0f, 0.0f,
			-1.0f,0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
		};
		return data;
	}
	
	public static Map<String, BufferOffset> genBoxBufferNamedOffsets() {
		Map<String, BufferOffset> offsets = new HashMap<String, BufferOffset>();
		offsets.put("vertices", new BufferOffset(0, 3 * 4 * 36));
		offsets.put("texcoords", new BufferOffset(3 * 4 * 36, 2 * 4 * 36));
		offsets.put("normals", new BufferOffset(3 * 4 * 36 + 2 * 4 * 36, 3 * 4 * 36));
		return offsets;
	}
}
