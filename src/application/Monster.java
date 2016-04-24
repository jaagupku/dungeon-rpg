package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Monster extends Fighter implements Drawable {
	private int x, y;
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

	public Monster(int x, int y, int id) {
		super(names.get(id), stats.get(id)[0], stats.get(id)[1], stats.get(id)[2], stats.get(id)[3],
				stats.get(id)[4]);
		this.img = images.get(id);
		this.x = x;
		this.y = y;

	}
	
	private static void reset(){
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
			WritableImage wi = new WritableImage(sheetImg.getPixelReader(), x, y, Game.tileSize,
					Game.tileSize);
			images.add((Image) wi);
		}

	}

	@Override
	public void render(GraphicsContext gc, double offsetX, double offsetY) {
		gc.drawImage(img, x * Game.tileSize - offsetX, y * Game.tileSize - offsetY);
	}

	public void move(int dir) {
		switch (dir) {
		case World.NORTH: {
			setY(getY() - 1);
			break;
		}
		case World.SOUTH: {
			setY(getY() + 1);
			break;
		}
		case World.WEST: {
			setX(getX() - 1);
			break;
		}
		case World.EAST: {
			setX(getX() + 1);
			break;
		}
		}
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
}
