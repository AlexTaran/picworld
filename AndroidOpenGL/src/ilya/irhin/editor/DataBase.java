package ilya.irhin.editor;

import java.util.UUID;

import alex.taran.picworld.LevelData;
import android.content.Context;

public interface DataBase {
	public UUID saveLevel(Context context, String name, LevelData levelData);

	public LevelData[] getAllLevels(Context context);

	public LevelData getLevelById(Context context, UUID id);

	public LevelData getLevelByName(Context context, String name);

	public void eraseLevelByName(Context context, String name);

}
