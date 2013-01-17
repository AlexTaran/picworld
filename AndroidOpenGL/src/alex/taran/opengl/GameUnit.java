package alex.taran.opengl;

import android.graphics.PointF;

public class GameUnit {
	public enum UnitType {
		NoUnit, RedUnit, BlueUnit, GreenUnit
	};

	public enum UnitState {
		NoAction, Attack, Defence
	};

	public UnitType type = UnitType.NoUnit;
	public UnitState state = UnitState.NoAction;
	public float health;
	public PointF position;
	public PointF target;
	public int playerId;

	public GameUnit() {
		position = new PointF();
		target = new PointF();
		playerId = -1;
	}

	public GameUnit(float x, float y, UnitType theType,int thePlayerId) {
		position = new PointF(x, y);
		playerId = thePlayerId;
		type=theType;
	}
	
	public static float getSpeedByType(UnitType t ){
		switch (t) {
		case RedUnit: return 4.0f;
		case GreenUnit: return 8.0f;
		case BlueUnit: return 15.0f;
		}
		return 0.0f;
	}
}
