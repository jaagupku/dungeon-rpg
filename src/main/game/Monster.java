package main.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import main.Game;
import main.tilemap.TileSet;

public class Monster extends Fighter implements Renderable, Movable {

	private int destinationX, destinationY, id;
	// monsterStrings ja monsterStats on kahedimensioonilised massiivid.
	// monster______[id][tunnus]
	// monsterStrings'il on {"koletise nimi", "kahe täheline string, mis on
	// kaardil näha"}
	// monsterStats'il on {max elud, attack power, attack accuracy, defense,
	// agility}

	private static List<String> names = new ArrayList<String>();
	private static List<int[]> stats = new ArrayList<int[]>();
	private static List<Image> images = new ArrayList<Image>();
	public static List<String> codeNames = new ArrayList<String>();

	public Monster(int x, int y, int id) {
		super(names.get(id), stats.get(id)[0], stats.get(id)[1], stats.get(id)[2], stats.get(id)[3], stats.get(id)[4]);
		this.id = id;
		setX(x);
		setY(y);
	}

	private static void reset() {
		names.clear();
		stats.clear();
		images.clear();
	}

	public static void loadMonstersFromFile(File f) throws FileNotFoundException {
		reset();
		Scanner sc;
		sc = new Scanner(f);
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (line.startsWith("//") || line.length() < 14)
				continue;
			String[] data = line.split(",");
			names.add(data[0]);
			codeNames.add(data[1]);
			int[] stat = { Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]),
					Integer.parseInt(data[5]), Integer.parseInt(data[6]) };
			stats.add(stat);
		}
		sc.close();
	}

	public static void loadMonsterImages(double scale, int tileSize) {
		images = TileSet.loadImagesFromTilesheet("images\\monster_sheet.png", names.size(), 4, tileSize, scale);
	}

	@Override
	public void render(GraphicsContext gc, double offsetX, double offsetY, int tileSize) {
		gc.drawImage(images.get(id), getX() * tileSize - offsetX, getY() * tileSize - offsetY);
	}

	@Override
	public Timeline move(Direction dir) {
		double oldX = getX();
		double oldY = getY();
		Point2D newCoords = Direction.getCoordinates(dir, new Point2D(oldX, oldY));
		double newX = newCoords.getX(), newY = newCoords.getY();
		setDestinationX((int) newX);
		setDestinationY((int) newY);

		Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(xProperty(), oldX), new KeyValue(yProperty(), oldY)),
				new KeyFrame(Duration.millis(Game.MOVE_TIME * .7), new KeyValue(xProperty(), newX),
						new KeyValue(yProperty(), newY)));
		timeline.setAutoReverse(false);
		timeline.setCycleCount(1);
		return timeline;
	}

	public void setX(double x) {
		super.setX(x);
		destinationX = (int) x;
	}

	public void setY(double y) {
		super.setY(y);
		destinationY = (int) y;
	}

	public int getDestinationX() {
		return destinationX;
	}

	public int getDestinationY() {
		return destinationY;
	}

	public void setDestinationX(int destinationX) {
		this.destinationX = destinationX;
	}

	public void setDestinationY(int destinationY) {
		this.destinationY = destinationY;
	}

	public void save(DataOutputStream dos) throws IOException {
		dos.writeInt(id);
		dos.writeInt(xProperty().intValue());
		dos.writeInt(yProperty().intValue());
		dos.writeInt(getHealth());
	}

	public static Monster load(DataInputStream dis) throws IOException {
		int id = dis.readInt();
		int x = dis.readInt();
		int y = dis.readInt();
		int hp = dis.readInt();
		Monster m = new Monster(x, y, id);
		m.setHealth(hp);
		return m;
	}

	public void drawHud(GraphicsContext gc, Font f, int tileSize, double scale, double offset, double offset2) {

		if (Game.getMouseX() > getX() * tileSize - offset && Game.getMouseX() < getX() * tileSize - offset + tileSize
				&& Game.getMouseY() > getY() * tileSize - offset2
				&& Game.getMouseY() < getY() * tileSize - offset2 + tileSize) {
			gc.setFont(f);
			gc.setTextAlign(TextAlignment.LEFT);
			gc.setTextBaseline(VPos.TOP);
			gc.setFill(Color.WHITE);
			gc.fillText(getName(), Game.getMouseX() + 5 * scale, Game.getMouseY() + 7 * scale);

		}

	}

}
