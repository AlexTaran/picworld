package alex.taran.opengl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import alex.taran.opengl.GameUnit.UnitState;
import alex.taran.opengl.utils.MathUtils;
import android.graphics.PointF;

public class GameWorld {
	private int lastFreeId = 0;
	
	public Map<Integer,GameUnit> units = new HashMap<Integer,GameUnit>();
	public GameMap gameMap = new GameMap();
	
	public void update(float deltaTime){
		for(Entry<Integer, GameUnit> e:units.entrySet()){
			GameUnit u = e.getValue();
			if(u.state==UnitState.Attack){
				float speed = GameUnit.getSpeedByType(u.type);
				PointF dir = new PointF(u.target.x-u.position.x,u.target.y-u.position.y);
				float l = dir.length();
				if(l<=MathUtils.EPS){
					u.state=UnitState.Defence;
				}
				dir.x *=speed*deltaTime/l;
				dir.y *=speed*deltaTime/l;
				float mul = gameMap.getSpeedMultiplierAtPoint(new PointF(u.position.x+dir.x,u.position.y+dir.y));
				dir.x*=mul;
				dir.y*=mul;
				if(Math.abs(dir.x)>=Math.abs(u.target.x-u.position.x) && 
				   Math.abs(dir.y)>=Math.abs(u.target.y-u.position.y)){
					u.position.x=u.target.x;
					u.position.y=u.target.y;
					u.state=UnitState.Defence;
				}else{
					u.position.x +=dir.x;
					u.position.y +=dir.y;
				}
			}
		}
	}
	
	/*private void tryGo(Integer unitId,PointF delta){
		GameUnit u = units.get(unitId);
		PointF newPosition = new PointF(u.position.x+delta.x,u.position.y+delta.y);
		if(unitId==null){
			return;
		}
		for(Entry<Integer, GameUnit> e:units.entrySet()){
			if(e.getKey()==unitId){
				continue;
			}
			if(MathUtils.distance(e.getValue().position,newPosition)<=2.2f){
				return;
			}
		}
		u.position = newPosition;
	}*/
	public void addUnit(GameUnit u){
		units.put(lastFreeId, u);
		lastFreeId++;
	}
}
