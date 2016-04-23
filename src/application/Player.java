package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player extends Fighter implements Drawable {
	private int x, y;
	private int xp, level;
	private final char SWORD = 'i', MAN = '\u263A';
	private Image img;

	public Player(int x, int y, int maxHealth) {
		super("Player", maxHealth, 10, 7, 3, 3);
		this.x = x;
		this.y = y;
		level = 1;
		xp = 0;
		img = new Image("player.png");
	}
	
	@Override
	public void render(GraphicsContext gc) {
		gc.drawImage(img, x * Game.TILE_SIZE, y * Game.TILE_SIZE);
		
	}

	public void move(int dir) {
		switch (dir) {
		case World.NORTH: {
			setY(getY() - 1);
			break;
		}
		case World.SOUTH: {
			setY(getY() + 1);
			break;
		}
		case World.WEST: {
			setX(getX() - 1);
			break;
		}
		case World.EAST: {
			setX(getX() + 1);
			break;
		}
		}
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
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
