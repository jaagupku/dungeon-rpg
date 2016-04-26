package game;

import java.io.File;
import java.io.IOException;

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
import tilemap.TiledMapEncodingException;

public class Game {
	private World world;
	private Canvas canvas;
	public static Bar healthBar, xpBar;
	public static int tileSize = 48;
	public static int moveTime = 144;
	public static final int TURN_DELAY = 25;
	private AnimationTimer timer;
	private long before = 0;
	

	public Game() throws ParserConfigurationException, SAXException, IOException, TiledMapEncodingException {
		canvas = new Canvas(Main.windowWidth, Main.windowHeight);
		canvas.setFocusTraversable(true);
		canvas.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.W) {
					world.movePlayer(World.NORTH);
				} else if (event.getCode() == KeyCode.S) {
					world.movePlayer(World.SOUTH);
				} else if (event.getCode() == KeyCode.A) {
					world.movePlayer(World.WEST);
				} else if (event.getCode() == KeyCode.D) {
					world.movePlayer(World.EAST);
				}
				event.consume();
			}
		});
		Monster.loadMonstersFromFile(new File("resources\\monsters.txt"));
		
		double xpWidth = canvas.getWidth() - 100;
		double xpHeight = 10;
		double xpX = (canvas.getWidth() - xpWidth) / 2;
		double xpY = canvas.getHeight() - xpHeight;
		xpBar = new Bar(xpX, xpY, xpWidth, xpHeight, Color.YELLOW, Color.GRAY);
		
		int hpWidth = 250;
		int hpHeight = 25;
		double hpX = (canvas.getWidth() - hpWidth) / 2;
		double hpY = canvas.getHeight() - hpHeight - xpHeight - 2;
		healthBar = new Bar(hpX, hpY, hpWidth, hpHeight, Color.GREEN, Color.RED);
		
		world = new World();
	}

	private void render(long delta) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, Main.windowWidth, Main.windowHeight);
		world.render(canvas);
		
		healthBar.draw(gc);
		xpBar.draw(gc);
		
		gc.setFill(Color.WHITE);
		double currentFps = 1_000_000_000 / delta;
		gc.setFont(new Font(14));
		gc.fillText("FPS: " + Double.toString(currentFps), 30, 30);
	}

	private void stop() {
		timer.stop();
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
