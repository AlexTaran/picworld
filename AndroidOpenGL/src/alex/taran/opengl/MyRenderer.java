package alex.taran.opengl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import vladimir.losev.HUDDraggableElement;
import vladimir.losev.HUDElement;
import vladimir.losev.HUDSlotElement;
import vladimir.losev.SimpleHUD;
import alex.taran.opengl.model.Model;
import alex.taran.opengl.particles.FountainParticle;
import alex.taran.opengl.particles.FountainParticleSystem;
import alex.taran.opengl.utils.FrameBufferHolder;
import alex.taran.opengl.utils.GLBuffers;
import alex.taran.opengl.utils.ResourceUtils;
import alex.taran.opengl.utils.Shader;
import alex.taran.opengl.utils.TextureHolder;
import alex.taran.opengl.utils.buffers.BufferOffset;
import alex.taran.opengl.utils.buffers.VertexBufferHolder;
import alex.taran.picworld.Command;
import alex.taran.picworld.GameField;
import alex.taran.picworld.GameField.CellLightState;
import alex.taran.picworld.Program;
import alex.taran.picworld.Robot.ImmutableRobot;
import alex.taran.picworld.World;
import alex.taran.picworld.World.WorldState;
import alex.taran.utils.Matrix4;
import alex.taran.utils.Vector3;
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
	private long lastTimeWorldUpdated = -1;
	private int width, height;

	private TextureHolder textures;
	private FrameBufferHolder frameBuffers;
	private VertexBufferHolder buffers;
	private Model robot;
	
	private World world;
	private SimpleHUD programmingUI;

	private Shader simpleShader;
	private Shader boxShader;
	private Shader objShader;
	private Shader lightFloorShader;
	private Shader buttonShader;
	private Shader skyboxShader;
	private Shader textureShader;
	private Shader smokeShader;
	
	private ArrayList<HUDElement> hudElements;
	private FountainParticleSystem fountainParticleSystem;
	
	public float cameraPhi;
	public float cameraTheta;
	public float cameraRadius;
	public float cameraX, cameraY, cameraZ;
	
	private float robotDeltaY;
	
	public MyRenderer(World world, SimpleHUD programmingUI) {
		hudElements = new ArrayList<HUDElement>();
		//rand = new Random(SystemClock.elapsedRealtime());
		
		this.world = world;
		this.programmingUI = programmingUI;
		
		cameraPhi = 0.0f;
		cameraTheta = 0.0f;
		cameraRadius = 5.0f;
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
		skyboxShader.enableVertexAttribArrays("pos", "tc");
		GLES20.glVertexAttribPointer(skyboxShader.attribLoc("pos"), 2, GLES20.GL_FLOAT, true, 0, 0);
		GLES20.glVertexAttribPointer(skyboxShader.attribLoc("tc"),  2, GLES20.GL_FLOAT, true, 0, 12 * 4);
		// NZ
		textures.bind("skybox_nz");
		modelMatrix.setIdentity().translate(0.0f, 0.0f, -0.5f).scale(1.0f, -1.0f, 1.0f);
		skyboxShader.setUniformMatrix4f("model_matrix", modelMatrix);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		// PX
		textures.bind("skybox_px");
		modelMatrix.setIdentity().rotate(90.0f, 0.0f, -1.0f, 0.0f).translate(0.0f, 0.0f, -0.5f).scale(1.0f, -1.0f, 1.0f);
		skyboxShader.setUniformMatrix4f("model_matrix", modelMatrix);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		// PZ
		textures.bind("skybox_pz");
		modelMatrix.setIdentity().rotate(180.0f, 0.0f, -1.0f, 0.0f).translate(0.0f, 0.0f, -0.5f).scale(1.0f, -1.0f, 1.0f);
		skyboxShader.setUniformMatrix4f("model_matrix", modelMatrix);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		// NX
		textures.bind("skybox_nx");
		modelMatrix.setIdentity().rotate(90.0f, 0.0f, 1.0f, 0.0f).translate(0.0f, 0.0f, -0.5f).scale(1.0f, -1.0f, 1.0f);
		skyboxShader.setUniformMatrix4f("model_matrix", modelMatrix);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		// NY
		textures.bind("skybox_ny");
		modelMatrix.setIdentity().rotate(90.0f,-1.0f, 0.0f,0.0f).rotate(90.0f, 0.0f, 0.0f,-1.0f).translate(0.0f, 0.0f, -0.5f).scale(1.0f, -1.0f, 1.0f);
		skyboxShader.setUniformMatrix4f("model_matrix", modelMatrix);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		// PY
		textures.bind("skybox_py");
		modelMatrix.setIdentity().rotate(90.0f, 1.0f, 0.0f, 0.0f).translate(0.0f, 0.0f, -0.5f).scale(1.0f, -1.0f, 1.0f);
		skyboxShader.setUniformMatrix4f("model_matrix", modelMatrix);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		
		textures.unbind();
		buffers.unBind(GLES20.GL_ARRAY_BUFFER);
		skyboxShader.unUse();
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		//viewMatrix.setLookAt(camposx, camposy, camposz, 0.0f, 0.0f, 0.0f, camupx, camupy, camupz);
		viewMatrix.setLookAt(camposx + cameraX, camposy + cameraY, camposz + cameraZ,
				cameraX, cameraY, cameraZ, camupx, camupy, camupz);
		
		// BASIS
		simpleShader.use();
		buffers.bind("basis", GLES20.GL_ARRAY_BUFFER);
		modelMatrix.setIdentity().scaleX(20.0f).scaleZ(20.0f);
		mvMatrix.setProduction(viewMatrix, modelMatrix);
		simpleShader.setUniformMatrix4f("modelview_matrix", mvMatrix);
		simpleShader.setUniformMatrix4f("projection_matrix", perspectiveMatrix);
		simpleShader.enableVertexAttribArrays("pos", "col");
		GLES20.glVertexAttribPointer(simpleShader.attribLoc("pos"), 3, GLES20.GL_FLOAT, true, 24, 0);
		GLES20.glVertexAttribPointer(simpleShader.attribLoc("col"), 3, GLES20.GL_FLOAT, true, 24, 12);
		GLES20.glDrawArrays(GLES20.GL_LINES, 0, 6);
		buffers.unBind(GLES20.GL_ARRAY_BUFFER);
		simpleShader.unUse();
		
		
		// FIELD
		boxShader.use();
		boxShader.setUniform1i("decal", 0);
		boxShader.setUniformMatrix4f("projection_matrix", perspectiveMatrix);
		boxShader.enableVertexAttribArrays("pos", "nrm", "tc");
		
		buffers.bind("sidebox", GLES20.GL_ARRAY_BUFFER);
		textures.bind("wall");
		boxShader.setUniform3f("cam_pos", cameraX + camposx, cameraY + camposy, cameraZ + camposz);
		GLES20.glVertexAttribPointer(boxShader.attribLoc("pos"), 3, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("sidebox", "vertices").offset);
		GLES20.glVertexAttribPointer(boxShader.attribLoc("nrm"), 3, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("sidebox", "normals").offset);
		GLES20.glVertexAttribPointer(boxShader.attribLoc("tc"), 2, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("sidebox", "texcoords").offset);
		
		GameField gameField = world.getGameField();
		for (int i = 0; i < gameField.getSizeX(); ++i) {
			for (int j = 0; j < gameField.getSizeZ(); ++j) {
				int height = gameField.getCellAt(i, j).getHeight();
				for (int k = 0; k <= height; ++k) {
					modelMatrix.setIdentity().translate(i - gameField.getSizeX() * 0.5f, k * 0.5f, j - gameField.getSizeZ() * 0.5f);
					mvMatrix.setProduction(viewMatrix, modelMatrix);
					GLES20.glUniformMatrix4fv(boxShader.uniformLoc("view_matrix"), 1, false, viewMatrix.data, 0);
					GLES20.glUniformMatrix4fv(boxShader.uniformLoc("model_matrix"), 1, false, modelMatrix.data, 0);
					GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6 * 4);
				}
			}
		}
		boxShader.unUse();
		
		
		buffers.bind("topbox", GLES20.GL_ARRAY_BUFFER);
		GLES20.glVertexAttribPointer(boxShader.attribLoc("pos"), 3, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("topbox", "vertices").offset);
		GLES20.glVertexAttribPointer(boxShader.attribLoc("nrm"), 3, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("topbox", "normals").offset);
		GLES20.glVertexAttribPointer(boxShader.attribLoc("tc"), 2, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("topbox", "texcoords").offset);
		boxShader.use();
		textures.bind("floor");
		for (int i = 0; i < gameField.getSizeX(); ++i) {
			for (int j = 0; j < gameField.getSizeZ(); ++j) {
				modelMatrix.setIdentity().translate(i - gameField.getSizeX() * 0.5f, -0.25f, j - gameField.getSizeZ() * 0.5f).rotateX(180.0f);
				mvMatrix.setProduction(viewMatrix, modelMatrix);
				boxShader.setUniformMatrix4f("view_matrix", viewMatrix);
				boxShader.setUniformMatrix4f("model_matrix", modelMatrix);
				GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6 * 1);		
				
				int height = gameField.getCellAt(i, j).getHeight();
				if (gameField.getCellAt(i, j).getLightState() == CellLightState.NO_LIGHT) {
					modelMatrix.setIdentity().translate(i - gameField.getSizeX() * 0.5f, height * 0.5f + 0.25f, j - gameField.getSizeZ() * 0.5f);
					mvMatrix.setProduction(viewMatrix, modelMatrix);
					boxShader.setUniformMatrix4f("view_matrix", viewMatrix);
					boxShader.setUniformMatrix4f("model_matrix", modelMatrix);
					GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6 * 1);
				}
			}
		}
		lightFloorShader.use();
		lightFloorShader.enableVertexAttribArrays("pos", "tc", "nrm");
		textures.bind("lightfloor");
		lightFloorShader.setUniform1i("decal", 0);
		lightFloorShader.setUniform3f("cam_pos", cameraX + camposx, cameraY + camposy, cameraZ + camposz);
		lightFloorShader.setUniformMatrix4f("projection_matrix", perspectiveMatrix);
		GLES20.glVertexAttribPointer(lightFloorShader.attribLoc("pos"), 3, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("topbox", "vertices").offset);
		GLES20.glVertexAttribPointer(lightFloorShader.attribLoc("nrm"), 3, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("topbox", "normals").offset);
		GLES20.glVertexAttribPointer(lightFloorShader.attribLoc("tc"), 2, GLES20.GL_FLOAT, true, 0,
				buffers.getNamedOffset("topbox", "texcoords").offset);
		int togglingX = -1;
		int togglingZ = -1;
		float togglingEmittance = 0.0f;
		if (world.getWorldState() == WorldState.EXECUTION) {
			float f = world.getCompletionOperationProgress();
			if (world.getCurrentRunningCommand() == Command.TOGGLE_LIGHT) {
				togglingX = world.getCurrentRobotState().getPosX();
				togglingZ = world.getCurrentRobotState().getPosZ();
				if (gameField.getCellAt(togglingX, togglingZ).getLightState() == CellLightState.LIGHT_ON) {
					togglingEmittance = 1.0f - f;
				} else {
					togglingEmittance = f;
				}
			}
		}
		for (int i = 0; i < gameField.getSizeX(); ++i) {
			for (int j = 0; j < gameField.getSizeZ(); ++j) {
				if (gameField.getCellAt(i, j).getLightState() != CellLightState.NO_LIGHT) {
					int height = gameField.getCellAt(i, j).getHeight();
					if (i == togglingX && j == togglingZ) {
						lightFloorShader.setUniform1f("emittance", togglingEmittance);
					} else if (gameField.getCellAt(i, j).getLightState() == CellLightState.LIGHT_ON) {
						lightFloorShader.setUniform1f("emittance", 1.0f);
					} else {
						lightFloorShader.setUniform1f("emittance", 0.0f);
					}
					lightFloorShader.setUniform3f("emit_center", i - gameField.getSizeX() * 0.5f, height * 0.5f + 0.25f, j - gameField.getSizeZ() * 0.5f);
					modelMatrix.setIdentity().translate(i - gameField.getSizeX() * 0.5f, height * 0.5f + 0.25f, j - gameField.getSizeZ() * 0.5f);
					mvMatrix.setProduction(viewMatrix, modelMatrix);
					lightFloorShader.setUniformMatrix4f("view_matrix", viewMatrix);
					lightFloorShader.setUniformMatrix4f("model_matrix", modelMatrix);
					GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6 * 1);
				}
			}
		}
		textures.unbind();
		Shader.unUse();
		
		// ROBOT
		
		objShader.use();
		textures.bind("r2d2_png");
		objShader.enableVertexAttribArrays("pos", "nrm", "tc");
		
		objShader.setUniform1i("decal", 0);
		GLES20.glUniform3f(objShader.uniformLoc("cam_pos"), camposx, camposy, camposz);
		objShader.setUniformMatrix4f("projection_matrix", perspectiveMatrix);
		objShader.setUniformMatrix4f("view_matrix", viewMatrix);
		ImmutableRobot immutableRobot = world.getCurrentRobotState();
		Matrix4 robotModelMatrix = calculateCurrentRobotWorldMatrix();
	
		objShader.setUniformMatrix4f("model_matrix", robotModelMatrix);
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
		
		// SUN
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
		textureShader.use();
		buffers.bind("cquad", GLES20.GL_ARRAY_BUFFER);
		modelMatrix.setBillboardMatrix(camposx + cameraX, camposy + cameraY, camposz + cameraZ, 5.0f * 2.0f, 4.0f * 2.0f, 3.0f * 2.0f, camupx, camupy, camupz)
			.scale(7.0f, 7.0f, 1.0f);
		mvMatrix.setProduction(viewMatrix, modelMatrix);
		textures.bind("sun");
		textureShader.setUniform1i("decal", 0);
		textureShader.setUniformMatrix4f("modelview_matrix", mvMatrix);
		textureShader.setUniformMatrix4f("projection_matrix", perspectiveMatrix);
		textureShader.enableVertexAttribArrays("pos", "tc");
		GLES20.glVertexAttribPointer(skyboxShader.attribLoc("pos"), 2, GLES20.GL_FLOAT, true, 0, 0);
		GLES20.glVertexAttribPointer(skyboxShader.attribLoc("tc"),  2, GLES20.GL_FLOAT, true, 0, 12 * 4);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		buffers.unBind(GLES20.GL_ARRAY_BUFFER);
		textureShader.unUse();
		GLES20.glDisable(GLES20.GL_BLEND);
		
		// SMOKE
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokeShader.use();
		textures.bind("smoke");
		smokeShader.enableVertexAttribArrays("pos", "tc");
		buffers.bind("cquad", GLES20.GL_ARRAY_BUFFER);
		GLES20.glVertexAttribPointer(smokeShader.attribLoc("pos"), 2, GLES20.GL_FLOAT, true, 0, 0);
		GLES20.glVertexAttribPointer(smokeShader.attribLoc("tc"),  2, GLES20.GL_FLOAT, true, 0, 12 * 4);
		smokeShader.setUniform1i("decal", 0);
		smokeShader.setUniformMatrix4f("view_matrix", viewMatrix);
		smokeShader.setUniformMatrix4f("projection_matrix", perspectiveMatrix);
		//smokeShader.setUniform3f("cam_pos", camposx + cameraX, camposy + cameraY, camposz + cameraZ);
		smokeShader.setUniform3f("cam_dir", -camposx, -camposy, -camposz);
		smokeShader.setUniform3f("cam_up", camupx, camupy, camupz);
		
		for (FountainParticle p: fountainParticleSystem.particles) {
			smokeShader.setUniform3f("particle_pos", p.position);
			smokeShader.setUniform1f("life", p.life);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		}
		
		buffers.unBind(GLES20.GL_ARRAY_BUFFER);
		smokeShader.unUse();
		GLES20.glDisable(GLES20.GL_BLEND);
		
		// UI
		GLES20.glViewport(0, 0, (int)width, (int)height);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
		buttonShader.use();
		orthoMatrix.setOrthoMatrix(0.0f, width, height, 0.0f, -1.0f, 1.0f);
		GLES20.glUniform1i(buttonShader.uniformLoc("decal"), 0);
		buttonShader.enableVertexAttribArrays("pos", "tc");
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
				case JUMP:         textures.bind("command_jump");    break;
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
			buttonShader.setUniformMatrix4f("model_matrix", modelMatrix);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		}
		buttonShader.unUse();
		
		long curr = SystemClock.elapsedRealtime();
		programmingUI.update(curr);
		
		// FPS
		if (lastTimeFPSUpdated < 0) {
			lastTimeFPSUpdated = curr;
			frameCounter = 0;
			lastFPS = 0;
		} else {
			frameCounter++;
			
			float deltaTime = (curr - lastTimeWorldUpdated) / 1000.0f;
			world.update(deltaTime);
			fountainParticleSystem.update(deltaTime);
			fountainParticleSystem.setPosition(
					immutableRobot.getPosX() - gameField.getSizeX() * 0.5f,
					immutableRobot.getPosY() * 0.5f + 0.25f + robotDeltaY,
					immutableRobot.getPosZ() - gameField.getSizeZ() * 0.5f);
			fountainParticleSystem.setPosition(new Vector3().transformBy(robotModelMatrix));
			//fountainParticleSystem.sortParticles(camposx, camposy, camposz);
			fountainParticleSystem.sortParticles(-camposx, -camposy, -camposz);
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
		
		Runnable onStart = new Runnable() {
			@Override
			public void run() {
				Log.i("FUCK", "onStart clicked");
				List<ArrayList<Command>> commands = programmingUI.getCommands();
				Map<String, List<Command>> program = new HashMap<String, List<Command>>();
				program.put("main", commands.get(0));
				program.put("A", commands.get(1));
				program.put("B", commands.get(2));
				world.beginExecution(new Program(program));
			}
		};

		Runnable onPause = new Runnable() {
			@Override
			public void run() {
				Log.i("FUCK", "onPause clicked");
				world.interruptExecution();
			}
		};

		programmingUI.init(w, h, onStart, onPause);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glClearDepthf(1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		
		Log.d("FUCK", "Initial memory usage: " + AndroidOpenGLActivity.getUsedMemorySize());
		// TODO(alex): implement automatic texture loader
		textures = new TextureHolder();
		frameBuffers = new FrameBufferHolder();

		textures.load("icon_scaling", R.drawable.icon_scaling);
		textures.load("icon_select", R.drawable.icon_select);
		textures.load("icon_go", R.drawable.icon_go);
		textures.load("empty_slot", R.drawable.empty_slot);
		textures.load("command_forward", R.drawable.command_forward);
		textures.load("command_jump", R.drawable.command_jump);
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
		
		textures.load("wall", R.drawable.bricks);
		textures.load("floor", R.drawable.floor);
		textures.load("lightfloor", R.drawable.lightfloor);
		textures.load("mix", R.drawable.mix);
		
		textures.load("sun", R.drawable.sun);
		textures.load("smoke", R.drawable.smoke);
		
		
		textures.createEmpty("fbo_depth", 1024, 1024, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_DEPTH_COMPONENT);
		Log.d("FUCK", "GL error: " + GLES20.glGetError());
		textures.createEmpty("fbo_color", 1024, 1024, GLES20.GL_RGBA, GLES20.GL_RGBA);
		Log.d("FUCK", "GL error: " + GLES20.glGetError());
		
		frameBuffers.create("fbo");
		frameBuffers.bind("fbo");
		//textures.attachToCurrentFrameBuffer("fbo_depth", GLES20.GL_DEPTH_ATTACHMENT);
		Log.d("FUCK", "GL error: " + GLES20.glGetError());
		textures.attachToCurrentFrameBuffer("fbo_color", GLES20.GL_COLOR_ATTACHMENT0);
		Log.d("FUCK", "GL error: " + GLES20.glGetError());
		Log.i("FUCK", "Framebuffer status: " + frameBuffers.getCurrentStatusString());
		frameBuffers.unbind();
		System.gc();
		Log.d("FUCK", "Memory usage after loading textures: " + AndroidOpenGLActivity.getUsedMemorySize());
		
		simpleShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "simpleshader");
		boxShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "boxshader");
		objShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "objshader");
		lightFloorShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "lightfloorshader");
		buttonShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "buttonshader");
		skyboxShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "skyboxshader");
		textureShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "textureshader");
		smokeShader = Shader.loadFromResource(AndroidOpenGLActivity.getContext(), "smokeshader");
	
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
		buffers.load("sidebox", GLBuffers.genSideBoxBuffer(0.5f, 0.25f, 0.5f), GLBuffers.genSideBoxBufferNamedOffsets());
		buffers.load("topbox", GLBuffers.gen3DHorizontalQuadBuffer(0.5f, 0.5f), GLBuffers.gen3DHorizontalQuadBufferNamedOffsets());
		
		
		textures.load("r2d2_png", R.drawable.r2d2_png);
		Log.d("FUCK", "Start loading R2D2");
		//r2d2 = new WavefrontObject("r2d2_obj");
		//robot = Model.loadFromAndroidObj(context, "r2d2_obj", 0.08f);
		//ResourceUtils.saveObjectToExternalStorage(context, robot, "robot.txt");
		
		robot = Model.loadSerializedFromStream(ResourceUtils.getInputStreamForRawResource(AndroidOpenGLActivity.getContext(), "robot"));
		robot.cleanup();
		float[] robotDimensions = robot.getModelDimensions();
		Log.d("FUCK", ""+robotDimensions[0]+" "+robotDimensions[1]+" "+robotDimensions[2]+" "+robotDimensions[3]+" "+robotDimensions[4]+" "+robotDimensions[5]+" ");
		float maxHorizontalDimension = Math.max(Math.max(-robotDimensions[0], robotDimensions[1]), Math.max(-robotDimensions[4], robotDimensions[5]));
		robot.scale(0.4f / maxHorizontalDimension);
		robotDeltaY = -robot.getModelDimensions()[2];
		for (String s: robot.getGroupNames()) {
			float[] vb = robot.getGroupVertexBuffer(s);
			buffers.load("r2d2_obj::" + s, vb, robot.genBufferOffsets(s));
		}
		robot.deleteData();
		Log.d("FUCK", "End loading R2D2.");
		Log.d("FUCK", "Memory usage after loading all: " + AndroidOpenGLActivity.getUsedMemorySize());
		
		ImmutableRobot immutableRobot = world.getCurrentRobotState();
		GameField gameField = world.getGameField();
		
		cameraX = immutableRobot.getPosX() - gameField.getSizeX() * 0.5f;
		cameraY = immutableRobot.getPosY() + 0.25f + robotDeltaY;
		cameraZ = immutableRobot.getPosZ() - gameField.getSizeZ() * 0.5f;
		
		cameraRadius = 5.0f;
		cameraPhi = (float) (Math.PI / 3.0f);
		cameraTheta = (float) (Math.PI / 6.0f);
		
		fountainParticleSystem = new FountainParticleSystem()
			.setDirection(0.0f, 1.0f, 0.0f)
			.setEmitAngle(0.35f)
			.setFades(0.2f, 0.4f)
			.setVelocities(0.25f, 0.4f)
			.setGravity(0.0f, -0.06f, 0.0f)
			.setPosition(cameraX, cameraY, cameraZ)
			.setParticleSize(0.5f)
			.setResurrection(true)
			.initialize(30);
	}

	public int getFPS() {
		return lastFPS;
	}
	
	private Matrix4 calculateCurrentRobotWorldMatrix() {
		ImmutableRobot immutableRobot = world.getCurrentRobotState();
		GameField gameField = world.getGameField();
		Matrix4 modelMatrix = new Matrix4();
		modelMatrix.setIdentity().translate(immutableRobot.getPosX() - gameField.getSizeX() * 0.5f, immutableRobot.getPosY() * 0.5f + 0.25f + robotDeltaY, immutableRobot.getPosZ() - gameField.getSizeZ() * 0.5f);
		modelMatrix.rotateY(-90.0f + immutableRobot.getLookDirection().getRotationDegreesFromPosX());
		if (world.getWorldState() == WorldState.EXECUTION) {
			float f = world.getCompletionOperationProgress();
			switch (world.getCurrentRunningCommand()) {
			case MOVE_FORWARD: {
				modelMatrix.translate(0.0f, 0.0f, -f);
				break;
			}
			case ROTATE_LEFT: {
				modelMatrix.rotateY(90.0f * f);
				break;
			}
			case ROTATE_RIGHT: {
				modelMatrix.rotateY(-90.0f * f);
				break;
			}
			case JUMP: {
				int nextCellX = immutableRobot.getPosX() + immutableRobot.getLookDirection().getLookX();
				int nextCellZ = immutableRobot.getPosZ() + immutableRobot.getLookDirection().getLookZ();
				float deltaHeight = gameField.getCellAt(nextCellX, nextCellZ).getHeight() - 
						gameField.getCellAt(immutableRobot.getPosX(), immutableRobot.getPosZ()).getHeight();
				Vector3 offset = Vector3.bezier(new Vector3(0.0f, 0.0f,0.0f), new Vector3(0.0f, 2.0f, 0.0f),
						new Vector3(0.0f, deltaHeight * 0.5f + 1.5f, -1.0f), new Vector3(0.0f, deltaHeight * 0.5f, -1.0f), f);
				modelMatrix.translate(offset);
				break;
			}
			}
		}
		return modelMatrix;
	}
}
