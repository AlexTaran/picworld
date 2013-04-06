package ilya.irhin.editor;

import alex.taran.opengl.R;
import alex.taran.picworld.GameField;
import alex.taran.picworld.GameField.Cell;
import alex.taran.picworld.GameField.CellLightState;
import alex.taran.picworld.LevelData;
import alex.taran.picworld.Robot;
import alex.taran.picworld.Robot.LookDirection;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class FieldEditorActivity extends Activity {
	private static final int MAX_HEIGHT = 5;
	private DisplayMetrics metrics;
	private Button button;
	private LinearLayout layout;
	private int height;
	private int width;
	private LevelData levelData;
	private int robotX = -1;
	private int robotY = -1;
	private int robotDir = 0;
	private GameField gameField;

	private void setRightBackgroud(Button b, int x, int y) {
		if (robotX == x && robotY == y) {
			if (levelData.gameField.getCellAt(x, y).getLightState() == CellLightState.LIGHT_OFF) {
				switch (robotDir) {
				case 1:
					b.setBackgroundResource(R.drawable.bulb_north);
					break;
				case 2:
					b.setBackgroundResource(R.drawable.bulb_east);
					break;
				case 3:
					b.setBackgroundResource(R.drawable.bulb_south);
					break;
				case 4:
					b.setBackgroundResource(R.drawable.bulb_west);
					break;
				}
			} else {
				switch (robotDir) {
				case 1:
					b.setBackgroundResource(R.drawable.none_north);
					break;
				case 2:
					b.setBackgroundResource(R.drawable.none_east);
					break;
				case 3:
					b.setBackgroundResource(R.drawable.none_south);
					break;
				case 4:
					b.setBackgroundResource(R.drawable.none_west);
					break;
				}
			}
		} else {
			if (levelData.gameField.getCellAt(x, y).getLightState() == CellLightState.LIGHT_OFF) {
				b.setBackgroundResource(R.drawable.bulb_none);
			} else {
				b.setBackgroundResource(R.drawable.none_none);
			}

		}

	}

	private void createField() {
		height = levelData.gameField.getSizeX();
		width = levelData.gameField.getSizeZ();
		layout = (LinearLayout) findViewById(R.id.fieldLayout);
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int size = Math.max(40, metrics.widthPixels / width);
		params.height = size;
		params.width = size;

		for (int i = 0; i < height; i++) {
			LinearLayout tempLayout = new LinearLayout(this);
			tempLayout.setOrientation(LinearLayout.HORIZONTAL);
			for (int j = 0; j < width; j++) {
				button = new Button(this);
				button.setLayoutParams(params);
				button.setGravity(Gravity.LEFT | Gravity.TOP);
				button.setText(String.valueOf(levelData.gameField.getCellAt(i,
						j).getHeight()));
				button.setId(width * i + j);
				setRightBackgroud(button, i, j);
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final Cell cell = gameField.getCellAt(
								v.getId() / width, v.getId() % width);
						int new_height = (cell.getHeight() + 1)
								% (MAX_HEIGHT + 1);
						((Button) v).setText(String.valueOf(new_height));
						cell.setHeight(new_height);
					}
				});
				button.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(final View v) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								FieldEditorActivity.this);
						builder.setTitle("Выберете действие");
						builder.setCancelable(false);
						builder.setNeutralButton("Назад",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
						final int x = v.getId() / width;
						final int y = v.getId() % width;
						final Cell cell = gameField.getCellAt(x, y);

						final String[] rrr = { "Убрать всё",
								"Поставить/убрать лампочку", "Робот Север",
								"Робот Восток", "Робот Юг", "Робот Запад" };
						builder.setSingleChoiceItems(rrr, -1,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int item) {
										switch (item) {
										case 0:
											cell.setLightState(CellLightState.NO_LIGHT);
											if (robotX == x && robotY == y) {
												robotX = -1;
												robotY = -1;
											}
											break;
										case 1:
											if (cell.getLightState() == CellLightState.NO_LIGHT)
												cell.setLightState(CellLightState.LIGHT_OFF);
											else
												cell.setLightState(CellLightState.NO_LIGHT);
											break;
										case 2:
										case 3:
										case 4:
										case 5:
											if (robotX != -1 && robotY != -1) {
												if (gameField.getCellAt(robotX,
														robotY).getLightState() == CellLightState.LIGHT_OFF) {
													((Button) findViewById(robotX
															* width + robotY))
															.setBackgroundResource(R.drawable.bulb_none);
												} else {
													((Button) findViewById(robotX
															* width + robotY))
															.setBackgroundResource(R.drawable.none_none);
												}
											}
											robotX = x;
											robotY = y;
											robotDir = item - 1;
											break;
										}

										setRightBackgroud((Button) v, x, y);
										dialog.cancel();
									}
								});
						builder.create().show();
						return true;
					}
				});
				tempLayout.addView(button);
			}
			layout.addView(tempLayout, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
	}

	public LevelData getLevelData() {
		if (robotX != -1 && robotY != -1) {
			Robot robot = new Robot();
			robot.setPosX(robotX);
			robot.setPosY(robotY);
			robot.setPosZ(levelData.gameField.getCellAt(robotX, robotY)
					.getHeight());
			switch (robotDir) {
			case 1:
				robot.setLookDirection(LookDirection.NEGX);
				break;
			case 2:
				robot.setLookDirection(LookDirection.POSZ);
				break;
			case 3:
				robot.setLookDirection(LookDirection.POSX);
				break;
			case 4:
				robot.setLookDirection(LookDirection.NEGZ);
				break;
			}
			levelData.initRobot = robot;
		} else {
			levelData.initRobot = null;
		}
		return levelData;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_field_editor);
		if (getIntent().hasExtra(Utility.LEVEL_TAG)) {
			levelData = LevelData.createFromJson(getIntent().getStringExtra(
					Utility.LEVEL_TAG));
			gameField = levelData.gameField;
			if (levelData.initRobot != null) {
				robotX = levelData.initRobot.getPosX();
				robotY = levelData.initRobot.getPosY();
				switch (levelData.initRobot.getLookDirection()) {
				case NEGX:
					robotDir = 1;
					break;
				case POSZ:
					robotDir = 2;
					break;
				case POSX:
					robotDir = 3;
					break;
				case NEGZ:
					robotDir = 4;
					break;
				}
			}
		} else {
			height = getIntent().getIntExtra(SizesEditorActivity.HEIGHT, 1);
			width = getIntent().getIntExtra(SizesEditorActivity.WIDTH, 1);
			gameField = new GameField(height, width);
			levelData = new LevelData(gameField, null);
		}
		createField();
	}

	public void onContinueButtonClick(View v) {
		Intent intent = new Intent(FieldEditorActivity.this,
				FunctionsEditorActivity.class);
		intent.putExtra(Utility.LEVEL_TAG, getLevelData().toJson());
		startActivity(intent);
		finish();
	}
}
