package ilya.irhin.editor;

public class Utility {
	static private DataBase database = null;

	static public DataBase getDataBase() {
		return database;
	}

	static public void setDataBaseContext() {
		if (database == null) {
			database = new SQLiteDatabaseImpl();
		}
	}

	public static final String LEVEL_TAG = "level tag";
}