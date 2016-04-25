package game;

import javafx.beans.property.DoubleProperty;

public interface Movable {

	public double[] move(int dir);

	public double getX();

	public double getY();

	DoubleProperty xProperty();

	DoubleProperty yProperty();

	void setX(double x);

	void setY(double y);
}
