package hud;

import game.Game;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class HitSplat {

	private String text;
	private DoubleProperty xProperty, yProperty;
	private Color textColor;
	private Font font;
	private boolean delete;
	private int timeMove;
	private int timeStay;
	private int timeVisible;
	private Timeline tl;

	public HitSplat(String text, double x, double y) {
		super();
		this.text = text;
		xProperty = new SimpleDoubleProperty(x);
		yProperty = new SimpleDoubleProperty(y);
		delete = false;
		text = text.toUpperCase();
		if (text.equals("BLOCKED")) {
			textColor = Color.ROYALBLUE;
			font = Font.font("verdana", FontWeight.EXTRA_BOLD, 20 * Game.scale);
		} else if (text.equals("DODGED")) {
			textColor = Color.CORAL;
			font = Font.font("verdana", FontPosture.ITALIC, 20 * Game.scale);
		} else {
			textColor = Color.RED;
			font = Font.font("verdana", FontWeight.BOLD, 22 * Game.scale);
		}
		timeVisible = 1200;
		timeMove = 675;
		timeStay = timeVisible - timeMove;
		double moveYdir = -(30d / 48) * Game.tileSize;
		double moveXdir = (7d / 48) * Game.tileSize;
		tl = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(xProperty, x), new KeyValue(yProperty, y)),
				new KeyFrame(Duration.millis(timeMove), new KeyValue(xProperty, x + moveXdir),
						new KeyValue(yProperty, y + moveYdir)),
				new KeyFrame(Duration.millis(timeMove + timeStay)));
		tl.setOnFinished(ae -> delete = true);
		tl.setAutoReverse(false);
		tl.setCycleCount(1);
		tl.play();
	}

	public boolean delete() {
		return delete;
	}

	public void draw(GraphicsContext gc, double offsetX, double offsetY) {
		Paint prevFill = gc.getFill();
		Font prevFont = gc.getFont();
		double alpha = (tl.getCurrentTime().toMillis() > timeMove
				? (tl.getCurrentTime().toMillis() - timeMove) / timeStay : 0);
		gc.setGlobalAlpha(1 - alpha);
		gc.setFont(font);
		gc.setFill(textColor);
		gc.fillText(text, xProperty.doubleValue() - offsetX, yProperty.doubleValue() - offsetY);
		gc.setFill(prevFill);
		gc.setFont(prevFont);
		gc.setGlobalAlpha(1);
	}
}
