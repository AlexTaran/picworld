package alex.taran.picworld;

import com.google.gson.Gson;

public class LevelData {
	private static Gson gson = new Gson();
	
	public GameField gameField;
	public Robot initRobot;
	
	public LevelData(GameField gameField, Robot initRobot) {
		this.gameField = gameField;
		this.initRobot = initRobot;
	}
	
	public String toJson() {
		return gson.toJson(this);
	}
	
	public static LevelData createFromJson(String json) {
		return gson.fromJson(json, LevelData.class);
	}
}