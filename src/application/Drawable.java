package application;

import javafx.scene.canvas.GraphicsContext;

public interface Drawable {
	public int getX();

	public int getY();

	//public void render(GraphicsContext gc);

	public void render(GraphicsContext gc, double offsetX, double offsetY);
}
