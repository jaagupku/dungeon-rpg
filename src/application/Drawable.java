package application;

import javafx.scene.canvas.GraphicsContext;

public interface Drawable {
	public int getX();

	public int getY();

	public void render(GraphicsContext gc);
}
