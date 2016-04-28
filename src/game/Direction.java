package game;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;

public enum Direction {
	NORTH, SOUTH, WEST, EAST;

	public static Point2D getCoordinates(Direction dir, Point2D coords) {
		Point2D newCoords = coords;
		switch (dir) {
		case NORTH: {
			newCoords = new Point2D(coords.getX(), coords.getY()-1);
			break;
		}
		case SOUTH: {
			newCoords = new Point2D(coords.getX(), coords.getY()+1);
			break;
		}
		case WEST: {
			newCoords = new Point2D(coords.getX()-1, coords.getY());
			break;
		}
		case EAST: {
			newCoords = new Point2D(coords.getX()+1, coords.getY());
			break;
		}
		}
		return newCoords;
	}

	public static List<Direction> getFreeDirections(Room r, int x, int y) {
		List<Direction> freeDirections = new ArrayList<Direction>();
		if (r.isCellEmpty(x, y - 1))
			freeDirections.add(NORTH);
		if (r.isCellEmpty(x, y + 1))
			freeDirections.add(SOUTH);
		if (r.isCellEmpty(x - 1, y))
			freeDirections.add(WEST);
		if (r.isCellEmpty(x + 1, y))
			freeDirections.add(EAST);
		return freeDirections;
	}
}
