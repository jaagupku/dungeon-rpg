package main.hud;

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
	private Timeline tl;

	private static double scale;
	private static int tileSize;

	private static final int TIME_MOVE = 675;
	private static final int TIME_VISIBLE = 1200;
	private static final int TIME_STAY = TIME_VISIBLE - TIME_MOVE;

	public HitSplat(String text, double x, double y) {
		super();
		this.text = text;
		x *= tileSize;
		y *= tileSize;
		xProperty = new SimpleDoubleProperty(x);
		yProperty = new SimpleDoubleProperty(y);
		delete = false;
		text = text.toUpperCase();
		if (text.equals("BLOCKED")) {
			textColor = Color.ROYALBLUE;
			font = Font.font("verdana", FontWeight.EXTRA_BOLD, 20 * scale);
		} else if (text.equals("DODGED")) {
			textColor = Color.CORAL;
			font = Font.font("verdana", FontPosture.ITALIC, 20 * scale);
		} else {
			textColor = Color.RED;
			font = Font.font("verdana", FontWeight.BOLD, 22 * scale);
		}
		double moveY = -30 * scale, moveX = 7 * scale;
		tl = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(xProperty, x), new KeyValue(yProperty, y)),
				new KeyFrame(Duration.millis(TIME_MOVE), new KeyValue(xProperty, x + moveX),
						new KeyValue(yProperty, y + moveY)),
				new KeyFrame(Duration.millis(TIME_MOVE + TIME_STAY)));
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
		double alpha = (tl.getCurrentTime().toMillis() > TIME_MOVE
				? (tl.getCurrentTime().toMillis() - TIME_MOVE) / TIME_STAY : 0);
		gc.setGlobalAlpha(1 - alpha);
		gc.setFont(font);
		gc.setFill(textColor);
		gc.fillText(text, xProperty.doubleValue() - offsetX, yProperty.doubleValue() - offsetY);
		gc.setFill(prevFill);
		gc.setFont(prevFont);
		gc.setGlobalAlpha(1);
	}

	public static void setScaleAndTileSize(double scale1, int tileSize1) {
		scale = scale1;
		tileSize = tileSize1;
	}
}
