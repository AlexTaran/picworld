package alex.taran.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import alex.taran.opengl.model.Model;
import alex.taran.opengl.utils.GLBuffers;
import alex.taran.opengl.utils.MatrixUtils;
import alex.taran.opengl.utils.Shader;
import alex.taran.opengl.utils.TextureHolder;
import alex.taran.opengl.utils.VertexBufferHolder;
import alex.taran.picworld.GameField;
import alex.taran.picworld.World;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.obj.WavefrontObject;

public class MyRenderer implements Renderer {
	private int frameCounter = 0;
	private int lastFPS = 0;
	private long lastTimeFPSUpdated = -1;
	private long lastTimeWorldUpdated = 0;
	private float width, height;

	private TextureHolder textures;
	private VertexBufferHolder buffers;
	private Context context;
	private WavefrontObject r2d2;
	private Model robot;
	
	private World world;

	private Shader simpleShader;
	private Shader boxShader;
	private Shader objShader;
	
	public float cameraPhi;
	public float cameraTheta;
	public float cameraRadius;
	
	public MyRenderer(Context theContext, World world) {
		//rand = new Random(SystemClock.elapsedRealtime());

		context = theContext;
		
		this.world = world;
		
		cameraPhi = 0.0f;
		cameraTheta = 0.0f;
		cameraRadius = 5.0f;
	}

	private void countLookAtMatrix(float[] m) {
		float posx = (float)(cameraRadius * Math.cos(cameraTheta) * Math.cos(cameraPhi));
		float posy = (float)(cameraRadius * Math.sin(cameraTheta));
		float posz = (float)(cameraRadius * Math.cos(cameraTheta) * Math.sin(cameraPhi));
		Matrix.setLookAtM(m, 0, posx, posy, posz, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
	}
	
	@Override
	public void onDrawFrame(GL10 unused) {
		GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		float camposx = (float)(cameraRadius * Math.cos(cameraTheta) * Math.cos(cameraPhi));
		float camposy = (float)(cameraRadius * Math.sin(cameraTheta));
		float camposz = (float)(cameraRadius * Math.cos(cameraTheta) * Math.sin(cameraPhi));
		
		float mvMatrix[] = new float[16];
		float viewMatrix[] = new float[16];
		float modelMatrix[] = new float[16];
		float perspectiveMatrix[] = new float[16];
		countLookAtMatrix(viewMatrix);
		MatrixUtils.perspectiveMatrix(perspectiveMatrix, 60.0f, width/height, 0.1f, 50.0f);
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		
		// BASIS
		simpleShader.use();
		buffers.bind("basis", GLES20.GL_ARRAY_BUFFER);
		GLES20.glUniformMatrix4fv(simpleShader.uniformLoc("modelview_matrix"), 1, false, mvMatrix, 0);
		GLES20.glUniformMatrix4fv(simpleShader.uniformLoc("projection_matrix"), 1, false, perspectiveMatrix, 0);
		simpleShader.enableVertexAttribArray("pos");
		simpleShader.enableVertexAttribArray("col");
		GLES20.glVertexAttribPointer(simpleShader.attribLoc("pos"), 3, GLES20.GL_FLOAT, true, 24, 0);
		GLES20.glVertexAttribPointer(simpleShader.attribLoc("col"), 3, GLES20.GL_FLOAT, true, 24, 12);
		//GLES20.glDrawArrays(GLES20.GL_LINES, 0, 6);
		buffers.unBind(GLES20.GL_ARRAY_BUFFER);
		simpleShader.unUse();
		
		boxShader.use();
		buffers.bind("halfbox", GLES20.GL_ARRAY_BUFFER);
		textures.bind("mix");
		boxShader.enableVertexAttribArray("pos");
		boxShader.enableVertexAttribArray("nrm");
		boxShader.enableVertexAttribArray("tc");
		GLES20.glUniform3f(boxShader.uniformLoc("cam_pos"), camposx, camposy, camposz);
		GLES20.glVertexAttribPointer(boxShader.attribLoc("pos"), 3, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("halfbox", "vertices"));
		GLES20.glVertexAttribPointer(boxShader.attribLoc("nrm"), 3, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("halfbox", "normals"));
		GLES20.glVertexAttribPointer(boxShader.attribLoc("tc"), 2, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("halfbox", "texcoords"));
		GLES20.glUniform1i(boxShader.attribLoc("decal"), 0);
		GLES20.glUniformMatrix4fv(boxShader.uniformLoc("projection_matrix"), 1, false, perspectiveMatrix, 0);
		
		GameField gameField = world.getGameField();
		for (int i = 0; i < gameField.getSizeX(); ++i) {
			for (int j = 0; j < gameField.getSizeZ(); ++j) {
				int height = gameField.getCellAt(i, j).getHeight();
				for (int k = 0; k <= height; ++k) {
					Matrix.setIdentityM(modelMatrix, 0);
					Matrix.translateM(modelMatrix, 0, i - gameField.getSizeX() * 0.5f, k * 0.5f, j - gameField.getSizeZ() * 0.5f);
					Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);
					GLES20.glUniformMatrix4fv(boxShader.uniformLoc("view_matrix"), 1, false, viewMatrix, 0);
					GLES20.glUniformMatrix4fv(boxShader.uniformLoc("model_matrix"), 1, false, modelMatrix, 0);
					//GLES20.glUniformMatrix3fv(boxShader.uniformLoc("normal_matrix"), 1, false, MatrixUtils.calcNormalMatrix(mvMatrix), 0);
					GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6 * 6);
				}
			}
		}
		textures.unbind();
		buffers.unBind(GLES20.GL_ARRAY_BUFFER);
		boxShader.unUse();
		
		objShader.use();
		buffers.bind("r2d2_obj", GLES20.GL_ARRAY_BUFFER);
		textures.bind("r2d2_png");
		objShader.enableVertexAttribArray("pos");
		objShader.enableVertexAttribArray("nrm");
		objShader.enableVertexAttribArray("tc");
		
		GLES20.glUniform1i(objShader.attribLoc("decal"), 0);
		GLES20.glUniform3f(objShader.uniformLoc("cam_pos"), camposx, camposy, camposz);
		GLES20.glUniformMatrix4fv(objShader.uniformLoc("projection_matrix"), 1, false, perspectiveMatrix, 0);
		GLES20.glUniformMatrix4fv(objShader.uniformLoc("view_matrix"), 1, false, viewMatrix, 0);
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, 0.0f, 0.8f, 0.0f);
		GLES20.glUniformMatrix4fv(objShader.uniformLoc("model_matrix"), 1, false, modelMatrix, 0);
		for (String s: robot.getGroupNames()) {
			int basePos = buffers.getNamedOffset("r2d2_obj", s);
			GLES20.glVertexAttribPointer(objShader.attribLoc("pos"), 3, GLES20.GL_FLOAT, true, 3 * 3 * 4, basePos);
			GLES20.glVertexAttribPointer(objShader.attribLoc("tc"),  3, GLES20.GL_FLOAT, true, 3 * 3 * 4, basePos + 3 * 4);
			GLES20.glVertexAttribPointer(objShader.attribLoc("nrm"), 3, GLES20.GL_FLOAT, true, 3 * 3 * 4, basePos + 2 * 3 * 4);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, robot.getGroupSize(s) / 9);
		}
		
		/*GameField gameField = world.getGameField();
		for (int i = 0; i < gameField.getSizeX(); ++i) {
			for (int j = 0; j < gameField.getSizeZ(); ++j) {
				int height = gameField.getCellAt(i, j).getHeight();
				for (int k = 0; k <= height; ++k) {
					Matrix.setIdentityM(modelMatrix, 0);
					Matrix.translateM(modelMatrix, 0, i - gameField.getSizeX() * 0.5f, k * 0.5f, j - gameField.getSizeZ() * 0.5f);
					Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);
					GLES20.glUniformMatrix4fv(boxShader.uniformLoc("view_matrix"), 1, false, viewMatrix, 0);
					GLES20.glUniformMatrix4fv(boxShader.uniformLoc("model_matrix"), 1, false, modelMatrix, 0);
					//GLES20.glUniformMatrix3fv(boxShader.uniformLoc("normal_matrix"), 1, false, MatrixUtils.calcNormalMatrix(mvMatrix), 0);
					GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6 * 6);
				}
			}
		}*/
		textures.unbind();
		buffers.unBind(GLES20.GL_ARRAY_BUFFER);
		objShader.unUse();
		// FPS
		if (lastTimeFPSUpdated < 0) {
			lastTimeFPSUpdated = SystemClock.elapsedRealtime();
			lastTimeWorldUpdated = SystemClock.elapsedRealtime();
			frameCounter = 0;
			lastFPS = 0;
		} else {
			frameCounter++;
			long curr = SystemClock.elapsedRealtime();
			float deltaTime = (curr - lastTimeWorldUpdated) / 1000.0f;
			//gameWorld.update(deltaTime);
			lastTimeWorldUpdated = curr;
			if (curr - lastTimeFPSUpdated >= 1000) {
				lastFPS = (int) (frameCounter * 1000.0f / (curr - lastTimeFPSUpdated));
				frameCounter = 0;
				lastTimeFPSUpdated = curr;
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		width = w;
		height = h;
		GLES20.glViewport(0, 0, w, h);
		//gl.glMatrixMode(GLES20.GL_PROJECTION);
		//gl.glLoadIdentity();
		//gl.glOrthof(0.0f, 1.0f, 1.0f, 0.0f, 0.5f, -0.5f);
		//gl.glScalef(1.0f / width, 1.0f / height, 1.0f);
		// GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
		// 100.0f);
		//gl.glMatrixMode(GL10.GL_MODELVIEW);
		//gl.glLoadIdentity();
		//controlState.surfaceSizes.x = width;
		//controlState.surfaceSizes.y = height;
		//controlState.scale = Math.max((height - controlState.countButtonSize())
		//		/ gameWorld.gameMap.sizes.y, width / gameWorld.gameMap.sizes.x);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		//GLES20.glSglShadeModel(GL10.GL_SMOOTH);
		GLES20.glClearDepthf(1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		//GLES20.glHint(GLES20.GL_PGL_PERSPECTIVE_CORRECTION_HINT, GLES20.GL_NICEST);
		//GLES20.glPointSize(16.0f);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);

		textures = new TextureHolder(context);
		textures.load("icon_scaling", R.drawable.icon_scaling);
		textures.load("icon_select", R.drawable.icon_select);
		textures.load("icon_go", R.drawable.icon_go);
		
		Log.d("FUCK", "Start loading textures");
		textures.load("bricks", R.drawable.bricks);
		textures.load("floor", R.drawable.floor);
		textures.load("mix", R.drawable.mix);
		Log.d("FUCK", "End loading textures");
		
		simpleShader = Shader.loadFromResource(context, "simpleshader");
		boxShader = Shader.loadFromResource(context, "boxshader");
		objShader = Shader.loadFromResource(context, "objshader");
	
		buffers = new VertexBufferHolder(context);
		buffers.load("basis", GLBuffers.genBuffer(new float[]{
		    	0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
		    	1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
		    	0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
		    	0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
		    	0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
		    	0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
		    }));
		buffers.load("halfbox", GLBuffers.genBoxBuffer(0.5f, 0.25f, 0.5f), GLBuffers.genBoxBufferNamedOffsets());
		
		textures.load("r2d2_png", R.drawable.r2d2_png);
		Log.d("FUCK", "Start loading R2D2");
		//r2d2 = new WavefrontObject("r2d2_obj");
		robot = Model.createFromObj(context, "r2d2_obj", 0.08f);
		buffers.load("r2d2_obj",GLBuffers.genBuffer(robot.genVertexBuffer()), robot.genNamedOffsetForVertexBuffer());
		//Log.d("FUCK", "End loading R2D2. Groups num = " + robot.getGroups().size() + " v = " + .getVertices().size());
		Log.d("FUCK", "End loading R2D2.");
	}

	public int getFPS() {
		return lastFPS;
	}
}
