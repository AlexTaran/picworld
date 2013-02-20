package alex.taran.opengl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import android.graphics.PointF;

public class GLBuffers {

	public static FloatBuffer genBuffer(int numFloats) {
		return ByteBuffer.allocateDirect(numFloats * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	public static FloatBuffer genQuadBuffer(float w,float h) {
		FloatBuffer vbo = genBuffer(6 * 2);
		vbo.put(0,0.0f); vbo.put(1,0.0f);
		vbo.put(2,w); vbo.put(3,0.0f);
		vbo.put(4,w); vbo.put(5,h);
		vbo.put(6,0.0f); vbo.put(7,0.0f);
		vbo.put(8,w); vbo.put(9,h);
		vbo.put(10,0.0f); vbo.put(11,h);
		return vbo;
	}
	
	public static FloatBuffer genCenteredQuadBuffer(float halfW, float halfH) {
		FloatBuffer vbo = genBuffer(6 * 2 * 2);
		vbo.put(0,-halfW); vbo.put(1,-halfH);
		vbo.put(2,halfW); vbo.put(3,-halfH);
		vbo.put(4,halfW); vbo.put(5,halfH);
		vbo.put(6,-halfW); vbo.put(7,-halfH);
		vbo.put(8,halfW); vbo.put(9,halfH);
		vbo.put(10,-halfW); vbo.put(11,halfH);
		
		vbo.put(12,0.0f); vbo.put(13,0.0f);
		vbo.put(14,1.0f); vbo.put(15,0.0f);
		vbo.put(16,1.0f); vbo.put(17,1.0f);
		vbo.put(18,0.0f); vbo.put(19,0.0f);
		vbo.put(20,1.0f); vbo.put(21,1.0f);
		vbo.put(22,0.0f); vbo.put(23,1.0f);
		return vbo;
	}
	
	public static FloatBuffer genBuffer(float[] values) {
		FloatBuffer fb = genBuffer(values.length);
		for (int i = 0; i < values.length; ++i) {
			fb.put(i, values[i]);
		}
		fb.position(0);
		return fb;
	}
	
	public static FloatBuffer genQuadBuffer(PointF p) {
		return genQuadBuffer(p.x, p.y);
	}
	
	public static FloatBuffer genBoxBuffer(float szx, float szy,float szz) {
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
		return genBuffer(data);
	}
	
	public static Map<String, Integer> genBoxBufferNamedOffsets() {
		Map<String, Integer> offsets = new HashMap<String, Integer>();
		offsets.put("vertices", 0);
		offsets.put("texcoords", 3 * 4 * 36);
		offsets.put("normals", 3 * 4 * 36 + 2 * 4 * 36);
		return offsets;
	}
}
