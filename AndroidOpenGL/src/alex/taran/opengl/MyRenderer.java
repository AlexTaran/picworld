package alex.taran.opengl;

import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import vladimir.losev.HUDDraggableElement;
import vladimir.losev.HUDElement;
import vladimir.losev.HUDSlotElement;
import vladimir.losev.SimpleHUD;
import alex.taran.opengl.model.Model;
import alex.taran.opengl.utils.GLBuffers;
import alex.taran.opengl.utils.ResourceUtils;
import alex.taran.opengl.utils.Shader;
import alex.taran.opengl.utils.TextureHolder;
import alex.taran.opengl.utils.buffers.BufferOffset;
import alex.taran.opengl.utils.buffers.VertexBufferHolder;
import alex.taran.picworld.GameField;
import alex.taran.picworld.World;
import alex.taran.utils.Matrix4;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;


public class MyRenderer implements Renderer {
	private int frameCounter = 0;
	private int lastFPS = 0;
	private long lastTimeFPSUpdated = -1;
	private long lastTimeWorldUpdated = 0;
	private float width, height;

	private TextureHolder textures;
	private VertexBufferHolder buffers;
	private Model robot;
	
	private World world;
	private SimpleHUD programmingUI;

	private Shader simpleShader;
	private Shader boxShader;
	private Shader objShader;
	private Shader buttonShader;
	private Shader skyboxShader;
	
	private ArrayList<HUDElement> hudElements;
	
	public float cameraPhi;
	public float cameraTheta;
	public float cameraRadius;
	
	public MyRenderer(World world, SimpleHUD programmingUI) {
		hudElements = new ArrayList<HUDElement>();
		//rand = new Random(SystemClock.elapsedRealtime());
		
		this.world = world;
		this.programmingUI = programmingUI;
		
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
		GLES20.glViewport(0, 0, (int)width, (int)height);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		GLES20.glViewport(0, 0, (int)(width * 0.75f), (int)height);
		
		float camposx = (float)(cameraRadius * Math.cos(cameraTheta) * Math.cos(cameraPhi));
		float camposy = (float)(cameraRadius * Math.sin(cameraTheta));
		float camposz = (float)(cameraRadius * Math.cos(cameraTheta) * Math.sin(cameraPhi));
		
		float camrightx = -(float) Math.sin(cameraPhi);
		float camrightz = (float) Math.cos(cameraPhi);
		
		float camupx = - camposy * camrightz;
		float camupy = - camposz * camrightx + camposx * camrightz;
		float camupz = + camposy * camrightx;
		
		Matrix4 mvMatrix = new Matrix4();
		Matrix4 viewMatrix = new Matrix4();
		Matrix4 modelMatrix = new Matrix4();
		Matrix4 perspectiveMatrix = new Matrix4();
		Matrix4 orthoMatrix = new Matrix4();
		
		perspectiveMatrix.setPerspectiveMatrix(60.0f, width * 0.75f / height, 0.1f, 50.0f);
		modelMatrix.setIdentity();
		mvMatrix.setProduction(viewMatrix, modelMatrix);
		
		// SKYBOX
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		skyboxShader.use();
		buffers.bind("cquad", GLES20.GL_ARRAY_BUFFER);
		viewMatrix.setLookAt(0.0f, 0.0f, 0.0f, -camposx, -camposy, -camposz, camupx, camupy, camupz);
		GLES20.glUniformMatrix4fv(skyboxShader.uniformLoc("view_matrix"), 1, false, viewMatrix.data, 0);
		GLES20.glUniformMatrix4fv(skyboxShader.uniformLoc("projection_matrix"), 1, false, perspectiveMatrix.data, 0);
		skyboxShader.enableVertexAttribArray("pos");
		skyboxShader.enableVertexAttribArray("tc");
		GLES20.glVertexAttribPointer(skyboxShader.attribLoc("pos"), 2, GLES20.GL_FLOAT, true, 0, 0);
		GLES20.glVertexAttribPointer(skyboxShader.attribLoc("tc"),  2, GLES20.GL_FLOAT, true, 0, 12 * 4);
		// NZ
		textures.bind("skybox_nz");
		modelMatrix.setIdentity().translate(0.0f, 0.0f, -0.5f).scale(1.0f, -1.0f, 1.0f);
		GLES20.glUniformMatrix4fv(skyboxShader.uniformLoc("model_matrix"), 1, false, modelMatrix.data, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		// PX
		textures.bind("skybox_px");
		modelMatrix.setIdentity().rotate(90.0f, 0.0f, -1.0f, 0.0f).translate(0.0f, 0.0f, -0.5f).scale(1.0f, -1.0f, 1.0f);
		GLES20.glUniformMatrix4fv(skyboxShader.uniformLoc("model_matrix"), 1, false, modelMatrix.data, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		// PZ
		textures.bind("skybox_pz");
		modelMatrix.setIdentity().rotate(180.0f, 0.0f, -1.0f, 0.0f).translate(0.0f, 0.0f, -0.5f).scale(1.0f, -1.0f, 1.0f);
		GLES20.glUniformMatrix4fv(skyboxShader.uniformLoc("model_matrix"), 1, false, modelMatrix.data, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		// NX
		textures.bind("skybox_nx");
		modelMatrix.setIdentity().rotate(90.0f, 0.0f, 1.0f, 0.0f).translate(0.0f, 0.0f, -0.5f).scale(1.0f, -1.0f, 1.0f);
		GLES20.glUniformMatrix4fv(skyboxShader.uniformLoc("model_matrix"), 1, false, modelMatrix.data, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		// NY
		textures.bind("skybox_ny");
		modelMatrix.setIdentity().rotate(90.0f,-1.0f, 0.0f,0.0f).rotate(90.0f, 0.0f, 0.0f,-1.0f).translate(0.0f, 0.0f, -0.5f).scale(1.0f, -1.0f, 1.0f);
		GLES20.glUniformMatrix4fv(skyboxShader.uniformLoc("model_matrix"), 1, false, modelMatrix.data, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		// PY
		textures.bind("skybox_py");
		modelMatrix.setIdentity().rotate(90.0f, 1.0f, 0.0f, 0.0f).translate(0.0f, 0.0f, -0.5f).scale(1.0f, -1.0f, 1.0f);
		GLES20.glUniformMatrix4fv(skyboxShader.uniformLoc("model_matrix"), 1, false, modelMatrix.data, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		
		textures.unbind();
		buffers.unBind(GLES20.GL_ARRAY_BUFFER);
		skyboxShader.unUse();
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		viewMatrix.setLookAt(camposx, camposy, camposz, 0.0f, 0.0f, 0.0f, camupx, camupy, camupz);
		
		// BASIS
		simpleShader.use();
		buffers.bind("basis", GLES20.GL_ARRAY_BUFFER);
		modelMatrix.setIdentity();
		mvMatrix.setProduction(viewMatrix, modelMatrix);
		GLES20.glUniformMatrix4fv(simpleShader.uniformLoc("modelview_matrix"), 1, false, mvMatrix.data, 0);
		GLES20.glUniformMatrix4fv(simpleShader.uniformLoc("projection_matrix"), 1, false, perspectiveMatrix.data, 0);
		simpleShader.enableVertexAttribArray("pos");
		simpleShader.enableVertexAttribArray("col");
		GLES20.glVertexAttribPointer(simpleShader.attribLoc("pos"), 3, GLES20.GL_FLOAT, true, 24, 0);
		GLES20.glVertexAttribPointer(simpleShader.attribLoc("col"), 3, GLES20.GL_FLOAT, true, 24, 12);
		GLES20.glDrawArrays(GLES20.GL_LINES, 0, 6);
		buffers.unBind(GLES20.GL_ARRAY_BUFFER);
		simpleShader.unUse();
		
		// FIELD
		boxShader.use();
		buffers.bind("halfbox", GLES20.GL_ARRAY_BUFFER);
		textures.bind("mix");
		boxShader.enableVertexAttribArray("pos");
		boxShader.enableVertexAttribArray("nrm");
		boxShader.enableVertexAttribArray("tc");
		GLES20.glUniform3f(boxShader.uniformLoc("cam_pos"), camposx, camposy, camposz);
		GLES20.glVertexAttribPointer(boxShader.attribLoc("pos"), 3, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("halfbox", "vertices").offset);
		GLES20.glVertexAttribPointer(boxShader.attribLoc("nrm"), 3, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("halfbox", "normals").offset);
		GLES20.glVertexAttribPointer(boxShader.attribLoc("tc"), 2, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("halfbox", "texcoords").offset);
		GLES20.glUniform1i(boxShader.attribLoc("decal"), 0);
		GLES20.glUniformMatrix4fv(boxShader.uniformLoc("projection_matrix"), 1, false, perspectiveMatrix.data, 0);
		
		GameField gameField = world.getGameField();
		for (int i = 0; i < gameField.getSizeX(); ++i) {
			for (int j = 0; j < gameField.getSizeZ(); ++j) {
				int height = gameField.getCellAt(i, j).getHeight();
				for (int k = 0; k <= height; ++k) {
					modelMatrix.setIdentity().translate(i - gameField.getSizeX() * 0.5f, k * 0.5f, j - gameField.getSizeZ() * 0.5f);
					mvMatrix.setProduction(viewMatrix, modelMatrix);
					GLES20.glUniformMatrix4fv(boxShader.uniformLoc("view_matrix"), 1, false, viewMatrix.data, 0);
					GLES20.glUniformMatrix4fv(boxShader.uniformLoc("model_matrix"), 1, false, modelMatrix.data, 0);
					GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6 * 6);
				}
			}
		}
		textures.unbind();
		buffers.unBind(GLES20.GL_ARRAY_BUFFER);
		boxShader.unUse();
		
		// ROBOT
		
		objShader.use();
		textures.bind("r2d2_png");
		objShader.enableVertexAttribArray("pos");
		objShader.enableVertexAttribArray("nrm");
		objShader.enableVertexAttribArray("tc");
		
		GLES20.glUniform1i(objShader.attribLoc("decal"), 0);
		GLES20.glUniform3f(objShader.uniformLoc("cam_pos"), camposx, camposy, camposz);
		GLES20.glUniformMatrix4fv(objShader.uniformLoc("projection_matrix"), 1, false, perspectiveMatrix.data, 0);
		GLES20.glUniformMatrix4fv(objShader.uniformLoc("view_matrix"), 1, false, viewMatrix.data, 0);
		modelMatrix.setIdentity().translate(0.0f, 0.8f, 0.0f);
		GLES20.glUniformMatrix4fv(objShader.uniformLoc("model_matrix"), 1, false, modelMatrix.data, 0);
		for (String s: robot.getGroupNames()) {
			buffers.bind("r2d2_obj::" + s, GLES20.GL_ARRAY_BUFFER);
			int basePos = 0;
			GLES20.glVertexAttribPointer(objShader.attribLoc("pos"), 3, GLES20.GL_FLOAT, true, 3 * 3 * 4, basePos);
			GLES20.glVertexAttribPointer(objShader.attribLoc("tc"),  3, GLES20.GL_FLOAT, true, 3 * 3 * 4, basePos + 3 * 4);
			GLES20.glVertexAttribPointer(objShader.attribLoc("nrm"), 3, GLES20.GL_FLOAT, true, 3 * 3 * 4, basePos + 2 * 3 * 4);
			BufferOffset ofs = buffers.getNamedOffset("r2d2_obj::" + s, "idx");
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, ofs.size);
		}
		
		textures.unbind();
		buffers.unBind(GLES20.GL_ARRAY_BUFFER);
		objShader.unUse();
		
		// UI
		GLES20.glViewport(0, 0, (int)width, (int)height);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
		buttonShader.use();
		orthoMatrix.setOrthoMatrix(0.0f, width, height, 0.0f, -1.0f, 1.0f);
		GLES20.glUniform1i(buttonShader.uniformLoc("decal"), 0);
		buttonShader.enableVertexAttribArray("pos");
		buttonShader.enableVertexAttribArray("tc");
		modelMatrix.setIdentity();
		GLES20.glUniformMatrix4fv(buttonShader.uniformLoc("projection_matrix"), 1, false, orthoMatrix.data, 0);
		GLES20.glUniformMatrix4fv(buttonShader.uniformLoc("view_matrix"), 1, false, modelMatrix.data, 0);
		buffers.bind("quad", GLES20.GL_ARRAY_BUFFER);
		
		programmingUI.getElements(hudElements);
		for (HUDElement e: hudElements) {
			if (e instanceof HUDDraggableElement) {
				HUDDraggableElement de = (HUDDraggableElement) e;
				
				switch (de.command) {
				case ROTATE_LEFT:  textures.bind("command_left");    break;
				case ROTATE_RIGHT: textures.bind("command_right");   break;
				case MOVE_FORWARD: textures.bind("command_forward"); break;
				case TOGGLE_LIGHT: textures.bind("command_light");   break;
				case CALL_A:       textures.bind("command_run_a");   break;
				case CALL_B:       textures.bind("command_run_b");   break;
				}
			} else if (e instanceof HUDSlotElement) {
				textures.bind("empty_slot");
			}
			
			GLES20.glVertexAttribPointer(buttonShader.attribLoc("pos"), 2, GLES20.GL_FLOAT, true, 0, 0);
			GLES20.glVertexAttribPointer(buttonShader.attribLoc("tc"),  2, GLES20.GL_FLOAT, true, 0, 0);
			modelMatrix.setIdentity().translate(e.left + e.width() * 0.1f, e.top + e.height() * 0.1f, 0.0f);
			//if (de.isInSlot) {
			//	Matrix.translateM(modelMatrix, 0, 0.0f, 0.0f, -0.1f);
			//}
			modelMatrix.scale(e.height() * 0.8f, e.width() * 0.8f, 1.0f);
			GLES20.glUniformMatrix4fv(buttonShader.uniformLoc("model_matrix"), 1, false, modelMatrix.data, 0);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		}
		buttonShader.unUse();
		
		long curr = SystemClock.elapsedRealtime();
		programmingUI.update(curr);
		
		// FPS
		if (lastTimeFPSUpdated < 0) {
			lastTimeFPSUpdated = curr;
			lastTimeWorldUpdated = curr;
			frameCounter = 0;
			lastFPS = 0;
		} else {
			frameCounter++;
			
			//float deltaTime = (curr - lastTimeWorldUpdated) / 1000.0f;
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
		programmingUI.init(w, h);
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
		Log.d("FUCK", "Initial memory usage: " + AndroidOpenGLActivity.getUsedMemorySize());
		textures = new TextureHolder();
		textures.load("icon_scaling", R.drawable.icon_scaling);
		textures.load("icon_select", R.drawable.icon_select);
		textures.load("icon_go", R.drawable.icon_go);
		textures.load("empty_slot", R.drawable.empty_slot);
		textures.load("command_forward", R.drawable.command_forward);
		textures.load("command_left", R.drawable.command_left);
		textures.load("command_right", R.drawable.command_right);
		textures.load("command_light", R.drawable.command_light);
		textures.load("command_run_a", R.drawable.command_run_a);
		textures.load("command_run_b", R.drawable.command_run_b);
		
		textures.load("skybox_px", R.drawable.fronthot, true);
		textures.load("skybox_nx", R.drawable.backhot, true);
		textures.load("skybox_pz", R.drawable.righthot, true);
		textures.load("skybox_nz", R.drawable.lefthot, true);
		textures.load("skybox_py", R.drawable.tophot, true);
		textures.load("skybox_ny", R.drawable.bothot, true);
		
		textures.load("bricks", R.drawable.bricks);
		textures.load("floor", R.drawable.floor);
		textures.load("mix", R.drawable.mix);
		System.gc();
		Log.d("FUCK", "Memory usage after loading textures: " + AndroidOpenGLActivity.getUsedMemorySize());
		
		simpleShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "simpleshader");
		boxShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "boxshader");
		objShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "objshader");
		buttonShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "buttonshader");
		skyboxShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "skyboxshader");
	
		buffers = new VertexBufferHolder();
		buffers.load("basis", new float[]{
		    	0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
		    	1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
		    	0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
		    	0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
		    	0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
		    	0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
		    });
		buffers.load("quad", GLBuffers.genQuadBuffer(1.0f, 1.0f));
		buffers.load("cquad", GLBuffers.genCenteredQuadBuffer(0.5f, 0.5f));
		buffers.load("halfbox", GLBuffers.genBoxBuffer(0.5f, 0.25f, 0.5f), GLBuffers.genBoxBufferNamedOffsets());
		
		textures.load("r2d2_png", R.drawable.r2d2_png);
		Log.d("FUCK", "Start loading R2D2");
		//r2d2 = new WavefrontObject("r2d2_obj");
		//robot = Model.loadFromAndroidObj(context, "r2d2_obj", 0.08f);
		//ResourceUtils.saveObjectToExternalStorage(context, robot, "robot.txt");
		
		robot = Model.loadSerializedFromStream(ResourceUtils.getInputStreamForRawResource(AndroidOpenGLActivity.getContext(), "robot"));
		robot.cleanup();
		for (String s: robot.getGroupNames()) {
			float[] vb = robot.getGroupVertexBuffer(s);
			buffers.load("r2d2_obj::" + s, vb, robot.genBufferOffsets(s));
		}
		robot.deleteData();
		Log.d("FUCK", "End loading R2D2.");
		Log.d("FUCK", "Memory usage after loading all: " + AndroidOpenGLActivity.getUsedMemorySize());
	}

	public int getFPS() {
		return lastFPS;
	}
}
