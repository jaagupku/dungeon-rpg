package application;

public class Map {
	public static final int WALL = 2; // Collision tile GID;
	private int[][] cells;
	private int sizeX, sizeY;

	public Map(int sizeX, int sizeY) {
		this.sizeX = (sizeX > 3 ? sizeX : 3);
		this.sizeY = (sizeY > 3 ? sizeY : 3);
		this.cells = new int[this.sizeY][this.sizeX];
		// fillWorldRandom();
	}

	public void changeMapTile(int x, int y, int tile) {
		cells[y][x] = tile;
	}

	public void loadMapFromCharArray(char[] c) {
		for (int x = 0; x < getSizeX(); x++) {
			for (int y = 0; y < getSizeY(); y++) {
				changeMapTile(x, y, Character.getNumericValue(c[x + y * getSizeX()]));
			}
		}
	}

	/*
	 * private void fillWorldRandom() { for (int x = 0; x < sizeX; x++) { for
	 * (int y = 0; y < sizeY; y++) { if (x == 0 || x == sizeX - 1 || y == 0 || y
	 * == sizeY - 1) { changeMapTile(x, y, WALL); } else { if (rng.nextInt(4) ==
	 * 0) changeMapTile(x, y, WALL); else changeMapTile(x, y, EMPTY); } } } }
	 */

	public int getCell(int x, int y) {
		return cells[y][x];
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

}
