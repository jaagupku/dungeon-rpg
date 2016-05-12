package main.game;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import main.hud.HitSplat;
import main.tilemap.IllegalTileSizeException;
import main.tilemap.TileSet;
import main.tilemap.TiledMap;
import main.tilemap.TiledMapEncodingException;

public class World {
	public static final int PLAYER_LOSE = 6, GAME_NOT_OVER = 7, PLAYER_WIN = 8;
	private Player player;
	private List<Room> rooms = new ArrayList<Room>();
	private Room currentRoom;
	private int tileSize = -1;
	private double winWidth, winHeight, scale;

	public World(double winWidth, double winHeight, double scale)
			throws ParserConfigurationException, SAXException, IOException, TiledMapEncodingException {
		int counter = 0;
		File f;
		// loob nii palju ruume, kui on kaustas "data" faile nimega
		// "room<number>.txt"
		while (true) {
			f = new File("resources\\rooms\\room" + counter + ".tmx");
			if (!f.exists())
				break;
			TiledMap tm = new TiledMap(f, scale);
			if (tileSize == -1)
				tileSize = tm.getTileSize();
			else {
				int otherTileSize = tm.getTileSize();
				if (tileSize != otherTileSize)
					throw new IllegalTileSizeException(otherTileSize, tileSize, f.getName());
			}
			rooms.add(new Room(tm));
			counter++;
		}
		if (rooms.size() == 0)
			throw new FileNotFoundException("resources\\rooms\\test0.tmx not found.");
		this.winWidth = winWidth;
		this.winHeight = winHeight;
		this.scale = scale;
		currentRoom = rooms.get(0);
		currentRoom.resumeAnimations();
		player = new Player(currentRoom.getEntranceX(), currentRoom.getEntranceY());
		player.setImage(TileSet.loadImagesFromTilesheet("images\\player.png", 1, 1, 48, scale).get(0));
		player.initBars(winWidth, winHeight);
		Monster.loadMonsterImages(scale, tileSize);
		tileSize *= scale;
		HitSplat.setScaleAndTileSize(scale, tileSize);
	}

	/**
	 * Renders world on canvas.
	 * 
	 * @param canvas
	 */
	public void render(Canvas canvas) {
		double[] offset = getOffset(canvas.getWidth(), canvas.getHeight());
		GraphicsContext gc = canvas.getGraphicsContext2D();
		currentRoom.render(gc, player, offset[0], offset[1], tileSize);

		Game.hitSplats.removeIf(elem -> elem.delete());
		Game.hitSplats.forEach(elem -> elem.draw(gc, offset[0], offset[1]));

	}

	public void drawHud(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		player.drawBars(gc);
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
			Timeline delay = new Timeline(new KeyFrame(Duration.millis(Game.MOVE_TIME * 1.3), ae -> {
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
		if (currentRoom.getWidth() * tileSize > screenWidth) {
			offsetX = player.getX() * tileSize - midX + tileSize / 2;
			if (offsetX < 0)
				offsetX = 0;
			else if (offsetX > currentRoom.getWidth() * tileSize - screenWidth) {
				offsetX = currentRoom.getWidth() * tileSize - screenWidth;
			}
		} else {
			offsetX = currentRoom.getWidth() * tileSize / 2 - screenWidth / 2;
		}

		if (currentRoom.getHeight() * tileSize > screenHeight) {
			offsetY = player.getY() * tileSize - midY + tileSize / 2;
			if (offsetY < 0)
				offsetY = 0;
			else if (offsetY > currentRoom.getHeight() * tileSize - screenHeight) {
				offsetY = currentRoom.getHeight() * tileSize - screenHeight;
			}
		} else {
			offsetY = currentRoom.getHeight() * tileSize / 2 - screenHeight / 2;
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

	public void saveGame(String path) throws IOException {
		// TODO Auto-generated method stub
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
		
		dos.writeInt(rooms.size());
		dos.writeInt(rooms.indexOf(currentRoom));
		player.save(dos);
		for(Room r: rooms){
			r.save(dos);
		}
		
		dos.close();
	}
	
	public void loadGame(String path) throws IOException{
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
		
		int roomsAmount = dis.readInt();
		if(roomsAmount != rooms.size()){
			dis.close();
			throw new IOException("Corrupted or outdated save game.");
		}
		
		int currentRoomId = dis.readInt();
		player = Player.load(dis, winWidth, winHeight);
		player.setImage(TileSet.loadImagesFromTilesheet("images\\player.png", 1, 1, 48, scale).get(0));
		currentRoom = rooms.get(currentRoomId);
		
		for(Room r: rooms){
			r.load(dis);
		}
		
		dis.close();
	}
}
