/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.opengl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import vladimir.losev.SimpleHUD;
import alex.taran.opengl.utils.ResourceUtils;
import alex.taran.picworld.GameField;
import alex.taran.picworld.GameField.CellLightState;
import alex.taran.picworld.LevelData;
import alex.taran.picworld.Robot;
import alex.taran.picworld.World;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.google.gson.Gson;

public class AndroidOpenGLActivity extends Activity {
	private MyRenderer renderer;
	private MyGLSurfaceView glView;
	private World world;
	private SimpleHUD programmingUI;
	private ActivityManager activityManager;
	private int levelNumber;
	//private GameView gameView;
	
	private static AndroidOpenGLActivity instance;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		instance = this;
		
		Log.d("FUCK", "Activity.onCreate memory usage at start: " + getUsedMemorySize());
		
		activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		Log.d("FUCK", "Application memory class = " + activityManager.getMemoryClass());
		
		setTitle("Fuck you, vedroid!");
		
		setContentView(R.layout.gamescreen);
		GameField gameField = new GameField(10, 15); 
		gameField.getCellAt(5, 4).setHeight(2).setLightState(CellLightState.LIGHT_OFF);
		gameField.getCellAt(3, 4).setHeight(1).setLightState(CellLightState.LIGHT_ON);
		for (int i = 0; i < 10; ++i) {
			gameField.getCellAt(i, 0).setHeight(1);
			gameField.getCellAt(i, 14).setHeight(1);
		}
		for (int i = 0; i < 15; ++i) {
			gameField.getCellAt(0, i).setHeight(1);
			gameField.getCellAt(9, i).setHeight(1);
		}
		gameField.getCellAt(0, 0).setHeight(3);
		gameField.getCellAt(9, 0).setHeight(3);
		gameField.getCellAt(0, 14).setHeight(3);
		gameField.getCellAt(9, 14).setHeight(3);
		
		Robot initRobot = new Robot();
		
		/*Gson gson = new Gson();	
		File root = android.os.Environment.getExternalStorageDirectory(); 
		File myFolder = new File(root, "PicWorld");
		myFolder.mkdir();
		Log.d("FUCK", root.getAbsolutePath());
		File file = new File(myFolder, "testLevel.txt");
		try {
			FileOutputStream f = new FileOutputStream(file);
			OutputStreamWriter ow = new OutputStreamWriter(f);
			//ow.write("Test file write...");
			//ow.write(gson.toJson(gameField).toString());
			//ow.write(gson.toJson(initRobot).toString());
			ow.write(new LevelData(gameField, initRobot).toJson());
			ow.close();
			f.close();
			MediaScannerConnection.scanFile(this.getApplicationContext(), new String[]{file.getAbsolutePath()}, null, null);
			Log.d("FUCK", "Successfully written: " + file.getAbsolutePath());
		} catch (Exception e) {
			Log.d("FUCK", "Unable to write: " + file.getAbsolutePath());
			e.printStackTrace();
		}*/
		
		LevelData levelData = LevelData.createFromJson(ResourceUtils.loadRawTextFileAsString(getApplicationContext(), "testlevel"));
		
		//world = new World(gameField, initRobot);
		
		levelNumber = getIntent().getExtras().getInt("lvl number");
		world = new World(levelData.gameField, levelData.initRobot);
		programmingUI = new SimpleHUD(new int[] {levelData.mainProcSize, levelData.firstProcSize, levelData.secondProcSize});
		
		Log.d("FUCK", "Activity.onCreate memory usage the end: " + getUsedMemorySize());
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Runnable onWinning = new Runnable() {
			@Override
			public void run() {
				finish();
			}
		};
		renderer = new MyRenderer(world, programmingUI, onWinning);
		View v = findViewById(R.id.my_view);
		//Log.e("FUCK", v.toString());
		glView = (MyGLSurfaceView)v;
		glView.setRenderer(renderer);
		glView.setHUD(programmingUI);
		
		h.postDelayed(updateProc, 100);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onDestroy() {
		glView.cleanupOpenGLResorces();
		super.onDestroy();
	}
	
	
	Handler h = new Handler();
	
	private Runnable updateProc = new Runnable() {
		@Override
		public void run() {
			setTitle("Level " + levelNumber + " FPS:"+renderer.getFPS());
			h.postDelayed(this, 300);
		}
	};
	
	public static Context getContext() {
		return instance.getApplicationContext();
	}
	
	public static long getUsedMemorySize() {
	    long freeSize = 0L;
	    long totalSize = 0L;
	    long usedSize = -1L;
	    try {
	        Runtime info = Runtime.getRuntime();
	        freeSize = info.freeMemory();
	        totalSize = info.totalMemory();
	        usedSize = totalSize - freeSize;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return usedSize;
	}
}