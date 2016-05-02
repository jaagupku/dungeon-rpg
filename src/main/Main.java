package main;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.tilemap.TiledMapEncodingException;

public class Main extends Application {

	public final static int windowWidth = 1280, windowHeight = 720;

	Scene getMenuScene(Stage stage) {
		BorderPane root = new BorderPane();
		VBox menuButtons = new VBox();
		menuButtons.setSpacing(10);

		Button loadGame = new Button("Load Game");
		loadGame.setOnMouseClicked(event -> System.out.println("load game"));
		menuButtons.getChildren().add(loadGame);

		Button newGame = new Button("New Game");
		newGame.setOnMouseClicked(event -> {
			Game game;
			try {
				game = new Game();
			} catch (ParserConfigurationException | SAXException | IOException | TiledMapEncodingException e) {
				throw new RuntimeException(e.getMessage());
			}
			stage.setScene(game.getGameScene(stage, this));
		});
		menuButtons.getChildren().add(newGame);

		Button settingsButton = new Button("Settings");
		settingsButton.setOnMouseClicked(event -> System.out.println("settings"));
		menuButtons.getChildren().add(settingsButton);

		Button helpButton = new Button("Help");
		helpButton.setOnMouseClicked(event -> stage.setScene(getHelpScene(stage)));
		menuButtons.getChildren().add(helpButton);

		Button exitButton = new Button("Exit");
		exitButton.setOnMouseClicked(event -> Platform.exit());
		menuButtons.getChildren().add(exitButton);

		menuButtons.setAlignment(Pos.CENTER);
		root.setCenter(menuButtons);

		return getDefaultScene(stage, root);
	}

	private Scene getHelpScene(Stage stage) {
		BorderPane root = new BorderPane();
		VBox bottom = new VBox();
		Button backButton = new Button("Back");
		backButton.setOnMouseClicked(event -> stage.setScene(getMenuScene(stage)));
		bottom.getChildren().add(backButton);
		bottom.setAlignment(Pos.CENTER);
		root.setBottom(bottom);

		return getDefaultScene(stage, root);
	}

	private Scene getDefaultScene(Stage stage, Parent root) {
		Scene scene = new Scene(root, windowWidth, windowHeight);
		root.setId("pane");
		scene.getStylesheets().add(getClass().getResource("/menu.css").toExternalForm());
		scene.getRoot().setStyle("-fx-background-image: url('background.jpg'); -fx-background-size: " + windowWidth
				+ "px " + windowHeight + "px;");
		return scene;
	}

	@Override
	public void stop() {
		System.out.println("Application closed! Save game in this method or something");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene menu = getMenuScene(primaryStage);
		primaryStage.setResizable(false);
		primaryStage.setScene(menu);
		primaryStage.setTitle("Dungeon the RPG");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
