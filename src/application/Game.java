package application;

import java.io.File;
import java.io.FileNotFoundException;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Game {
	private World world;
	private Canvas canvas;
	public static final int TILE_SIZE = 48;

	public Game() throws FileNotFoundException {
		canvas = new Canvas(Main.windowWidth, Main.windowHeight);
		canvas.setFocusTraversable(true);
		canvas.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
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
				world.monsterTurn();
				render();
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

	public Scene getGameScene(Stage stage) {
		Group root = new Group();
		root.getChildren().add(canvas);
		render();
		Scene scene = new Scene(root);
		return scene;
	}
}
