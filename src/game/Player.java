package game;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player extends Fighter implements Renderable, Movable {
	private DoubleProperty x, y;
	private int xp, level;
	private Image img;
	private boolean hasTurn;

	public Player(int x, int y, int maxHealth) {
		super("Player", maxHealth, 10, 7, 3, 3);
		this.x = new SimpleDoubleProperty(x);
		this.y = new SimpleDoubleProperty(y);
		level = 1;
		xp = 0;
		img = new Image("player.png");
		hasTurn = true;
	}

	@Override
	public void render(GraphicsContext gc, double offsetX, double offsetY) {
		gc.drawImage(img, getX() * Game.tileSize - offsetX, getY() * Game.tileSize - offsetY);

	}

	@Override
	public double[] move(int dir) {
		double newX = getX(), newY = getY();
		switch (dir) {
		case World.NORTH: {
			newY = getY() - 1;
			break;
		}
		case World.SOUTH: {
			newY = getY() + 1;
			break;
		}
		case World.WEST: {
			newX = getX() - 1;
			break;
		}
		case World.EAST: {
			newX = getX() + 1;
			break;
		}
		}
		return new double[] { newX, newY };
	}

	public void setTurn(boolean b) {
		hasTurn = b;
	}

	public boolean hasTurn() {
		return hasTurn;
	}

	@Override
	public double getX() {
		return x.doubleValue();
	}

	@Override
	public double getY() {
		return y.doubleValue();
	}

	@Override
	public final DoubleProperty xProperty() {
		return x;
	}

	@Override
	public final DoubleProperty yProperty() {
		return y;
	}

	@Override
	public void setX(double x) {
		this.x.set(x);
		;
	}

	@Override
	public void setY(double y) {
		this.y.set(y);
		;
	}

	public void addXp(int xp) {
		this.xp += xp;
		if (this.xp >= xpToNextLevel())
			levelUp();

	}

	private int xpToNextLevel() {
		int sum = 0;
		for (int i = 1; i <= level; i++) {
			sum += (int) Math.floor(i + 30 * Math.pow(2, i / 3.7d));
		}
		return sum;
	}

	@Override
	protected void levelUp() {
		super.levelUp();
		level++;
		System.out.println("You have leveled up. You are now level " + level);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int nextlvlxp = xpToNextLevel();
		sb.append("You are level ");
		sb.append(level);
		sb.append(", you have ");
		sb.append(xp);
		sb.append("xp. Next level at ");
		sb.append(nextlvlxp);
		sb.append("xp. Experience needed for next level: ");
		sb.append(nextlvlxp - xp);
		sb.append("\nStrength: ");
		sb.append(getAttackPower());
		sb.append("\nAccuracy: ");
		sb.append(getAttackAccuracy());
		sb.append("\nDefense: ");
		sb.append(getDefense());
		sb.append("\nAgility: ");
		sb.append(getAgility());
		sb.append("\n");
		return sb.toString();
	}
}
