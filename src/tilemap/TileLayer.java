package tilemap;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import game.Game;
import game.Renderable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class TileLayer implements Renderable{
	
	private int width, height;
	private int[][] tiles;
	private boolean visible;
	private Image renderedLayer;
	
	public TileLayer(Node node) throws TiledMapEncodingException {
		super();
		Element e = (Element) node;
		Node layerDataNode = node.getChildNodes().item(1);
		Element layerData = (Element) layerDataNode;
		String data = layerDataNode.getFirstChild().getNodeValue().trim();
		if (!layerData.getAttribute("encoding").equals("csv"))
			throw new TiledMapEncodingException(
					"Unsupported encoding: " + layerData.getAttribute("encoding") + ", please use CSV.");
		
		this.width = Integer.parseInt(e.getAttribute("width"));
		this.height = Integer.parseInt(e.getAttribute("height"));
		if(e.hasAttribute("visible")){
			if(Integer.parseInt(e.getAttribute("visible")) == 1)
				visible = true;
			else
				visible = false;
		} else 
			visible = true;
		tiles = new int[width][height];
		String[] layerDataTileLines = data.split("\n");
		for (int y = 0; y < layerDataTileLines.length; y++) {
			String[] temp = layerDataTileLines[y].split(",");
			for (int x = 0; x < temp.length; x++) {
				tiles[x][y] = Integer.parseInt(temp[x])-1;
			}
		}
	}

	@Override
	public void render(GraphicsContext gc, double offsetX, double offsetY){
		if(!visible)
			return;
		gc.drawImage(renderedLayer, -offsetX, -offsetY);
	}
	
	public void renderTilesOnImage(List<Image> tileSheet){
		WritableImage wi = new WritableImage(getWidth()*Game.tileSize, getHeight()*Game.tileSize);
		PixelWriter pw = wi.getPixelWriter();
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (tiles[x][y] != -1)
					pw.setPixels(x*Game.tileSize, y*Game.tileSize, Game.tileSize, Game.tileSize, tileSheet.get(tiles[x][y]).getPixelReader(), 0, 0);
			}
		}
		renderedLayer = wi;
	}
	
	public void renderEachTile(GraphicsContext gc, double offsetX, double offsetY, List<Image> tileSheet) {
		if(!visible)
			return;
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (tiles[x][y] != -1)
					gc.drawImage(tileSheet.get(tiles[x][y]), x * Game.tileSize - offsetX, y * Game.tileSize - offsetY);
			}
		}
	}
	
	public int getTile(int x, int y){
		return tiles[x][y];
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	

}
