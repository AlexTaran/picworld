package max.popov;


import alex.taran.opengl.R;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class MainMenuActivity extends Activity {
	private Button playGameButton;
	private Button settingsButton;
	private Button twitterButton;
	private Button levelEditorButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		playGameButton = (Button) findViewById(R.id.playGameButton);
		settingsButton = (Button) findViewById(R.id.settingsButton);
		twitterButton = (Button) findViewById(R.id.twitterButton);
		levelEditorButton = (Button) findViewById(R.id.lvlEditorButton);
		
		
		playGameButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(v == playGameButton && event.getAction() == MotionEvent.ACTION_DOWN){
					playGameButton.setBackgroundResource(R.drawable.play_game_button_pressed);
				}
				if(v == playGameButton && event.getAction() == MotionEvent.ACTION_UP){
					playGameButton.setBackgroundResource(R.drawable.play_game_button);
					Intent intent = new Intent(MainMenuActivity.this, LevelListActivity.class);
					startActivity(intent);
				}
				return true;
			}
		});
		
		
		
		settingsButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(v ==settingsButton && event.getAction() == MotionEvent.ACTION_DOWN){
					settingsButton.setBackgroundResource(R.drawable.settings_button_pressed);
				}
				if(v == settingsButton && event.getAction() == MotionEvent.ACTION_UP){
					settingsButton.setBackgroundResource(R.drawable.settings_button);
					Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
					startActivity(intent);
				}
				return true;
			}
		});
		
		twitterButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(v ==twitterButton && event.getAction() == MotionEvent.ACTION_DOWN){
					twitterButton.setBackgroundResource(R.drawable.twitter_button_pressed);
				}
				if(v == twitterButton && event.getAction() == MotionEvent.ACTION_UP){
					twitterButton.setBackgroundResource(R.drawable.twitter_button);
					//call twitter activity here
				}
				return true;
			}
		});
		
		levelEditorButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(v ==levelEditorButton && event.getAction() == MotionEvent.ACTION_DOWN){
					levelEditorButton.setBackgroundResource(R.drawable.lvl_editor_button_pressed);
				}
				if(v == levelEditorButton && event.getAction() == MotionEvent.ACTION_UP){
					levelEditorButton.setBackgroundResource(R.drawable.lvl_editor_button);
					//call levelEditor activity here
				}
				return true;
			}
		});
	}
	
	
	
	
	
	
	
}
