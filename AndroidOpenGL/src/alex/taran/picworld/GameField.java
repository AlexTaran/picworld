/* COPYRIGHT (C) 2012-2013 Alexander Taran. All Rights Reserved. */
/* Use of this source code is governed by a BSD-style license that can be found in the LICENSE file */
package alex.taran.picworld;

public class GameField implements Cloneable{
	public enum CellLightState {
		NO_LIGHT, LIGHT_ON, LIGHT_OFF;
		
		public CellLightState toggled() {
			switch (this) {
			case NO_LIGHT: return NO_LIGHT;
			case LIGHT_ON: return LIGHT_OFF;
			case LIGHT_OFF: return LIGHT_ON;
			default: throw new RuntimeException("Bad State of CellLightState");
			}
		}
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
	
	// Copy ctor
	public GameField(GameField other) {
		this.sizeX = other.sizeX;
		this.sizeZ = other.sizeZ;
		this.cells = new Cell[sizeX][];
		
		for (int i = 0; i < sizeX; ++i) {
			cells[i] = new Cell[sizeZ];
		}
		
		for (int i = 0; i < sizeX; ++i) {
			for (int j = 0; j < sizeZ; ++j) {
				cells[i][j] = new Cell(other.cells[i][j]);
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
		
		public Cell(Cell other) {
			this.height = other.height;
			this.lightState = other.lightState;
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
		
		public void toggleLight() {
			lightState = lightState.toggled();
		}
		
		private CellLightState lightState;
		private int height;
	}
	
	public boolean isWinning() {
		for (int i = 0; i < sizeX; ++i) {
			for (int j = 0; j < sizeZ; ++j) {
				if(cells[i][j].getLightState() == CellLightState.LIGHT_OFF){
					return false;
				}
			}
		}
		return true;
	}
	
	private int sizeX;
	private int sizeZ;
	private Cell[][] cells;
}
