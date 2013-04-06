package max.popov;


import alex.taran.opengl.AndroidOpenGLActivity;
import alex.taran.opengl.R;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class LevelListActivity extends Activity {
	private GridView gridview;
	private ImageView pressedImageView = null;
	private int previousPosition = -1;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.level_list);
		
		gridview = (GridView) findViewById(R.id.gvLvl);
	    gridview.setAdapter(new ImageAdapter(this));

	    gridview.setOnTouchListener(new OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {

	            int action = event.getActionMasked();
	            float currentXPosition = event.getX();
	            float currentYPosition = event.getY();
	            int position = gridview.pointToPosition((int) currentXPosition, (int) currentYPosition);
	            ImageView curImageView = (ImageView) gridview.getChildAt(position);
	            
	            Resources res = getResources();
	            TypedArray pressedButtonImages = res.obtainTypedArray(R.array.pressed_button_images); 
	            TypedArray nonpressedButtonImages = res.obtainTypedArray(R.array.nonpressed_button_images);

	            if(curImageView != null){
    	            Drawable nonpressedImage = nonpressedButtonImages.getDrawable(position);
            		Drawable pressedImage = pressedButtonImages.getDrawable(position);
            		

	            	if(action == MotionEvent.ACTION_DOWN) {
	            		curImageView.setImageDrawable(pressedImage);
	            		pressedImageView = curImageView;
	            		previousPosition = position;
	            	}
	            	
	            	if(action == MotionEvent.ACTION_UP) {
	            		curImageView.setImageDrawable(nonpressedImage);
	            		if(curImageView == pressedImageView){
	            			//open new lvl activity here
	            			Intent intent = new Intent(LevelListActivity.this, AndroidOpenGLActivity.class);
	            			intent.putExtra("lvl number", position + 1); //numbered from 1 to 22
	    					startActivity(intent);
	            		}
	            	}
	            	
	            	if(action == MotionEvent.ACTION_CANCEL){
	            		curImageView.setImageDrawable(nonpressedImage);
	            	}
	            }else{
					if(pressedImageView != null && previousPosition != -1){
	    	            Drawable nonpressedImage = nonpressedButtonImages.getDrawable(previousPosition);
						pressedImageView.setImageDrawable(nonpressedImage);
					}
	            }

	            return true;
	        }
	    });
	   
	    
	}
	
}
