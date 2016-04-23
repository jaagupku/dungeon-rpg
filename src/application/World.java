package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class World {
	public static final int NORTH = 0, SOUTH = 1, WEST = 2, EAST = 3; // Directions
	public static final int PLAYER_LOSE = 6, GAME_NOT_OVER = 7, PLAYER_WIN = 8;
	private Player player;
	private List<Room> rooms = new ArrayList<Room>();
	private Room currentRoom;

	public World() throws FileNotFoundException {
		int counter = 0;
		File f;
		// loob nii palju ruume, kui on kaustas "data" faile nimega
		// "room<number>.txt"
		while (true) {
			f = new File("resources\\rooms\\room" + counter + ".txt");
			if (!f.exists())
				break;
			rooms.add(new Room(f));
			counter++;
		}
		currentRoom = rooms.get(0);
		player = new Player(currentRoom.getEntranceX(), currentRoom.getEntranceY(), 100);
	}

	public void playerAttack(int dir) {
		int x = 0;
		int y = 0;
		switch (dir) {
		case World.NORTH: {
			x = player.getX();
			y = player.getY() - 1;
			break;
		}
		case World.SOUTH: {
			x = player.getX();
			y = player.getY() + 1;
			break;
		}
		case World.WEST: {
			x = player.getX() - 1;
			y = player.getY();
			break;
		}
		case World.EAST: {
			x = player.getX() + 1;
			y = player.getY();
			break;
		}
		}
		Monster m = currentRoom.getMonsterAt(x, y);
		if (m != null) {
			player.addXp(player.attackOther(m));
		} else {
			System.out.println("You swing your sword in empty air.\nIt was pointless.");
		}
	}

	public void render(Canvas canvas) {
		currentRoom.render(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		player.render(gc);
	}

	public void monsterTurn() {
		currentRoom.updateMonsters(player);
	}

	public void movePlayer(int dir) {
		// liigutab mängijat
		if (currentRoom.getFreeDirections(player.getX(), player.getY()).contains(dir)) {
			player.move(dir);
		} else {
			System.out.println("You can't walk there.");
		}
		if (player.getX() < 0 || player.getX() >= currentRoom.getSizeX() || player.getY() < 0
				|| player.getY() >= currentRoom.getSizeY()) {
			// Kui mängija liikus kaardist välja, siis see tähendab, et ta peab
			// minema uute ruumi
			// Leiame selle ja vahetame ruumid ära.
			Room nextRoom = currentRoom.getNextRoom(player.getX(), player.getY(), rooms);
			if (nextRoom != null)
				currentRoom = nextRoom;
			player.setX(currentRoom.getEntranceX());
			player.setY(currentRoom.getEntranceY());
		}
	}

	public void showPlayerLevels() {
		System.out.println(player.toString());
	}

	public int getGameState() {
		int nMonsters = 0;
		for (Room r : rooms) {
			nMonsters += r.getNumberOfMonsters();
		}
		if (nMonsters == 0)
			return PLAYER_WIN;
		if (player.getHealth() <= 0)
			return PLAYER_LOSE;
		// TODO Game can't be won.
		return GAME_NOT_OVER;
	}
}
