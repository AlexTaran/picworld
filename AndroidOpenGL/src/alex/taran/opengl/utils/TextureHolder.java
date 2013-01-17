package alex.taran.opengl.utils;

import java.util.HashMap;

import java.util.Map;
import java.util.Map.Entry;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLES20;
import android.util.Log;

public class TextureHolder extends Holder{
	private int[] temp = new int[1]; // for temporary tex ID
	private Map<String,Integer> textures = new HashMap<String,Integer>();
	
	public TextureHolder(Context theContext){
		super(theContext);
	}
	
	public void bind(String s){
		Integer i = textures.get(s);
		if(i!=null){
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, i);
		}
	}
	
	public void unbind(){
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	}
	
	public void load(String s,int resId){
		delete(s);
		Bitmap bitmap = null;
		Log.d("FUCK", "trying to load texture "+s+" from R.id = "+resId);
		try {
			bitmap = BitmapFactory.decodeResource(context.getResources(),resId);
		} catch (Exception e) {
			Log.e("FUCK", "Error while loading texture "+s+" from R.id = "+resId);
		}
		
		// создаем текстуру и биндим
		GLES20.glGenTextures(1, temp, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, temp[0]);
		// настраиваем параметры текстуры
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		
		bitmap.recycle();
		textures.put(s, temp[0]);
		unbind();
	}
	
	public void delete(String s){
		Integer i = textures.get(s);
		if(i!=null){
			temp[0]=i;
			GLES20.glDeleteTextures(1, temp, 0);
			textures.remove(s);
		}
	}
	
	@Override
	public void clear(){
		for(Integer i:textures.values()){
			temp[0]=i;
			GLES20.glDeleteTextures(1, temp, 0);
		}
		textures.clear();
	}
}
