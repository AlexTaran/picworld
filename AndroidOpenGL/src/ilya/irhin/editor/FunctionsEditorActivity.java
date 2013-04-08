package ilya.irhin.editor;

import alex.taran.opengl.R;
import alex.taran.picworld.LevelData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class FunctionsEditorActivity extends Activity {
	private LevelData levelData;
	private EditText nameView;
	private EditText mainSizeView;
	private EditText f1SizeView;
	private EditText f2SizeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_functions_editor);
		levelData = LevelData.createFromJson(getIntent().getStringExtra(
				Utility.LEVEL_TAG));

		nameView = (EditText) findViewById(R.id.textLevelName);
		mainSizeView = (EditText) findViewById(R.id.mainFunctionSize);
		f1SizeView = (EditText) findViewById(R.id.f1FunctionSize);
		f2SizeView = (EditText) findViewById(R.id.f2FunctionSize);

	}

	public void onSaveAndFinishClick(View v) {
		levelData.name = nameView.getText().toString();
		levelData.mainSize = Integer
				.parseInt(mainSizeView.getText().toString());
		levelData.f1Size = Integer.parseInt(f1SizeView.getText().toString());
		levelData.f2Size = Integer.parseInt(f2SizeView.getText().toString());
		saveLevel();
	}

	private void saveLevel() {
		try {
			Utility.getDataBase().saveLevel(FunctionsEditorActivity.this,
					levelData.name, levelData);
			Log.e(levelData.name, levelData.toJson());
			finish();
		} catch (IllegalArgumentException e) {
			AlertDialog.Builder builder1 = new AlertDialog.Builder(
					FunctionsEditorActivity.this);
			builder1.setTitle("Вы хотите пезаписать уровень?");
			builder1.setCancelable(false);
			builder1.setPositiveButton("Да",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Utility.getDataBase().eraseLevelByName(
									FunctionsEditorActivity.this,
									levelData.name);
							Utility.getDataBase().saveLevel(
									FunctionsEditorActivity.this,
									levelData.name, levelData);
							Log.e(levelData.name, levelData.toJson());
							finish();

						}
					});
			builder1.setNegativeButton("Нет",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			builder1.create().show();
		}
	}
}
