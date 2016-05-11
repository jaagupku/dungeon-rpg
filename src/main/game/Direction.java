package main.game;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;

public enum Direction {
	NORTH, SOUTH, WEST, EAST;

	/**
	 * Gets direction, point and returns new point.
	 * 
	 * @param dir
	 *            - direction to new coordinates
	 * @param coords
	 *            - current coordinates
	 * @return New coordinates
	 */
	public static Point2D getCoordinates(Direction dir, Point2D coords) {
		Point2D newCoords = coords;
		switch (dir) {
		case NORTH: {
			newCoords = new Point2D(coords.getX(), coords.getY() - 1);
			break;
		}
		case SOUTH: {
			newCoords = new Point2D(coords.getX(), coords.getY() + 1);
			break;
		}
		case WEST: {
			newCoords = new Point2D(coords.getX() - 1, coords.getY());
			break;
		}
		case EAST: {
			newCoords = new Point2D(coords.getX() + 1, coords.getY());
			break;
		}
		}
		return newCoords;
	}

	/**
	 * Returns list of free directions at certain tile in a room.
	 * 
	 * @param room
	 *            - a room where free directions are searched
	 * @param x
	 *            - x coordinate of a tile
	 * @param y
	 *            - y coordinate of a tile
	 * @return List of free directions at (x, y) from room.
	 */
	public static List<Direction> getFreeDirections(Room room, int x, int y) {
		List<Direction> freeDirections = new ArrayList<Direction>();
		if (room.isCellEmpty(x, y - 1))
			freeDirections.add(NORTH);
		if (room.isCellEmpty(x, y + 1))
			freeDirections.add(SOUTH);
		if (room.isCellEmpty(x - 1, y))
			freeDirections.add(WEST);
		if (room.isCellEmpty(x + 1, y))
			freeDirections.add(EAST);
		return freeDirections;
	}
}
