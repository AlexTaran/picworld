/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.picworld;

import com.google.gson.Gson;

public class LevelData {
	private static Gson gson = new Gson();

	public GameField gameField;
	public Robot initRobot;

	public String name;
	public int mainSize = 10;
	public int f1Size = 10;
	public int f2Size = 10;

	public LevelData(GameField gameField, Robot initRobot) {
		this.gameField = gameField;
		this.initRobot = initRobot;
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public String toString() {
		return name;
	}

	public static LevelData createFromJson(String json) {
		return gson.fromJson(json, LevelData.class);
	}
}
