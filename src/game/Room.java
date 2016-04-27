package game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;
import tilemap.TiledMap;
import tilemap.TiledMapEncodingException;

public class Room {
	private List<Monster> monsters;
	private List<Item> items;
	private List<Connection> connections;
	private int entranceX, entranceY;
	private TiledMap map;
	private Random rng = new Random();

	public Room(File file) throws ParserConfigurationException, SAXException, IOException, TiledMapEncodingException {
		monsters = new ArrayList<Monster>();
		items = new ArrayList<Item>();
		connections = new ArrayList<Connection>();
		map = new TiledMap(file);
		List<Node> objects = map.getObjects();
		for (Node n : objects) {
			Element e = (Element) n;
			switch (e.getAttribute("type")) {
			case "monster": {
				int monsterID = Monster.codeNames.indexOf(e.getAttribute("name"));
				int x = (int) (Double.parseDouble(e.getAttribute("x")) / Game.tileSize);
				int y = (int) (Double.parseDouble(e.getAttribute("y")) / Game.tileSize - 1);
				monsters.add(new Monster(x, y, monsterID));
				break;
			}
			case "connection": {
				String connectionName = e.getAttribute("name");
				int x = (int) (Double.parseDouble(e.getAttribute("x")) / Game.tileSize);
				int y = (int) (Double.parseDouble(e.getAttribute("y")) / Game.tileSize - 1);
				connections.add(new Connection(connectionName, x, y));
				break;
			}
			case "player_spawn": {
				int x = (int) (Double.parseDouble(e.getAttribute("x")) / Game.tileSize);
				int y = (int) (Double.parseDouble(e.getAttribute("y")) / Game.tileSize - 1);
				setEntranceX(x);
				setEntranceY(y);
			}
			}
		}
	}

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

	public void render(GraphicsContext gc, Player player, double offsetX, double offsetY) {
		List<Renderable> renderOrder = new ArrayList<Renderable>();
		
		renderOrder.add(map);
		renderOrder.addAll(items);
		renderOrder.addAll(monsters);
		renderOrder.add(player);
		
		renderOrder.forEach(elem -> elem.render(gc, offsetX, offsetY));
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
				List<Integer> freeDirections = getFreeDirections((int) m.getX(), (int) m.getY());
				if (m.getY() <= 0)
					freeDirections.remove((Integer) World.NORTH);
				else if (m.getY() >= getHeight() - 1)
					freeDirections.remove((Integer) World.SOUTH);
				if (m.getX() <= 0)
					freeDirections.remove((Integer) World.WEST);
				else if (m.getX() >= getWidth() - 1)
					freeDirections.remove((Integer) World.EAST);
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

	public int getWidth() {
		return map.getWidth();
	}

	public int getHeight() {
		return map.getHeight();
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
		else if (entranceX >= getWidth())
			this.entranceX = getWidth() - 1;
		else
			this.entranceX = entranceX;
	}

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

		public int getX() {
			// Tagastab koordinaadid, mis on kaardi sees.
			if (x == -1) {
				return x + 1;
			} else if (x == getWidth()) {
				return x - 1;
			}
			return x;
		}

		public int getY() {
			// Tagastab koordinaadid, mis on kaardi sees.
			if (y == -1) {
				return y + 1;
			} else if (y == getHeight()) {
				return y - 1;
			}
			return y;
		}
	}
}
