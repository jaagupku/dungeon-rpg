package game;

import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;

public interface Movable {

	public double getX();

	public double getY();

	DoubleProperty xProperty();

	DoubleProperty yProperty();

	void setX(double x);

	void setY(double y);

	Timeline move(int dir);
}
