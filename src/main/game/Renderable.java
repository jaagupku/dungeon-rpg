package main.game;

import javafx.scene.canvas.GraphicsContext;

public interface Renderable {
	
	public void render(GraphicsContext gc, double offsetX, double offsetY, int tileSize);
}
