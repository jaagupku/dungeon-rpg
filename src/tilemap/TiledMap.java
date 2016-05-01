package tilemap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class TiledMap implements Renderable {

	private int width, height;
	private List<Node> objects;
	private TileLayer collisionLayer;
	private TileLayer[] layers;
	private Map<String, Object> properties;
	private TileSetList tileSheet;

	public TiledMap(File file)
			throws ParserConfigurationException, SAXException, IOException, TiledMapEncodingException {
		Node root = getRootNode(file);
		width = Integer.parseInt(((Element) root).getAttribute("width"));
		height = Integer.parseInt(((Element) root).getAttribute("height"));
		objects = new ArrayList<Node>();
		properties = new HashMap<>();
		Game.tileSize = Integer.parseInt(((Element) root).getAttribute("tilewidth"));
		loadTiledMap(root);
	}
	
	public void playAnimations(){
		tileSheet.playAnimations();
	}
	
	public void stopAnimations(){
		tileSheet.stopAnimations();
	}

	/**
	 * Gets root node of the TMX map file.
	 * 
	 * @param file
	 *            - map file
	 * @return root node of map
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Node getRootNode(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		return doc.getDocumentElement();
	}

	/**
	 * Loads tilesets, layers, objects and map properties.
	 * 
	 * @param root
	 *            - root node of map
	 * @throws TiledMapEncodingException
	 */
	private void loadTiledMap(Node root) throws TiledMapEncodingException {
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
		loadProperties(mapProperties);
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

	private void loadProperties(List<Node> propertyNodes) {
		for (Node n : propertyNodes) {
			NodeList nList = n.getChildNodes();
			for (int i = 0; i < nList.getLength(); i++) {
				if (nList.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element e = (Element) nList.item(i);
				String variableName = e.getAttribute("name");
				String variableType = e.getAttribute("type");
				String variableValue = e.getAttribute("value");
				Object o;
				switch (variableType) {
				case "int": {
					o = new Integer(Integer.parseInt(variableValue));
					break;
				}
				case "float": {
					o = new Float(Float.parseFloat(variableValue));
					break;
				}
				case "bool": {
					o = new Boolean(Boolean.parseBoolean(variableValue));
					break;
				}
				default: {
					o = new String(variableValue);
				}
				}
				properties.put(variableName, o);
			}
		}
	}

	private void loadTileSets(List<Node> sheets) {
		tileSheet = new TileSetList(sheets);
	}

	private void loadLayers(List<Node> layers) throws TiledMapEncodingException {
		int layerCounter = 0;
		for (Node n : layers) {
			this.layers[layerCounter] = new TileLayer(n, tileSheet);
			if (((Element) n).getAttribute("name").equals("collision"))
				collisionLayer = this.layers[layerCounter];
			layerCounter++;
		}
	}

	public List<Node> getObjects() {
		return objects;
	}

	/**
	 * Renders specified layer.
	 * 
	 * @param gc
	 *            - GraphicsContext
	 * @param offsetX
	 *            - x offset
	 * @param offsetY
	 *            - y offset
	 * @param layer
	 *            - layer id
	 */
	public void render(GraphicsContext gc, double offsetX, double offsetY, int layer) {
		layers[layer].render(gc, offsetX, offsetY);
	}

	/**
	 * Renders layers in range [from, to)
	 * 
	 * @param gc
	 *            - GraphicsContext
	 * @param offsetX
	 *            - x offset
	 * @param offsetY
	 *            - y offset
	 * @param from
	 *            - Start layer id
	 * @param to
	 *            - End layer id
	 */
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
		return collisionLayer.getTile(x, y) == 0;
	}

	public final Map<String, Object> getProperties() {
		return properties;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
