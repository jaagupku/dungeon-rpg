package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Room {
	private List<Monster> monsters;
	private List<Item> items;
	private List<Connection> connections;
	private int entranceX, entranceY;
	private Map map;
	private Random rng = new Random();
	private Image[] tiles;

	public Room(File file) throws ParserConfigurationException, SAXException, IOException {
		monsters = new ArrayList<Monster>();
		items = new ArrayList<Item>();
		connections = new ArrayList<Connection>();
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getDocumentElement().getChildNodes();
		parseNodes(nList);

	}

	private void parseNodes(NodeList nList) {
		int firstObjGid = 0, firstConnectionGid = 0;
		for (int i = 0; i < nList.getLength(); i++) {
			Node node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (node.getNodeName().equals("tileset")) {
					if (element.getAttribute("name").equals("background")) {
						Game.tileSize = Integer.parseInt(element.getAttribute("tilewidth"));
						tiles = new Image[Integer.parseInt(element.getAttribute("tilecount"))];
						Element tileSheetElement = (Element) node.getChildNodes().item(1);
						Image sheetImg = new Image(tileSheetElement.getAttribute("source").replace("../", ""));
						int sheetSizeX = (int) (sheetImg.getWidth() / Game.tileSize);
						for (int tileCounter = 0; tileCounter < tiles.length; tileCounter++) {
							int x = (tileCounter % sheetSizeX) * Game.tileSize;
							int y = (tileCounter / sheetSizeX) * Game.tileSize;
							WritableImage wi = new WritableImage(sheetImg.getPixelReader(), x, y, Game.tileSize,
									Game.tileSize);
							tiles[tileCounter] = (Image) wi;
						}
					} else if (element.getAttribute("name").equals("objects_sheet")) {
						firstObjGid = Integer.parseInt(element.getAttribute("firstgid"));
					} else if (element.getAttribute("name").equals("markers")) {
						firstConnectionGid = Integer.parseInt(element.getAttribute("firstgid"));
					}
				} else if (node.getNodeName().equals("layer")) {
					if (element.getAttribute("name").equals("background")) {
						int width = Integer.parseInt(element.getAttribute("width"));
						int height = Integer.parseInt(element.getAttribute("height"));
						map = new Map(width, height);
						NodeList data = node.getChildNodes().item(1).getChildNodes();
						int counter = 0;
						for (int tile = 0; tile < data.getLength(); tile++) {
							if (data.item(tile).getNodeType() != Node.ELEMENT_NODE)
								continue;
							int x = counter % width;
							int y = counter / width;
							counter++;
							map.changeMapTile(x, y, Integer.parseInt(((Element) data.item(tile)).getAttribute("gid")));
						}
					}
				} else if (node.getNodeName().equals("objectgroup")) {
					if (element.getAttribute("name").equals("map_data")) {
						NodeList objects = node.getChildNodes();
						for (int obj = 0; obj < objects.getLength(); obj++) {
							if (objects.item(obj).getNodeType() != Node.ELEMENT_NODE)
								continue;
							if (Integer.parseInt(
									((Element) objects.item(obj)).getAttribute("gid")) == firstConnectionGid + 1) {
								int x = Integer.parseInt(((Element) objects.item(obj)).getAttribute("x"))
										/ Game.tileSize;
								int y = Integer.parseInt(((Element) objects.item(obj)).getAttribute("y"))
										/ Game.tileSize - 1;
								setEntranceX(x);
								setEntranceY(y);
							} else if (Integer.parseInt(
									((Element) objects.item(obj)).getAttribute("gid")) == firstConnectionGid) {

								String connectionName = ((Element) objects.item(obj)).getAttribute("name");
								int x = Integer.parseInt(((Element) objects.item(obj)).getAttribute("x"))
										/ Game.tileSize;
								int y = Integer.parseInt(((Element) objects.item(obj)).getAttribute("y"))
										/ Game.tileSize - 1;
								connections.add(new Connection(connectionName, x, y));
							}
						}
					} else if (element.getAttribute("name").equals("monsters")) {
						NodeList objects = node.getChildNodes();
						for (int obj = 0; obj < objects.getLength(); obj++) {
							if (objects.item(obj).getNodeType() != Node.ELEMENT_NODE)
								continue;
							int id = Integer.parseInt(((Element) objects.item(obj)).getAttribute("gid"))-firstObjGid;
							int x = Integer.parseInt(((Element) objects.item(obj)).getAttribute("x"))
									/ Game.tileSize;
							int y = Integer.parseInt(((Element) objects.item(obj)).getAttribute("y"))
									/ Game.tileSize - 1;
							monsters.add(new Monster(x, y, id));
						}
					}
				}
			}
		}
	}

	public boolean isCellEmpty(int x, int y) {
		if (x < 0 || x >= getSizeX() || y < 0 || y >= getSizeY()) {
			// kaardist väljaspool olev rakk on tühi, siis kui see ühendab
			// mingit teist ruumi
			for (Connection c : connections) {
				if (c.coordinatesEquals(x, y)) {
					return true;
				}
			} // kui ei ühenda, siis pole tühi
			return false;
		}
		if (getCell(x, y) == Map.WALL) {
			return false;
		}
		// koletised ka alluvad tõrjutusprintsiibile :D
		for (Monster m : monsters) {
			if (m.getX() == x && m.getY() == y) {
				return false;
			}
		}
		return true;
	}

	public void render(Canvas canvas, double sourceX, double sourceY) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		for (int y = 0; y < getSizeY(); y++) {
			for (int x = 0; x < getSizeX(); x++) {
				int cell = getCell(x, y);
				gc.drawImage(tiles[cell - 1], x * Game.tileSize + sourceX, y * Game.tileSize + sourceY);
			}
		}
		List<Drawable> drawList = new ArrayList<Drawable>(items);
		drawList.addAll(monsters);
		for (Drawable d : drawList) {
			d.render(gc, sourceX, sourceY);
		}
	}

	public Monster getMonsterAt(int x, int y) {
		for (Monster m : monsters) {
			if (m.getX() == x && m.getY() == y)
				return m;
		}
		return null;
	}

	public void updateMonsters(Player player) {
		// Eemaldab kõik surnud koletised listist.
		monsters.removeIf(monster -> monster.getHealth() < 1);
		for (Monster m : monsters) {
			double distanceFromPlayerSquared = Math.pow(m.getX() - player.getX(), 2)
					+ Math.pow(m.getY() - player.getY(), 2);
			// Kui koletis on mängijast kaugemal kui 2 ruutu, siis ta liigub
			// suvaliselt ringi.
			// Kui mitte siis seisab niisama või ründab mängijat.
			if (distanceFromPlayerSquared > 2) {
				List<Integer> freeDirections = getFreeDirections(m.getX(), m.getY());
				if (m.getY() <= 0)
					freeDirections.remove((Integer) World.NORTH);
				else if (m.getY() >= getSizeY() - 1)
					freeDirections.remove((Integer) World.SOUTH);
				if (m.getX() <= 0)
					freeDirections.remove((Integer) World.WEST);
				else if (m.getX() >= getSizeX() - 1)
					freeDirections.remove((Integer) World.EAST);
				if (freeDirections.size() > 0)
					m.move(freeDirections.get(rng.nextInt(freeDirections.size())));
			} else if (distanceFromPlayerSquared == 1) {
				m.attackOther(player);
			}
		}
	}

	public List<Integer> getFreeDirections(int x, int y) {
		List<Integer> freeDirections = new ArrayList<Integer>();
		if (isCellEmpty(x, y - 1))
			freeDirections.add(World.NORTH);
		if (isCellEmpty(x, y + 1))
			freeDirections.add(World.SOUTH);
		if (isCellEmpty(x - 1, y))
			freeDirections.add(World.WEST);
		if (isCellEmpty(x + 1, y))
			freeDirections.add(World.EAST);
		return freeDirections;
	}

	public Room getNextRoom(int x, int y, List<Room> others) {
		// getNextRoom otsib üles ühenduse ja siis tagastab teise ruumi, kus on
		// ka see sama ühenduse nimi.
		Connection connection = null;
		for (Connection c : getConnections()) {
			if (c.coordinatesEquals(x, y)) {
				connection = c; // Leidis ühenduse sellest ruumist
				break;
			}
		}
		for (Room r : others) {
			if (r == this) {
				continue; // otsime teistest ruumidest.
			}
			if (r.getConnections().contains(connection)) { // Kui selles teises
															// ruumis on otsitav
															// ühendus
				int newIndex = r.getConnections().indexOf(connection);
				// Määrame teises ruumis sissepääsu koordinaatideks selle
				// ühenduse koordinaadid.
				r.setEntranceX(r.getConnections().get(newIndex).getX());
				r.setEntranceY(r.getConnections().get(newIndex).getY());
				return r; // Tagastame uue ruumi.
			}
		}
		return null; // Kui ruumi ei leidnud siis tagastame nulli.
	}

	private List<Connection> getConnections() {
		return connections;
	}

	public int getCell(int x, int y) {
		return map.getCell(x, y);
	}

	public int getSizeX() {
		return map.getSizeX();
	}

	public int getSizeY() {
		return map.getSizeY();
	}

	public int getEntranceX() {
		return entranceX;
	}

	public int getEntranceY() {
		return entranceY;
	}

	public int getNumberOfMonsters() {
		return monsters.size();
	}

	private void setEntranceX(int entranceX) {
		if (entranceX < 0)
			this.entranceX = 0;
		else if (entranceX >= getSizeX())
			this.entranceX = getSizeX() - 1;
		else
			this.entranceX = entranceX;
	}

	private void setEntranceY(int entranceY) {
		if (entranceY < 0)
			this.entranceY = 0;
		else if (entranceY >= getSizeY())
			this.entranceY = getSizeY() - 1;
		else
			this.entranceY = entranceY;
	}

	private class Connection {
		// Ühendusel on nimi, ja koordinaadid
		private String name;
		private int x, y;

		public Connection(String name, int x, int y) {
			super();
			this.name = name;
			// Kui ühendus on kaardi ääres, siis liigutame tema kaardist
			// väljapoole.
			if (x == 0) {
				x--;
			} else if (x == getSizeX() - 1) {
				x++;
			} else if (y == 0) {
				y--;
			} else if (y == getSizeY() - 1) {
				y++;
			}
			this.x = x;
			this.y = y;
		}

		public boolean coordinatesEquals(int x, int y) {
			// Kui koordinaaadid on ühenduse omaga võrdsed
			return this.x == x && this.y == y;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Connection) {
				return ((Connection) o).getName().equals(getName());
			} else {
				return false;
			}

		}

		public String getName() {
			return name;
		}

		public int getX() {
			// Tagastab koordinaadid, mis on kaardi sees.
			if (x == -1) {
				return x + 1;
			} else if (x == getSizeX()) {
				return x - 1;
			}
			return x;
		}

		public int getY() {
			// Tagastab koordinaadid, mis on kaardi sees.
			if (y == -1) {
				return y + 1;
			} else if (y == getSizeY()) {
				return y - 1;
			}
			return y;
		}
	}
}
