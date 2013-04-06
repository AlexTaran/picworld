package ilya.irhin.editor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartEditorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Utility.setDataBaseContext();
		super.onCreate(savedInstanceState);
		setContentView(alex.taran.opengl.R.layout.activity_start_editor);
	}

	public void onNewUserButtonClick(View v) {
		Intent intent = new Intent(StartEditorActivity.this,
				SizesEditorActivity.class);
		startActivity(intent);
		finish();
	}

	public void onExistingUserButtonClick(View v) {
		Intent intent = new Intent(StartEditorActivity.this,
				ChooseExistingLevelActivity.class);
		startActivity(intent);
		finish();
	}

}
