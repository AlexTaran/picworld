package alex.taran.opengl;

import alex.taran.opengl.utils.Obstacle;
import alex.taran.opengl.utils.Obstacle.ObstacleType;
import alex.taran.opengl.utils.Polygon;
import android.graphics.PointF;
import java.util.*;

public class GameMap {
	public PointF sizes = new PointF();
	public List<Obstacle> obstacles = new ArrayList<Obstacle>();
	
	public void genSampleMap1(){
		sizes.x = 100.0f;
		sizes.y = 100.0f;
		obstacles.clear();
		Obstacle o1 = new Obstacle(ObstacleType.Wall);
		o1.polys.add(new Polygon(10.0f,10.0f,20.0f,10.0f,20.0f,50.0f));
		o1.polys.add(new Polygon(10.0f,10.0f,20.0f,50.0f,10.0f,50.0f));
		obstacles.add(o1);
		
		Obstacle o2 = new Obstacle(ObstacleType.Wall);
		o2.polys.add(new Polygon(30.0f,60.0f,80.0f,70.0f,80.0f,85.0f));
		o2.polys.add(new Polygon(30.0f,60.0f,80.0f,85.0f,30.0f,75.0f));
		obstacles.add(o2);
		
		Obstacle o3 = new Obstacle(ObstacleType.Swamp);
		o3.polys.add(new Polygon(40.0f,20.0f,90.0f,20.0f,90.0f,50.0f));
		obstacles.add(o3);
		
		Obstacle o4 = new Obstacle(ObstacleType.Wall);
		o4.polys.add(new Polygon(0.0f,0.0f,5.0f,0.0f,0.0f,100.0f));
		o4.polys.add(new Polygon(5.0f,0.0f,0.0f,100.0f,5.0f,100.0f));
		obstacles.add(o4);
		
		Obstacle o5 = new Obstacle(ObstacleType.Wall);
		o5.polys.add(new Polygon(95.0f,0.0f,100.0f,0.0f,100.0f,100.0f));
		o5.polys.add(new Polygon(95.0f,0.0f,100.0f,100.0f,95.0f,100.0f));
		obstacles.add(o5);
		
		Obstacle o6 = new Obstacle(ObstacleType.Wall);
		o6.polys.add(new Polygon(5.0f,0.0f,95.0f,0.0f,95.0f,5.0f));
		o6.polys.add(new Polygon(5.0f,0.0f,95.0f,5.0f,5.0f,5.0f));
		obstacles.add(o6);
		
		Obstacle o7 = new Obstacle(ObstacleType.Wall);
		o7.polys.add(new Polygon(5.0f,95.0f,95.0f,95.0f,95.0f,100.0f));
		o7.polys.add(new Polygon(5.0f,95.0f,95.0f,100.0f,5.0f,100.0f));
		obstacles.add(o7);
	}

	public float getSpeedMultiplierAtPoint(PointF pt) {
		float ret = 1.0f;
		for(Obstacle o: obstacles){
			float m = getSpeedMultiplierByObstacleType(o.type);
			for(Polygon p:o.polys){
				if(p.isPointInside(pt)){
					return m;
				}
			}
		}
		return ret;
	}

	private float getSpeedMultiplierByObstacleType(ObstacleType type) {
		switch (type) {
		case Swamp: return 0.5f;	
		case Wall: return 0.0f;
		}
		return 1.0f;
	}
}
