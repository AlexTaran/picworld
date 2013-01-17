package alex.taran.picworld;

public class GameField {
	public enum CellLightState {
		NO_LIGHT, LIGHT_ON, LIGHT_OFF;
	}
	
	private GameField() {
		sizeX = 0;
		sizeZ = 0;
		cells = null;
	}
	
	public GameField(int sizeX, int sizeZ) {
		this.sizeX = sizeX;
		this.sizeZ = sizeZ;
		
		cells = new Cell[sizeX][];
		for (int i = 0; i < sizeX; ++i) {
			cells[i] = new Cell[sizeZ];
		}
		
		for (int i = 0; i < sizeX; ++i) {
			for (int j = 0; j < sizeZ; ++j) {
				cells[i][j] = new Cell();
			}
		}
	}
	
	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeZ() {
		return sizeZ;
	}
	
	public Cell getCellAt(int x, int z) {
		return cells[x][z];
	}
	
	public class Cell {
		private Cell() {
			height = 0;
			lightState = CellLightState.NO_LIGHT;
		}
		
		public Cell(int height, CellLightState lightState) {
			this.height = height;
			this.lightState = lightState;
		}
		
		public int getHeight() {
			return height;
		}
		
		public Cell setHeight(int height) {
			this.height = height;
			return this;
		}

		public CellLightState getLightState () {
			return lightState;
		}
		
		public Cell setLightState(CellLightState lightState) {
			this.lightState = lightState;
			return this;
		}
		
		private CellLightState lightState;
		private int height;
	}
	
	private int sizeX;
	private int sizeZ;
	private Cell[][] cells;
}
