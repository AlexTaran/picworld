package alex.taran;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GameView extends RelativeLayout{
	public ImageView buttonCommandForward;
	public ImageView buttonCommandLeft;
	public ImageView buttonCommandRight;
	
	public GameView(Context context) {
		super(context);
	}
	
	public GameView(Context context, AttributeSet attrSet) {
		super(context, attrSet);
	}

	public GameView(Context context, AttributeSet attrSet, int defStyle) {
		super(context, attrSet, defStyle);
	}
	
	@Override
	public void onFinishInflate() {
		//buttonCommandForward = (ImageView)findViewById(R.id.button_command_forward);
		//buttonCommandLeft = (ImageView)findViewById(R.id.button_command_left);
		//buttonCommandRight = (ImageView)findViewById(R.id.button_command_right);
		OnTouchListener listener = new OnTouchListener() {
			private float startPointerX;
			private float startPointerY;
			private int startImageX;
			private int startImageY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				CommandButton commandButton = (CommandButton)v;
				LayoutParams layoutParams = (LayoutParams) v.getLayoutParams();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					commandButton.cancelAllAnimations();
					startPointerX = event.getRawX();
					startPointerY = event.getRawY();
					startImageX = layoutParams.leftMargin;
					startImageY = layoutParams.topMargin;
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					layoutParams.leftMargin = startImageX + (int)(event.getRawX() - startPointerX);
					layoutParams.topMargin = startImageY + (int)(event.getRawY() - startPointerY);
					v.setLayoutParams(layoutParams);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					commandButton.beginTranslationAnimationTo(commandButton.getInitialMarginLeft(), commandButton.getInitialMarginTop(), 200);
					eventListener.onBeginExecute();
				}
				return true;
			}
		};
		buttonCommandForward.setOnTouchListener(listener);
		buttonCommandLeft.setOnTouchListener(listener);
		buttonCommandRight.setOnTouchListener(listener);
	}
	
	public interface EventListener {
		public void onBeginExecute();
		public void onInterruptExecute();
		public void onResetRobot();
	}
	
	public void setEventListener(EventListener e) {
		eventListener = e;
	}
	
	private EventListener eventListener = new EventListener() {
		public void onBeginExecute(){}
		public void onInterruptExecute(){}
		public void onResetRobot(){}
	};
}
