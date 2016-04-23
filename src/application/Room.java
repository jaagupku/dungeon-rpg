package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Room {
	private List<Monster> monsters = new ArrayList<Monster>();
	private List<Item> items = new ArrayList<Item>();
	private List<Connection> connections = new ArrayList<Connection>();
	private int entranceX, entranceY;
	private Map map;
	private Random rng = new Random();
	private Image wall, ground;

	public Room(File file) throws FileNotFoundException {
		StringBuilder sb = new StringBuilder();
		Scanner input = new Scanner(file);
		wall = new Image("wall.png");
		ground = new Image("ground.png");
		while (input.hasNextLine()) {
			String line = input.nextLine();
			// Loeb kogu faili ühte pikka sõnesse.
			// Kui rida failis ei alga // siis see rida läheb pikka sõnesse.
			if (!line.startsWith("//"))
				sb.append(line);
		}
		input.close();
		// Teeb pikast stringist käskude kaupa sõne massiivi.
		String[] mapLines = sb.toString().split(";");
		for (String st : mapLines) {
			// Jagab käsu kaheks. command[0] on käsu nimi ja command[1] selle
			// väärtus
			String[] command = st.split("=");
			// kontrollib käsud läbi
			if (command[0].equalsIgnoreCase("map_size")) {
				// Kaardi suurusel on kaks väärtus, x ja y
				String[] values = command[1].split(","); // eraldab need
				// Loob uue kaardi vajalike suurustega
				map = new Map(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
			} else if (command[0].equalsIgnoreCase("map_tiles")) {
				// map_tiles täidab kaardi vastavate väärtustega
				StringBuilder sb1 = new StringBuilder(command[1]);
				while (sb1.indexOf("$") != -1) { // loob ruumide vahel
													// ühendused.
					int startPos = sb1.indexOf("$");
					int endPos = sb1.indexOf("$", startPos + 1);
					// kahe $ vahel on ühenduse nimi. Luuakse uus ühendus.
					connections.add(new Connection(sb1.substring(startPos + 1, endPos), startPos));
					// $nimi$ asendatakse tühja rakuga.
					sb1.replace(startPos, endPos + 1, "0");
				}
				map.loadMapFromCharArray(sb1.toString().toCharArray());
			} else if (command[0].equalsIgnoreCase("player_spawn")) {
				// player_spawn on vajalik ainult esimesel ruumil, ehk room0.txt
				// teistel vahet ei ole.
				String[] values = command[1].split(",");
				entranceX = Integer.parseInt(values[0]);
				entranceY = Integer.parseInt(values[1]);
			} else if (command[0].equalsIgnoreCase("add_monster")) {
				// add_monster lisab koletisi ruumi.
				// add_monster=x,y,koletiseID;
				String[] values = command[1].split(",");
				monsters.add(new Monster(Integer.parseInt(values[0]), Integer.parseInt(values[1]),
						Integer.parseInt(values[2])));
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

	public void render(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		for (int y = 0; y < getSizeY(); y++) {
			for (int x = 0; x < getSizeX(); x++) {
				int cell = getCell(x, y);
				if (cell == Map.WALL) {
					gc.drawImage(wall, x * Game.TILE_SIZE, y * Game.TILE_SIZE);
				} else if (cell == Map.EMPTY) {
					gc.drawImage(ground, x * Game.TILE_SIZE, y * Game.TILE_SIZE);
				}
			}
		}
		List<Drawable> drawList = new ArrayList<Drawable>(items);
		drawList.addAll(monsters);
		for (Drawable d : drawList) {
			d.render(gc);
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

	private int getStartPos(int x, int y) {
		return x * 2 + (2 * getSizeX() + 1) * y;
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

		public Connection(String name, int position) {
			super();
			this.name = name;
			// Leiame koordinaadid x ja y, sest position on ühenduse asukoht
			// sisseloetud sõnes.
			x = position % getSizeX();
			y = (position - x) / getSizeX();
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
