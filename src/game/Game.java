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
import javafx.stage.Stage;
import tilemap.TiledMapEncodingException;

public class Game {
	private World world;
	private Canvas canvas;
	public static int tileSize = 48;
	public static int moveTime = 160;
	public static final int TURN_DELAY = 25;
	private AnimationTimer timer;

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
		world = new World();
	}

	private void render() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.fillRect(0, 0, Main.windowWidth, Main.windowHeight);
		world.render(canvas);
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
				render();
			}
		};
		timer.start();
		return scene;
	}
}
