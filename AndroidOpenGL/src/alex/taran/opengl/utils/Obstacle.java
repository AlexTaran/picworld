package alex.taran.opengl.utils;

import java.util.ArrayList;
import java.util.List;

public class Obstacle {
	public enum ObstacleType {
		None, Wall, Swamp
	};

	public List<Polygon> polys = new ArrayList<Polygon>();
	public ObstacleType type = ObstacleType.None;
	
	public Obstacle(){
		
	}
	
	public Obstacle(ObstacleType theType){
		type = theType;
	}
}
