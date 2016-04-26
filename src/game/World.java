package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import tilemap.TiledMapEncodingException;

public class World {
	public static final int NORTH = 0, SOUTH = 1, WEST = 2, EAST = 3; // Directions
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
			rooms.add(new Room(f));
			counter++;
		}
		if (rooms.size() == 0)
			throw new FileNotFoundException("resources\\rooms\\test0.tmx not found.");
		currentRoom = rooms.get(0);
		player = new Player(currentRoom.getEntranceX(), currentRoom.getEntranceY(), 100);
	}

	public void render(Canvas canvas) {
		double[] offset = getOffset(canvas.getWidth(), canvas.getHeight());
		GraphicsContext gc = canvas.getGraphicsContext2D();
		currentRoom.render(gc, player, offset[0], offset[1]);

		double xpWidth = canvas.getWidth() - 100;
		double xpHeight = 10;
		double xpX = (canvas.getWidth() - xpWidth) / 2;
		double xpY = canvas.getHeight() - xpHeight;

		int hpWidth = 250;
		int hpHeight = 25;
		double hpX = (canvas.getWidth() - hpWidth) / 2;
		double hpY = canvas.getHeight() - hpHeight - xpHeight - 2;

		String xpText = Integer.toString(player.getXp()) + "/" + Integer.toString(player.getNextXp());
		double xpTextX = canvas.getWidth() / 2;
		double xpTextY = canvas.getHeight();

		String hpText = Integer.toString(player.getHealth()) + "/" + Integer.toString(player.getMaxHealth());
		double hpTextX = hpX + hpWidth/2;
		double hpTextY = hpY;
		
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(3);
		gc.strokeRect(hpX, hpY, hpWidth, hpHeight);
		gc.setLineWidth(1);
		gc.setFill(Color.RED);
		gc.fillRect(hpX, hpY, hpWidth, hpHeight);
		gc.setFill(Color.GREEN);
		gc.fillRect(hpX, hpY, hpWidth * player.getHealth() / player.getMaxHealth(), hpHeight);

		gc.setStroke(Color.BLACK);
		gc.setLineWidth(2);
		gc.strokeRect(xpX, xpY, xpWidth, xpHeight);
		gc.setLineWidth(1);
		gc.setFill(Color.GREY);
		gc.fillRect(xpX, xpY, xpWidth, xpHeight);
		gc.setFill(Color.YELLOW);
		gc.fillRect(xpX, xpY,
				xpWidth * (player.getXp() - player.getPrevXp()) / (player.getNextXp() - player.getPrevXp()), xpHeight);

		gc.setFill(Color.DIMGREY.darker().darker().darker());
		gc.setFont(new Font("verdana", xpHeight + 2));
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.BASELINE);
		gc.fillText(xpText, xpTextX, xpTextY);
		
		gc.setTextBaseline(VPos.TOP);
		gc.fillText(hpText, hpTextX, hpTextY);
	}

	public void movePlayer(int dir) {
		if (!player.hasTurn())
			return;
		// liigutab mängijat
		player.setTurn(false);
		if (currentRoom.getFreeDirections((int) player.getX(), (int) player.getY()).contains(dir)) {

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
			monsterTurn();
		}
	}

	public void playerAttack(int dir) {
		int x = 0;
		int y = 0;
		switch (dir) {
		case World.NORTH: {
			x = (int) player.getX();
			y = (int) (player.getY() - 1);
			break;
		}
		case World.SOUTH: {
			x = (int) player.getX();
			y = (int) (player.getY() + 1);
			break;
		}
		case World.WEST: {
			x = (int) (player.getX() - 1);
			y = (int) player.getY();
			break;
		}
		case World.EAST: {
			x = (int) (player.getX() + 1);
			y = (int) player.getY();
			break;
		}
		}
		Monster m = currentRoom.getMonsterAt(x, y);
		if (m != null) {
			player.addXp(player.attackOther(m));
		}
	}

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
