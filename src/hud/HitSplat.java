package hud;

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
	private long displayUntil;

	public HitSplat(String text, double x, double y, long displayUntil) {
		super();
		this.text = text;
		xProperty = new SimpleDoubleProperty(x);
		yProperty = new SimpleDoubleProperty(y);
		this.displayUntil = displayUntil+900_000_000;
		text = text.toUpperCase();
		if (text.equals("BLOCKED")) {
			textColor = Color.ROYALBLUE;
			font = Font.font("verdana", FontWeight.EXTRA_BOLD, 20);
		} else if (text.equals("DODGED")) {
			textColor = Color.CORAL;
			font = Font.font("verdana", FontPosture.ITALIC, 20);
		} else {
			textColor = Color.RED;
			font = Font.font("verdana", FontWeight.BOLD, 22);
		}
		
		Timeline tl = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(xProperty, x), new KeyValue(yProperty, y)),
				new KeyFrame(Duration.millis(770), new KeyValue(xProperty, x+7), new KeyValue(yProperty, y-30)));
		tl.setAutoReverse(false);
		tl.setCycleCount(1);
		tl.play();
	}

	public long getDisplayUntilTime() {
		return displayUntil;
	}

	public void draw(GraphicsContext gc, double offsetX, double offsetY) {
		Paint prevFill = gc.getFill();
		Font prevFont = gc.getFont();
		gc.setFont(font);
		gc.setFill(textColor);
		gc.fillText(text, xProperty.doubleValue()-offsetX, yProperty.doubleValue()-offsetY);
		gc.setFill(prevFill);
		gc.setFont(prevFont);
	}
}
