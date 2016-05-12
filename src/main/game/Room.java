package main.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import main.tilemap.TiledMap;
import main.tilemap.TiledMapEncodingException;

/**
 * 
 *
 */
public class Room {
	private List<Monster> monsters;
	private List<Item> items;
	private List<Connection> connections;
	private int entranceX, entranceY;
	private TiledMap map;
	private Random rng = new Random();

	/**
	 * Creates and fills new room from {@link TiledMap} object.
	 * 
	 * @param tMap
	 *            - tiledmap
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TiledMapEncodingException
	 */
	public Room(TiledMap tMap)
			throws ParserConfigurationException, SAXException, IOException, TiledMapEncodingException {
		monsters = new ArrayList<Monster>();
		items = new ArrayList<Item>();
		connections = new ArrayList<Connection>();
		map = tMap;
		List<Node> objects = map.getObjects();
		for (Node n : objects) {
			Element e = (Element) n;
			switch (e.getAttribute("type")) {
			case "monster": {
				int monsterID = Monster.codeNames.indexOf(e.getAttribute("name"));
				int[] coords = getCoordinatesFromNodeElement(e);
				monsters.add(new Monster(coords[0], coords[1], monsterID));
				break;
			}
			case "connection": {
				String connectionName = e.getAttribute("name");
				int[] coords = getCoordinatesFromNodeElement(e);
				connections.add(new Connection(connectionName, coords[0], coords[1]));
				break;
			}
			case "player_spawn": {
				int[] coords = getCoordinatesFromNodeElement(e);
				setEntranceX(coords[0]);
				setEntranceY(coords[1]);
			}
			}
		}
	}

	private int[] getCoordinatesFromNodeElement(Element e) {
		int x = (int) (Double.parseDouble(e.getAttribute("x")) / map.getTileSize());
		int y = (int) (Double.parseDouble(e.getAttribute("y")) / map.getTileSize() - 1);
		return new int[] { x, y };
	}

	/**
	 * Returns true if cell at (x, y) is empty and false if it is not.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @return boolean
	 */
	public boolean isCellEmpty(int x, int y) {
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			// kaardist väljaspool olev rakk on tühi, siis kui see ühendab
			// mingit teist ruumi
			for (Connection c : connections) {
				if (c.coordinatesEquals(x, y)) {
					return true;
				}
			} // kui ei ühenda, siis pole tühi
			return false;
		}
		if (!map.isEmpty(x, y)) {
			return false;
		}
		// koletised ka alluvad tõrjutusprintsiibile :D
		for (Monster m : monsters) {
			if ((int) m.getDestinationX() == x && (int) m.getDestinationY() == y) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Renders room using given {@link GraphicsContext}
	 * 
	 * @param gc
	 *            - GraphicsContext
	 * @param player
	 *            - player
	 * @param offsetX
	 *            - x offset of scene
	 * @param offsetY
	 *            - y offset of scene
	 */
	public void render(GraphicsContext gc, Player player, double offsetX, double offsetY, int tileSize) {
		List<Renderable> renderOrder = new ArrayList<Renderable>();

		renderOrder.add(map);
		renderOrder.addAll(items);
		renderOrder.addAll(monsters);
		renderOrder.add(player);

		renderOrder.forEach(elem -> elem.render(gc, offsetX, offsetY, tileSize));
	}

	/**
	 * Gets monster at (x, y) or null of there are no monsters.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @return Monster or null
	 */
	public Monster getMonsterAt(int x, int y) {
		for (Monster m : monsters) {
			if (m.getX() == x && m.getY() == y)
				return m;
		}
		return null;
	}

	/**
	 * Updates all monsters in the room by moving them or attacking the
	 * {@link Player}
	 * 
	 * @param player
	 *            - {@link Player}
	 */
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
				List<Direction> freeDirections = Direction.getFreeDirections(this, (int) m.getX(), (int) m.getY());
				if (m.getY() <= 0)
					freeDirections.remove(Direction.NORTH);
				else if (m.getY() >= getHeight() - 1)
					freeDirections.remove(Direction.SOUTH);
				if (m.getX() <= 0)
					freeDirections.remove(Direction.WEST);
				else if (m.getX() >= getWidth() - 1)
					freeDirections.remove(Direction.EAST);
				if (freeDirections.size() > 0) {
					Timeline timeline = m.move(freeDirections.get(rng.nextInt(freeDirections.size())));
					timeline.play();
				}
			} else if (distanceFromPlayerSquared == 1) {
				m.attackOther(player);
			}
		}
		player.setTurn(true);
	}

	/**
	 * Looks for a {@link Connection} at (x, y) coordinates. If found then
	 * searches that connection from other rooms. If found then returns that
	 * other room, else returns null.
	 * 
	 * @param x
	 *            - x coordinate of a {@link Player}
	 * @param y
	 *            - y coordinate of a {@link Player}
	 * @param others
	 *            - other {@link Room}s
	 * @return the room connected to current room.
	 */
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
				map.stopAnimations();
				r.resumeAnimations();
				return r; // Tagastame uue ruumi.
			}
		}
		return null;
	}

	void resumeAnimations() {
		map.playAnimations();
	}

	private List<Connection> getConnections() {
		return connections;
	}

	/**
	 * Gets the width of room
	 * 
	 * @return Width of the room
	 */
	public int getWidth() {
		return map.getWidth();
	}

	/**
	 * Gets the height of the room
	 * 
	 * @return height of the room
	 */
	public int getHeight() {
		return map.getHeight();
	}

	/**
	 * Gets x coordinate of the entrance to the room
	 * 
	 * @return x coordinate of the entrance
	 */
	public int getEntranceX() {
		return entranceX;
	}

	/**
	 * Gets y coordinate of the entrance to the room
	 * 
	 * @return y coordinate of the entrance
	 */
	public int getEntranceY() {
		return entranceY;
	}

	/**
	 * Gets the number of monsters in the room.
	 * 
	 * @return number of monsters
	 */
	public int getNumberOfMonsters() {
		return monsters.size();
	}

	/**
	 * Sets the entrance of room.
	 * 
	 * @param entranceX
	 *            - x coordinate
	 */
	private void setEntranceX(int entranceX) {
		if (entranceX < 0)
			this.entranceX = 0;
		else if (entranceX >= getWidth())
			this.entranceX = getWidth() - 1;
		else
			this.entranceX = entranceX;
	}

	/**
	 * Sets the entrance of room.
	 * 
	 * @param entranceY
	 *            - y coordinate
	 */
	private void setEntranceY(int entranceY) {
		if (entranceY < 0)
			this.entranceY = 0;
		else if (entranceY >= getHeight())
			this.entranceY = getHeight() - 1;
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
			} else if (x == getWidth() - 1) {
				x++;
			} else if (y == 0) {
				y--;
			} else if (y == getHeight() - 1) {
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

		/**
		 * Returns x coordinate within rooms width.
		 * 
		 * @return x coordinate
		 */
		public int getX() {
			// Tagastab koordinaadid, mis on kaardi sees.
			if (x == -1) {
				return x + 1;
			} else if (x == getWidth()) {
				return x - 1;
			}
			return x;
		}

		/**
		 * Returns y coordinate within rooms height.
		 * 
		 * @return y coordinate
		 */
		public int getY() {
			if (y == -1) {
				return y + 1;
			} else if (y == getHeight()) {
				return y - 1;
			}
			return y;
		}
	}

	public void stopAnimations() {
		map.stopAnimations();
	}

	public void save(DataOutputStream dos) throws IOException {
		dos.writeInt(monsters.size());
		for (Monster m : monsters) {
			m.save(dos);
		}
		dos.writeInt(items.size());
		for (Item i : items) {
			i.save(dos);
		}
	}

	public void load(DataInputStream dis) throws IOException {
		monsters.clear();
		items.clear();
		int nMonsters = dis.readInt();
		for (int i = 0; i < nMonsters; i++) {
			monsters.add(Monster.load(dis));
		}
		int nItems = dis.readInt();
		for (int i = 0; i < nItems; i++) {
			items.add(Item.load(dis));
		}
	}
}
