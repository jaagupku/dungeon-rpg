package main.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.Game;

public class Item implements Renderable {
	private int x, y;
	private String name;
	private boolean taken;
	private Image img;

	public Item(int x, int y, String name, Image img) {
		super();
		this.x = x;
		this.y = y;
		this.name = name;
		this.img = img;
		taken = false;
	}

	public void setTaken(boolean b) {
		taken = b;
	}

	public boolean isTaken() {
		return taken;
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public void render(GraphicsContext gc, double offsetX, double offsetY) {
		gc.drawImage(img, x*Game.tileSize - offsetX, y*Game.tileSize - offsetY);
		
	}

}
