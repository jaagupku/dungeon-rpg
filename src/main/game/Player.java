package main.game;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import main.Game;
import main.hud.Bar;

public class Player extends Fighter implements Renderable, Movable {
	private int xp, level, nextXp, prevXp;
	private Image img;
	private boolean hasTurn;
	private Timeline turnDelayTimeline;
	private Bar healthBar, xpBar;

	public Player(int x, int y, int maxHealth, Image img) {
		super("Player", maxHealth, 10, 7, 3, 3);
		setX(x);
		setY(y);
		level = 1;
		xp = 0;
		prevXp = 0;
		nextXp = xpToNextLevel();
		this.img = img;
		hasTurn = true;
		turnDelayTimeline = new Timeline();

		healthProperty().addListener(cl -> updateHpBar());
	}

	void initBars(double width, double heigth) {
		double xpWidth = 0.875 * width;
		double xpHeight = 0.02 * heigth;
		double xpX = (width - xpWidth) / 2;
		double xpY = heigth - xpHeight;
		xpBar = new Bar(xpX, xpY, xpWidth, xpHeight, Color.GOLD, Color.GRAY);

		double hpWidth = 0.3125 * width;
		double hpHeight = 0.0416 * heigth;
		double hpX = (width - hpWidth) / 2;
		double hpY = heigth - hpHeight - xpHeight - 2;
		healthBar = new Bar(hpX, hpY, hpWidth, hpHeight, Color.GREEN, Color.DARKRED.brighter());

		updateHpBar();
		updateXpBar();
	}

	public void drawBars(GraphicsContext gc) {
		healthBar.draw(gc);
		xpBar.draw(gc);
	}

	@Override
	public void render(GraphicsContext gc, double offsetX, double offsetY, int tileSize) {
		gc.drawImage(img, getX() * tileSize - offsetX, getY() * tileSize - offsetY);

	}

	@Override
	public Timeline move(Direction dir) {
		double oldX = getX();
		double oldY = getY();
		Point2D newCoords = Direction.getCoordinates(dir, new Point2D(oldX, oldY));
		double newX = newCoords.getX(), newY = newCoords.getY();

		Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(xProperty(), oldX), new KeyValue(yProperty(), oldY)),
				new KeyFrame(Duration.millis(Game.MOVE_TIME * .5), new KeyValue(xProperty(), newX),
						new KeyValue(yProperty(), newY)));
		timeline.setAutoReverse(false);
		timeline.setCycleCount(1);
		return timeline;
	}

	public void setTurn(boolean b) {
		if (b) {
			turnDelayTimeline = new Timeline(
					new KeyFrame(Duration.millis(Game.TURN_DELAY + Game.MOVE_TIME), ae -> hasTurn = true));
			turnDelayTimeline.setAutoReverse(false);
			turnDelayTimeline.setCycleCount(1);
			turnDelayTimeline.play();
			return;
		}
		hasTurn = b;
	}

	public void addXp(int xp) {
		this.xp += xp;
		updateXpBar();
		if (this.xp >= nextXp)
			levelUp();

	}

	public int getLevel() {
		return level;
	}

	private int xpToNextLevel() {
		int sum = 0;
		for (int i = 1; i <= level; i++) {
			sum += (int) Math.floor(i + 30 * Math.pow(2, i / 3.7d));
		}
		return sum;
	}

	private void updateXpBar() {
		xpBar.setValue(((double) getXp() - getPrevXp()) / (getNextXp() - getPrevXp()));
		xpBar.setText1(Integer.toString(getXp()) + "/" + Integer.toString(getNextXp()));
		xpBar.setText2("Level: " + Integer.toString(getLevel()));
	}

	private void updateHpBar() {
		healthBar.setText1(Integer.toString(getHealth()) + "/" + Integer.toString(getMaxHealth()));
		healthBar.setValue(((double) getHealth()) / getMaxHealth());
	}

	@Override
	protected void levelUp() {
		super.levelUp();
		prevXp = nextXp;
		level++;
		nextXp = xpToNextLevel();
		updateXpBar();
		System.out.println("You have leveled up. You are now level " + level);
	}

	public boolean hasTurn() {
		return hasTurn;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int nextlvlxp = nextXp;
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

	public int getNextXp() {
		return nextXp;
	}

	public int getPrevXp() {
		return prevXp;
	}

	public int getXp() {
		return xp;
	}
}
