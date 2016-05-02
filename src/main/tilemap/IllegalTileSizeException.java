package main.tilemap;

public class IllegalTileSizeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8443456541244946038L;
	private int tileSize;
	private String fileName;
	
	public IllegalTileSizeException(int tileSize, int excepted, String fileName){
		super("Illegal tile size: " + tileSize + ", excepted: " + excepted + " at file " + fileName);
		this.tileSize = tileSize;
		this.fileName = fileName;
	}

	public int getTileSize() {
		return tileSize;
	}

	public String getFileName() {
		return fileName;
	}
	
}
