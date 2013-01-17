package alex.taran.opengl;

import java.util.Random;

import alex.taran.GameView;
import alex.taran.opengl.GameUnit.UnitType;
import alex.taran.picworld.GameField;
import alex.taran.picworld.Robot;
import alex.taran.picworld.GameField.CellLightState;
import alex.taran.picworld.World;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.opengl.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class AndroidOpenGLActivity extends Activity {
	private MyRenderer renderer;
	private GLSurfaceView glView;
	private World world;
	private GameView gameView;
	private static Context applicationContext;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		applicationContext = getApplicationContext();
		
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
		
		world = new World(gameField, initRobot);
		
		gameView.setEventListener(new GameView.EventListener() {
			
			@Override
			public void onResetRobot() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onInterruptExecute() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onBeginExecute() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		renderer = new MyRenderer(this.getApplicationContext(), world);
		View v = findViewById(R.id.my_view);
		//Log.e("FUCK", v.toString());
		glView = (MyGLSurfaceView)v;
		glView.setRenderer(renderer);
		
		/*gameView = (GameView)findViewById(R.id.game_view);
		if (gameView == null) {
			Log.e("FUCK", "Cannot find R.id.game_view!");
			throw new NullPointerException("Cannot find R.id.game_view!");
		}*/
		
		
		
		
		h.postDelayed(updateProc, 100);
	}
	
	
	Handler h = new Handler();
	
	private Runnable updateProc = new Runnable() {
		@Override
		public void run() {
			setTitle("FPS:"+renderer.getFPS());
			h.postDelayed(this, 300);
		}
	};
	
	public static Context getContext() {
		return applicationContext;
	}
}