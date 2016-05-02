package main.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;
import main.Game;
import main.tilemap.TiledMap;
import main.tilemap.TiledMapEncodingException;

public class World {
	public static final int PLAYER_LOSE = 6, GAME_NOT_OVER = 7, PLAYER_WIN = 8;
	private Player player;
	private List<Room> rooms = new ArrayList<Room>();
	private Room currentRoom;

	public World() throws ParserConfigurationException, SAXException, IOException, TiledMapEncodingException {
		int counter = 0;
		File f;
		// loob nii palju ruume, kui on kaustas "data" faile nimega
		// "room<number>.txt"
		while (true) {
			f = new File("resources\\rooms\\room" + counter + ".tmx");
			if (!f.exists())
				break;
			TiledMap tm = new TiledMap(f);
			rooms.add(new Room(tm));
			counter++;
		}
		if (rooms.size() == 0)
			throw new FileNotFoundException("resources\\rooms\\test0.tmx not found.");
		currentRoom = rooms.get(0);
		currentRoom.resumeAnimations();
		player = new Player(currentRoom.getEntranceX(), currentRoom.getEntranceY(), 100);
	}

	/**
	 * Renders world on canvas.
	 * 
	 * @param canvas
	 */
	public void render(Canvas canvas) {
		double[] offset = getOffset(canvas.getWidth(), canvas.getHeight());
		GraphicsContext gc = canvas.getGraphicsContext2D();
		currentRoom.render(gc, player, offset[0], offset[1]);

		Game.hitSplats.removeIf(elem -> elem.delete());
		Game.hitSplats.forEach(elem -> elem.draw(gc, offset[0], offset[1]));

	}

	public void movePlayer(Direction dir) {
		if (!player.hasTurn())
			return;
		// liigutab mängijat
		player.setTurn(false);
		if (Direction.getFreeDirections(currentRoom, (int) player.getX(), (int) player.getY()).contains(dir)) {

			Timeline timeline = player.move(dir);
			timeline.setOnFinished(event -> {
				if (player.getX() < 0 || player.getX() >= currentRoom.getWidth() || player.getY() < 0
						|| player.getY() >= currentRoom.getHeight()) {
					// Kui mängija liikus kaardist välja, siis see tähendab, et
					// ta
					// peab
					// minema uute ruumi
					// Leiame selle ja vahetame ruumid ära.
					Room nextRoom = currentRoom.getNextRoom((int) player.getX(), (int) player.getY(), rooms);
					if (nextRoom != null)
						currentRoom = nextRoom;
					player.setX(currentRoom.getEntranceX());
					player.setY(currentRoom.getEntranceY());
				}
				monsterTurn();
			});
			timeline.play();
		} else {
			playerAttack(dir);
			Timeline delay = new Timeline(new KeyFrame(Duration.millis(Game.moveTime * 1.3), ae -> {
				monsterTurn();
			}));
			delay.setCycleCount(1);
			delay.setAutoReverse(false);
			delay.play();
		}
	}

	public void playerAttack(Direction dir) {
		Point2D coords = Direction.getCoordinates(dir, new Point2D(player.getX(), player.getY()));
		Monster m = currentRoom.getMonsterAt((int) coords.getX(), (int) coords.getY());
		if (m != null) {
			player.addXp(player.attackOther(m));
		}
	}

	/**
	 * Calculates offset from screen and player.
	 * 
	 * @param screenWidth
	 *            - Width of the canvas
	 * @param screenHeight
	 *            - Height of the canvas
	 * @return {offsetX, offsetY}
	 */
	private double[] getOffset(double screenWidth, double screenHeight) {
		double offsetX, offsetY;
		double midX = screenWidth / 2, midY = screenHeight / 2;
		if (currentRoom.getWidth() * Game.tileSize > screenWidth) {
			offsetX = player.getX() * Game.tileSize - midX;
			if (offsetX < 0)
				offsetX = 0;
			else if (offsetX > currentRoom.getWidth() * Game.tileSize - screenWidth) {
				offsetX = currentRoom.getWidth() * Game.tileSize - screenWidth;
			}
		} else {
			offsetX = currentRoom.getWidth() * Game.tileSize / 2 - screenWidth / 2;
		}

		if (currentRoom.getHeight() * Game.tileSize > screenHeight) {
			offsetY = player.getY() * Game.tileSize - midY;
			if (offsetY < 0)
				offsetY = 0;
			else if (offsetY > currentRoom.getHeight() * Game.tileSize - screenHeight) {
				offsetY = currentRoom.getHeight() * Game.tileSize - screenHeight;
			}
		} else {
			offsetY = currentRoom.getHeight() * Game.tileSize / 2 - screenHeight / 2;
		}
		return new double[] { offsetX, offsetY };
	}

	public void monsterTurn() {
		currentRoom.updateMonsters(player);
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

	public void stop() {
		currentRoom.stopAnimations();

	}
}
