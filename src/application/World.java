package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class World {
	public static final int NORTH = 0, SOUTH = 1, WEST = 2, EAST = 3; // Directions
	public static final int PLAYER_LOSE = 6, GAME_NOT_OVER = 7, PLAYER_WIN = 8;
	private Player player;
	private List<Room> rooms = new ArrayList<Room>();
	private Room currentRoom;

	public World() throws ParserConfigurationException, SAXException, IOException {
		int counter = 0;
		File f;
		// loob nii palju ruume, kui on kaustas "data" faile nimega
		// "room<number>.txt"
		while (true) {
			f = new File("resources\\rooms\\room" + counter + ".tmx");
			if (!f.exists())
				break;
			rooms.add(new Room(f));
			counter++;
		}
		if (rooms.size() == 0)
			throw new FileNotFoundException("resources\\rooms\\test0.tmx not found.");
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
		}
	}

	public void render(Canvas canvas) {
		double offsetX, offsetY;
		double midX = canvas.getWidth() / 2, 
			   midY = canvas.getHeight() / 2;
		
		if (currentRoom.getWidth() * Game.tileSize > canvas.getWidth()) {
			offsetX = player.getX() * Game.tileSize - midX;
			if (offsetX < 0)
				offsetX = 0;
			else if (offsetX > currentRoom.getWidth()*Game.tileSize - canvas.getWidth()){
				offsetX = currentRoom.getWidth()*Game.tileSize - canvas.getWidth();
			}
		} else {
			offsetX = currentRoom.getWidth() * Game.tileSize / 2 - canvas.getWidth() / 2;
		}

		if (currentRoom.getHeight() * Game.tileSize > canvas.getHeight()) {
			offsetY = player.getY() * Game.tileSize - midY;
			if (offsetY < 0)
				offsetY = 0;
			else if (offsetY > currentRoom.getHeight()*Game.tileSize - canvas.getHeight()){
				offsetY = currentRoom.getHeight()*Game.tileSize - canvas.getHeight();
			}
		} else {
			offsetY = currentRoom.getHeight() * Game.tileSize / 2 - canvas.getHeight() / 2;
		}
		currentRoom.render(canvas, offsetX, offsetY);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		player.render(gc, offsetX, offsetY);
	}

	public void monsterTurn() {
		currentRoom.updateMonsters(player);
	}

	public void movePlayer(int dir) {
		// liigutab mängijat
		if (currentRoom.getFreeDirections(player.getX(), player.getY()).contains(dir)) {
			player.move(dir);
		} else {
			playerAttack(dir);
		}
		if (player.getX() < 0 || player.getX() >= currentRoom.getWidth() || player.getY() < 0
				|| player.getY() >= currentRoom.getHeight()) {
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
