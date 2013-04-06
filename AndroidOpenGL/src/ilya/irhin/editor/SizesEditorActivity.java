package ilya.irhin.editor;

import alex.taran.opengl.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SizesEditorActivity extends Activity {
	public static final String HEIGHT = "height";
	public static final String WIDTH = "width";
	public static final int HEIGHT_MIN_VAL = 5;
	public static final int HEIGHT_MAX_VAL = 15;
	public static final int WIDTH_MIN_VAL = 5;
	public static final int WIDTH_MAX_VAL = 15;

	private SeekBar heightBar;
	private SeekBar weightBar;
	private TextView heightValueView;
	private TextView weightValueView;

	private void tuneSeekBar(SeekBar seekBar, final TextView valueView,
			final int minVal, final int maxVal) {
		seekBar.setMax(maxVal - minVal);
		valueView.setText(String.valueOf(seekBar.getProgress() + minVal));
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				valueView.setText(String.valueOf(seekBar.getProgress() + minVal));
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sizes_editor);
		heightBar = (SeekBar) findViewById(R.id.heightBar);
		weightBar = (SeekBar) findViewById(R.id.weightBar);
		heightValueView = (TextView) findViewById(R.id.heightValue);
		weightValueView = (TextView) findViewById(R.id.weightValue);
		tuneSeekBar(heightBar, heightValueView, HEIGHT_MIN_VAL, HEIGHT_MAX_VAL);
		tuneSeekBar(weightBar, weightValueView, WIDTH_MIN_VAL, WIDTH_MAX_VAL);
	}

	@Override
	public void onResume() {
		heightBar = (SeekBar) findViewById(R.id.heightBar);
		weightBar = (SeekBar) findViewById(R.id.weightBar);
		heightValueView = (TextView) findViewById(R.id.heightValue);
		weightValueView = (TextView) findViewById(R.id.weightValue);
		tuneSeekBar(heightBar, heightValueView, HEIGHT_MIN_VAL, HEIGHT_MAX_VAL);
		tuneSeekBar(weightBar, weightValueView, WIDTH_MIN_VAL, WIDTH_MAX_VAL);
		super.onResume();
	}

	public void onClick(View v) {
		Intent intent = new Intent(SizesEditorActivity.this,
				FieldEditorActivity.class);
		intent.putExtra(HEIGHT, heightBar.getProgress() + HEIGHT_MIN_VAL);
		intent.putExtra(WIDTH, weightBar.getProgress() + WIDTH_MIN_VAL);
		startActivity(intent);
		finish();
	}

}
