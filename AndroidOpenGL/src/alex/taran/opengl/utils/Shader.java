/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alex.taran.utils.Matrix4;
import alex.taran.utils.Vector3;
import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class Shader {
	private int program;
	private int vertexShader;
	private int fragmentShader;
	private Map<String, Integer> attribLocations;
	private Map<String, Integer> uniformLocations;

	public Shader() {
		program = GLES20.glCreateProgram();
		vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
	}

	public static Shader loadFromResource(Context context, String resName) {
		List<String> text = ResourceUtils.loadRawTextFile(context, resName);
		String vertex = "", fragment = "";
		int mode = 0;
		for (String s : text) {
			if(s.equals("###VERTEX")){
				mode = GLES20.GL_VERTEX_SHADER;
				continue;
			}
			if(s.equals("###FRAGMENT")){
				mode = GLES20.GL_FRAGMENT_SHADER;
				continue;
			}
			switch(mode){
			case GLES20.GL_VERTEX_SHADER: vertex = vertex+"\n"+s; break;
			case GLES20.GL_FRAGMENT_SHADER: fragment = fragment+"\n"+s; break;
			}
		}
		Shader sh = new Shader();
		sh.load(vertex, fragment);
		return sh;
	}

	public void load(String vertexShaderCode, String fragmentShaderCode) {
		String infoLog;
		GLES20.glShaderSource(vertexShader, vertexShaderCode);
		GLES20.glCompileShader(vertexShader); checkShader(vertexShader);
		GLES20.glAttachShader(program, vertexShader); checkShader(vertexShader);
		infoLog = GLES20.glGetShaderInfoLog(vertexShader).trim();
		if(infoLog.length()!=0){
			Log.e("OpenGL_Shaders", "VertexShader InfoLog: "+infoLog);
		}
		GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
		GLES20.glCompileShader(fragmentShader); checkShader(fragmentShader);
		GLES20.glAttachShader(program, fragmentShader); checkShader(fragmentShader);
		infoLog = GLES20.glGetShaderInfoLog(fragmentShader);
		if(infoLog.length()!=0){
			Log.e("OpenGL_Shaders", "FragmentShader InfoLog: "+infoLog);
		}
		
		
		GLES20.glLinkProgram(program); checkShader(fragmentShader);
		GLES20.glValidateProgram(program);
		infoLog = GLES20.glGetProgramInfoLog(program);
		if(infoLog.length()!=0){
			Log.e("OpenGL_Shaders", "Program InfoLog: "+infoLog);
		}
		attribLocations = new HashMap<String, Integer>();
		uniformLocations = new HashMap<String, Integer>();
	}
	
	private void checkShader(int shader) {
		String infoLog = GLES20.glGetShaderInfoLog(shader);
		if(infoLog.length()!=0){
			Log.e("OpenGL_Shaders", "Shader InfoLog: "+infoLog);
		}
	}

	public void use() {
		GLES20.glUseProgram(program);
	}

	public static void unUse() {
		GLES20.glUseProgram(0);
	}

	public int attribLoc(String s) {
		Integer loc = attribLocations.get(s);
		if (loc == null) {
			loc = GLES20.glGetAttribLocation(program, s);
			if (loc >= 0) {
				attribLocations.put(s, loc);
			}else{
				//Log.e("OpenGL","Cannot find attrib location "+s);
			}
		}
		return loc;
	}
	
	public void enableVertexAttribArray(String s) {
		GLES20.glEnableVertexAttribArray(attribLoc(s));
	}
	
	public void enableVertexAttribArrays(String ... s) {
		for (String str :s) {
			enableVertexAttribArray(str);
		}
	}

	public int uniformLoc(String s) {
		Integer loc = uniformLocations.get(s);
		if (loc == null) {
			loc = GLES20.glGetUniformLocation(program, s);
			if (loc >= 0) {
				uniformLocations.put(s, loc);
			}else{
				//Log.e("OpenGL","Cannot find uniform location "+s);
			}
		}
		return loc;
	}
	
	// uniform setters
	
	public void setUniform1i(String s, int i) {
		GLES20.glUniform1i(uniformLoc(s), i);
	}
	
	public void setUniform1f(String s, float f) {
		GLES20.glUniform1f(uniformLoc(s), f);
	}
	
	public void setUniform3f(String s, float x, float y, float z) {
		GLES20.glUniform3f(uniformLoc(s), x, y, z);
	}
	
	public void setUniform3f(String s, Vector3 v) {
		GLES20.glUniform3f(uniformLoc(s), v.x, v.y, v.z);
	}
	
	public void setUniformMatrix4f(String s, Matrix4 m) {
		GLES20.glUniformMatrix4fv(uniformLoc(s), 1, false, m.data, 0);
	}

	@Override
	public void finalize() {
		GLES20.glDeleteShader(vertexShader);
		GLES20.glDeleteShader(fragmentShader);
		GLES20.glDeleteProgram(program);
	}
}
