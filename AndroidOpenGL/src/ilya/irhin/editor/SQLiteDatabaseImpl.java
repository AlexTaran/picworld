package ilya.irhin.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import alex.taran.picworld.LevelData;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabaseImpl implements DataBase {
	private class QuizDataBase extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "quizDatabase.db";
		private static final int DATABASE_VERSION = 1;
		public static final String LEVEL_TABLE_NAME = "levelTable";
		public static final String LEVEL_ID = "levelId";
		public static final String LEVEL_NAME = "levelName";
		public static final String LEVEL_DESC = "levelDescribtion";

		private static final String SQL_CREATE_LEVEL_ENTRIES = "CREATE TABLE "
				+ LEVEL_TABLE_NAME + " (" + LEVEL_ID + " TEXT PRIMARY KEY , "
				+ LEVEL_NAME + " TEXT, " + LEVEL_DESC + " TEXT)";
		private static final String SQL_DELETE_LEVEL_ENTRIES = "DROP TABLE IF EXISTS "
				+ LEVEL_TABLE_NAME;

		public QuizDataBase(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(android.database.sqlite.SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_LEVEL_ENTRIES);
		}

		@Override
		public void onUpgrade(android.database.sqlite.SQLiteDatabase db,
				int oldVersion, int newVersion) {
			db.execSQL(SQL_DELETE_LEVEL_ENTRIES);
			onCreate(db);
		}

	}

	@Override
	public UUID saveLevel(Context context, String name, LevelData levelData) {
		QuizDataBase sqh = new QuizDataBase(context);
		SQLiteDatabase sqdb;
		sqdb = sqh.getReadableDatabase();
		String query = "SELECT " + QuizDataBase.LEVEL_ID + " FROM "
				+ QuizDataBase.LEVEL_TABLE_NAME + " WHERE "
				+ QuizDataBase.LEVEL_NAME + " = " + "'" + name + "'";
		Cursor cursor = sqdb.rawQuery(query, null);
		if (cursor.getCount() == 0) {
			sqdb = sqh.getWritableDatabase();
			UUID uuid = UUID.randomUUID();
			String insertQuery = "INSERT INTO " + QuizDataBase.LEVEL_TABLE_NAME
					+ " (" + QuizDataBase.LEVEL_ID + " , "
					+ QuizDataBase.LEVEL_NAME + " , " + QuizDataBase.LEVEL_DESC
					+ ") VALUES (" + "'" + uuid + "'" + " , " + "'" + name
					+ "'" + ", '" + levelData.toJson() + "')";
			sqdb.execSQL(insertQuery);
			sqdb.close();
			sqh.close();
			return uuid;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public LevelData[] getAllLevels(Context context) {
		QuizDataBase sqh = new QuizDataBase(context);
		SQLiteDatabase sqdb = sqh.getReadableDatabase();
		String query = "SELECT " + QuizDataBase.LEVEL_DESC + " FROM "
				+ QuizDataBase.LEVEL_TABLE_NAME;
		Cursor cursor = sqdb.rawQuery(query, null);
		List<LevelData> userList = new ArrayList<LevelData>();
		while (cursor.moveToNext()) {
			userList.add(LevelData.createFromJson(cursor.getString(cursor
					.getColumnIndex(QuizDataBase.LEVEL_DESC))));
		}
		cursor.close();
		sqdb.close();
		sqh.close();
		return userList.toArray(new LevelData[userList.size()]);
	}

	@Override
	public LevelData getLevelById(Context context, UUID id) {
		QuizDataBase sqh = new QuizDataBase(context);
		SQLiteDatabase sqdb = sqh.getReadableDatabase();
		String query = "SELECT " + QuizDataBase.LEVEL_DESC + " FROM "
				+ QuizDataBase.LEVEL_TABLE_NAME + " WHERE "
				+ QuizDataBase.LEVEL_ID + " = " + "'" + id + "'";
		Cursor cursor = sqdb.rawQuery(query, null);
		LevelData user = null;
		while (cursor.moveToNext()) {
			user = LevelData.createFromJson(cursor.getString(cursor
					.getColumnIndex(QuizDataBase.LEVEL_DESC)));
		}
		cursor.close();
		sqdb.close();
		sqh.close();
		return user;
	}

	@Override
	public LevelData getLevelByName(Context context, String name) {
		QuizDataBase sqh = new QuizDataBase(context);
		SQLiteDatabase sqdb = sqh.getReadableDatabase();
		String query = "SELECT " + QuizDataBase.LEVEL_DESC + " FROM "
				+ QuizDataBase.LEVEL_TABLE_NAME + " WHERE "
				+ QuizDataBase.LEVEL_NAME + " = " + "'" + name + "'";
		Cursor cursor = sqdb.rawQuery(query, null);
		LevelData user = null;
		while (cursor.moveToNext()) {
			user = LevelData.createFromJson(cursor.getString(cursor
					.getColumnIndex(QuizDataBase.LEVEL_DESC)));
		}
		cursor.close();
		sqdb.close();
		sqh.close();
		return user;
	}

	@Override
	public void eraseLevelByName(Context context, String name) {
		QuizDataBase sqh = new QuizDataBase(context);
		SQLiteDatabase sqdb = sqh.getReadableDatabase();
		sqdb.delete(QuizDataBase.LEVEL_TABLE_NAME, QuizDataBase.LEVEL_NAME
				+ "=" + "'" + name + "'", null);
		sqdb.close();
		sqh.close();
	}

}
