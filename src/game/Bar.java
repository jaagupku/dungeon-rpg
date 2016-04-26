package game;

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

public class Bar {

	private double posX, posY;
	private double width, height;
	private double textX, textY;
	private Color color1, color2;
	private Color textColor;
	private Font font;
	private DoubleProperty value;
	private DoubleProperty changeableValue;
	private String text;

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
		font = new Font("verdana", height - height/20);
		value = new SimpleDoubleProperty();
		changeableValue = new SimpleDoubleProperty();
		changeableValue.addListener((obs, oldV, newV) -> {
			int time;
			if (Game.moveTime * 2 + Game.TURN_DELAY < 222)
				time = Game.moveTime * 2;
			else
				time = 222;
			Timeline tl = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(value, oldV)),
					new KeyFrame(Duration.millis(time), new KeyValue(value, newV)));
			tl.setCycleCount(1);
			tl.setAutoReverse(false);
			tl.play();
		});
	}

	public void draw(GraphicsContext gc) {
		Paint prevStroke = gc.getStroke();
		Paint prevFill = gc.getFill();
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(2);
		gc.strokeRect(posX, posY, width, height);
		gc.setLineWidth(1);
		gc.setFill(color2);
		gc.fillRect(posX, posY, width, height);
		gc.setFill(color1);
		gc.fillRect(posX, posY, width * value.doubleValue(), height);
		gc.setFill(textColor);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(font);
		gc.fillText(text, textX, textY);
		gc.setStroke(prevStroke);
		gc.setFill(prevFill);
	}

	public void setPosition(double x, double y) {
		posX = x;
		posY = y;
	}

	public void setDimensions(double width, double height) {
		this.width = width;
		this.height = height;
	}
	
	public void setText(String s){
		text = s;
	}

	public void setValue(double value){
		System.out.println(value);
		changeableValue.set(value);
	}
}
