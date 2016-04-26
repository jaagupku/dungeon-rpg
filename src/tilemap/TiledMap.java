package tilemap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import game.Game;
import game.Renderable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class TiledMap implements Renderable {

	private int width, height, layersAmount;
	private List<Image> tileSheet;
	private List<Node> objects;
	private TileLayer collisionLayer;
	private TileLayer[] layers;
	// private byte[][] collisionLayer;
	// private byte[][][] layerTiles; // layers[x][y][layerId]

	public TiledMap(File file)
			throws ParserConfigurationException, SAXException, IOException, TiledMapEncodingException {
		tileSheet = new ArrayList<Image>();
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		Node root = doc.getDocumentElement();
		width = Integer.parseInt(((Element) root).getAttribute("width"));
		height = Integer.parseInt(((Element) root).getAttribute("height"));
		objects = new ArrayList<Node>();
		Game.tileSize = Integer.parseInt(((Element) root).getAttribute("tilewidth"));
		NodeList rootChilds = root.getChildNodes();
		ArrayList<Node> tilesetNodes = new ArrayList<Node>();
		ArrayList<Node> layerNodes = new ArrayList<Node>();
		ArrayList<Node> objectGroupNodes = new ArrayList<Node>();
		ArrayList<Node> mapProperties = new ArrayList<Node>();
		for (int i = 0; i < rootChilds.getLength(); i++) {
			Node node = rootChilds.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			String nodeName = node.getNodeName();
			switch (nodeName) {
			case "tileset":
				tilesetNodes.add(node);
				break;
			case "layer":
				layerNodes.add(node);
				break;
			case "objectgroup":
				objectGroupNodes.add(node);
				break;
			case "properties":
				mapProperties.add(node);
				break;
			}
		}
		layers = new TileLayer[layerNodes.size()];
		loadTileSets(tilesetNodes);
		loadLayers(layerNodes);
		loadObjects(objectGroupNodes);

	}

	private void loadObjects(List<Node> objectGroupNodes) {
		for (Node n : objectGroupNodes) {
			NodeList nList = n.getChildNodes();
			for (int i = 0; i < nList.getLength(); i++) {
				if (nList.item(i).getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				objects.add(nList.item(i));
			}
		}

	}

	private void loadTileSets(List<Node> sheets) {
		for (Node n : sheets) {
			Element e = (Element) n;
			if (e.getAttribute("name").equals("markers"))
				continue;
			int tileCount = Integer.parseInt(e.getAttribute("tilecount"));
			int columns = Integer.parseInt(e.getAttribute("columns"));
			Element imageData = (Element) n.getChildNodes().item(1);
			String sheetPath = imageData.getAttribute("source").replace("../", "");
			Image imgSheet = new Image(sheetPath);
			for (int tCount = 0; tCount < tileCount; tCount++) {
				int x = tCount % columns * Game.tileSize;
				int y = tCount / columns * Game.tileSize;
				WritableImage wi = new WritableImage(imgSheet.getPixelReader(), x, y, Game.tileSize, Game.tileSize);
				tileSheet.add(wi);
			}
		}
	}

	private void loadLayers(List<Node> layers) throws TiledMapEncodingException {
		int layerCounter = 0;
		for (Node n : layers) {
			this.layers[layerCounter] = new TileLayer(n);
			if (((Element) n).getAttribute("name").equals("collision"))
				collisionLayer = this.layers[layerCounter];
			this.layers[layerCounter].renderTilesOnImage(tileSheet);
			layerCounter++;
		}
		layersAmount = layerCounter;
	}

	public List<Node> getObjects() {
		return objects;
	}

	public void render(GraphicsContext gc, double offsetX, double offsetY, int layer) {
		layers[layer].render(gc, offsetX, offsetY);
	}

	public void render(GraphicsContext gc, double offsetX, double offsetY, int from, int to) {
		for (int layer = from; layer < to; layer++)
			layers[layer].render(gc, offsetX, offsetY);
	}

	public void render(GraphicsContext gc, double offsetX, double offsetY) {
		for (TileLayer tl : layers) {
			tl.render(gc, offsetX, offsetY);
		}
	}

	public boolean isEmpty(int x, int y) {
		return collisionLayer.getTile(x, y) == -1;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
