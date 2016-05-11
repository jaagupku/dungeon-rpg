package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.game.Direction;
import main.game.Monster;
import main.game.World;
import main.hud.Bar;
import main.hud.HitSplat;
import main.tilemap.TiledMapEncodingException;

public class Game {
	private World world;
	private Canvas canvas;
	private AnimationTimer timer;
	private long before = 0;

	public static final List<HitSplat> hitSplats = new ArrayList<HitSplat>();;
	public static Bar healthBar, xpBar;
	public static int tileSize = -1;
	public static final int MOVE_TIME = 320;
	public static final int TURN_DELAY = 15;
	private static double mouseX = 0, mouseY = 0;

	public Game() throws ParserConfigurationException, SAXException, IOException, TiledMapEncodingException {
		canvas = new Canvas(Main.getWinWidth(), Main.getWinHeight());
		canvas.setFocusTraversable(true);
		canvas.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.W) {
					world.movePlayer(Direction.NORTH);
				} else if (event.getCode() == KeyCode.S) {
					world.movePlayer(Direction.SOUTH);
				} else if (event.getCode() == KeyCode.A) {
					world.movePlayer(Direction.WEST);
				} else if (event.getCode() == KeyCode.D) {
					world.movePlayer(Direction.EAST);
				}
				event.consume();
			}
		});
		canvas.setOnMouseMoved(mouse -> {
			mouseX = mouse.getX();
			mouseY = mouse.getY();
		});

		double xpWidth = 0.875 * canvas.getWidth();
		double xpHeight = 0.02 * canvas.getHeight();
		double xpX = (canvas.getWidth() - xpWidth) / 2;
		double xpY = canvas.getHeight() - xpHeight;
		xpBar = new Bar(xpX, xpY, xpWidth, xpHeight, Color.GOLD, Color.GRAY);

		double hpWidth = 0.3125 * canvas.getWidth();
		double hpHeight = 0.0416 * canvas.getHeight();
		double hpX = (canvas.getWidth() - hpWidth) / 2;
		double hpY = canvas.getHeight() - hpHeight - xpHeight - 2;
		healthBar = new Bar(hpX, hpY, hpWidth, hpHeight, Color.GREEN, Color.DARKRED.brighter());

		Monster.loadMonstersFromFile(new File("resources\\monsters.txt"));
		world = new World();
		Monster.loadMonsterImages();
		Game.tileSize *= Main.getScale();
	}

	private void render(long delta) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, Main.getWinWidth(), Main.getWinHeight());
		world.render(canvas);

		healthBar.draw(gc);
		xpBar.draw(gc);

		gc.setFill(Color.WHITE);
		double currentFps = 1_000_000_000 / delta;
		gc.setFont(new Font(14));
		gc.fillText("FPS: " + Double.toString(currentFps), 30, 30);
	}

	public static double getMouseX() {
		return mouseX;
	}

	public static double getMouseY() {
		return mouseY;
	}

	private void stop() {
		timer.stop();
		world.stop();
		tileSize = -1;
		world = null;
		canvas = null;
	}

	public Scene getGameScene(Stage stage, Main m) {
		Group root = new Group();
		canvas.setOnKeyReleased(value -> {
			if (value.getCode() == KeyCode.ESCAPE) {
				stop();
				stage.setScene(m.getMenuScene(stage));
			}
		});
		root.getChildren().add(canvas);
		Scene scene = new Scene(root);
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				long delta = now - before;
				render(delta);
				before = now;
			}
		};
		timer.start();
		return scene;
	}
}
