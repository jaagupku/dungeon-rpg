package main.hud;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import main.Game;

public class Bar {

	private double posX, posY;
	private double width, height;
	private double textX, textY;
	private Color color1, color2;
	private Color textColor;
	private Font font;
	private DoubleProperty value;
	private DoubleProperty changeableValue;
	private String text1, text2;

	public Bar(double posX, double posY, double width, double height, Color color1, Color color2) {
		super();
		this.posX = posX;
		this.posY = posY;
		textX = posX + width / 2;
		textY = posY + height / 2;
		this.width = width;
		this.height = height;
		this.color1 = color1;
		this.color2 = color2;
		textColor = Color.DIMGREY.darker().darker().darker();
		font = new Font("verdana", height - height / 50 + 1);
		value = new SimpleDoubleProperty();
		changeableValue = new SimpleDoubleProperty();
		changeableValue.addListener((obs, oldV, newV) -> {
			int time = 222;
			Timeline tl = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(value, oldV)),
					new KeyFrame(Duration.millis(time), new KeyValue(value, newV)));
			tl.setCycleCount(1);
			tl.setAutoReverse(false);
			tl.play();
		});
	}

	public void draw(GraphicsContext gc) {
		draw(gc, posX, posY);
	}

	public void draw(GraphicsContext gc, double x, double y) {
		Paint prevStroke = gc.getStroke();
		Paint prevFill = gc.getFill();
		Font prevFont = gc.getFont();
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(2);
		gc.strokeRect(x, y, width, height);
		gc.setLineWidth(1);
		gc.setFill(color2);
		gc.fillRect(x, y, width, height);
		gc.setFill(color1);
		gc.fillRect(x, y, width * value.doubleValue(), height);
		gc.setFill(textColor);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(font);
		if (text2 != null) {
			if (x < Game.getMouseX() && Game.getMouseX() < x + width && y < Game.getMouseY()
					&& Game.getMouseY() < y + height)
				gc.fillText(text2, textX, textY);
			else
				gc.fillText(text1, textX, textY);
		} else
			gc.fillText(text1, textX, textY);
		gc.setStroke(prevStroke);
		gc.setFill(prevFill);
		gc.setFont(prevFont);
	}

	public void setPosition(double x, double y) {
		posX = x;
		posY = y;
	}

	public void setDimensions(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public void setText1(String s) {
		text1 = s;
	}

	public void setText2(String s) {
		text2 = s;
	}

	public void setValue(double value) {
		if (value > 1)
			value = 1;
		changeableValue.set(value);
	}
}
