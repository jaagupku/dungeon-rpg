package main.game;

import javafx.scene.canvas.GraphicsContext;

public class Item implements Renderable {
	private int x, y;
	private String name;
	private boolean taken;
	private String img;

	public Item(int x, int y, String name, String img) {
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
		// TODO Auto-generated method stub
		
	}

}