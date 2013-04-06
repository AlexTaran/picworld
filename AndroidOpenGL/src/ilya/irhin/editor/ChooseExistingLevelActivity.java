package ilya.irhin.editor;

import alex.taran.opengl.R;
import alex.taran.picworld.LevelData;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ChooseExistingLevelActivity extends Activity {
	private Spinner spinner;
	LevelData[] levels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_existing_level);
		levels = Utility.getDataBase().getAllLevels(this);
		ArrayAdapter<LevelData> adapter = new ArrayAdapter<LevelData>(this,
				android.R.layout.simple_spinner_item, levels);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner = (Spinner) findViewById(R.id.user_list);
		spinner.setAdapter(adapter);
		spinner.setPromptId(R.string.available_levels);
	}

	public void OnChooseExistingUserButtonReturnClick(View v) {
		Intent intent = new Intent(ChooseExistingLevelActivity.this,
				StartEditorActivity.class);
		startActivity(intent);
		finish();
	}

	public void OnChooseExistingUserButtonContinueClick(View v) {
		if (spinner.getSelectedItem() != null) {
			int num = spinner.getSelectedItemPosition();
			Intent intent = new Intent(ChooseExistingLevelActivity.this,
					FieldEditorActivity.class);
			intent.putExtra(Utility.LEVEL_TAG, levels[num].toJson());
			startActivity(intent);
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(ChooseExistingLevelActivity.this,
					StartEditorActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
