package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;

public class Monster extends Fighter implements Renderable, Movable {
	
	private int destinationX, destinationY;
	private Image img;
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
		this.img = images.get(id);
		setX(x);
		setY(y);
	}

	private static void reset() {
		names.clear();
		stats.clear();
		images.clear();
	}

	static void loadMonstersFromFile(File f) throws FileNotFoundException {
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
		Image sheetImg = new Image("objects_sheet.png");
		int sheetSizeX = (int) (sheetImg.getWidth() / Game.tileSize);
		for (int tileCounter = 0; tileCounter < names.size(); tileCounter++) {
			int x = (tileCounter % sheetSizeX) * Game.tileSize;
			int y = (tileCounter / sheetSizeX) * Game.tileSize;
			WritableImage wi = new WritableImage(sheetImg.getPixelReader(), x, y, Game.tileSize, Game.tileSize);
			images.add((Image) wi);
		}

	}

	@Override
	public void render(GraphicsContext gc, double offsetX, double offsetY) {
		gc.drawImage(img, getX() * Game.tileSize - offsetX, getY() * Game.tileSize - offsetY);
	}

	@Override
	public Timeline move(int dir) {
		double oldX = getX();
		double oldY = getY();
		double newX = getX(), newY = getY();
		switch (dir) {
		case World.NORTH: {
			newY = getY() - 1;
			break;
		}
		case World.SOUTH: {
			newY = getY() + 1;
			break;
		}
		case World.WEST: {
			newX = getX() - 1;
			break;
		}
		case World.EAST: {
			newX = getX() + 1;
			break;
		}
		}
		setDestinationX((int) newX);
		setDestinationY((int) newY);

		Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(xProperty(), oldX), new KeyValue(yProperty(), oldY)),
				new KeyFrame(Duration.millis(Game.moveTime*.7), new KeyValue(xProperty(), newX),
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

}
