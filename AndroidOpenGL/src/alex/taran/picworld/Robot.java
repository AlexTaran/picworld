package alex.taran.picworld;

public class Robot implements Cloneable {
	
	public class ImmutableRobot {
		public ImmutableRobot(Robot robot) {
			this.robot = robot;
		}

		public int getPosX() {
			return robot.getPosX();
		}
		
		public int getPosY() {
			return robot.getPosY();
		}
		
		public int getPosZ() {
			return robot.getPosZ();
		}
		
		public LookDirection getLookDirection() {
			return robot.lookDirection;
		}
		
		private final Robot robot;
	}
	
	public ImmutableRobot getImmutable() {
		return new ImmutableRobot(this);
	}
	
	public Robot() {
		setLookDirection(LookDirection.POSX);
		setPosX(0);
		setPosY(0);
		setPosZ(0);
	}
	
	public Robot clone() {
		Robot r = new Robot();
		r.setLookDirection(lookDirection);
		r.setPosX(posX);
		r.setPosY(posY);
		r.setPosZ(posZ);
		return r;
	}
	
	public LookDirection getLookDirection() {
		return lookDirection;
	}
	
	public void setLookDirection(LookDirection lookDirection) {
		this.lookDirection = lookDirection;
	}
	
	public int getPosX() {
		return posX;
	}
	
	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public int getPosZ() {
		return posZ;
	}

	public void setPosZ(int posZ) {
		this.posZ = posZ;
	}
	
	public void rotateLeft() {
		setLookDirection(getLookDirection().getRotatedLeft());
	}
	
	public void rotateRight() {
		setLookDirection(getLookDirection().getRotatedRight());
	}

	public enum LookDirection {
		POSX(1, 0),
		NEGX(-1, 0),
		POSZ(0, 1),
		NEGZ(0, -1);
		
		LookDirection(int x, int z){
			lookX = x;
			lookZ = z;
		}
		
		public int getLookX() {
			return lookX;
		}
		
		public int getLookZ() {
			return lookZ;
		}
		
		public LookDirection getRotatedLeft() {
			switch(this) {
			case POSX: return NEGZ;
			case NEGZ: return NEGX;
			case NEGX: return POSZ;
			case POSZ: return POSX;
			default:
				throw new RuntimeException("Wrong code in LookDirection.getRotatedLeft code");
			}
		}
		
		public LookDirection getRotatedRight() {
			switch(this) {
			case POSX: return POSZ;
			case NEGZ: return POSX;
			case NEGX: return NEGZ;
			case POSZ: return NEGX;
			default:
				throw new RuntimeException("Wrong code in LookDirection.getRotatedLeft code");
			}
		}
		
		private int lookX;
		private int lookZ;
	}
	
	private LookDirection lookDirection;
	private int posX;
	private int posY;
	private int posZ;
}
