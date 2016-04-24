package application;

import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	protected static int windowWidth = 800, windowHeight = 600;

	private Scene getMenuScene(Stage stage) {
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
			} catch (FileNotFoundException e) {
				throw new RuntimeException();
			}
			stage.setScene(game.getGameScene(stage));
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
		//scene.heightProperty().addListener((ov, oldValue, newValue) -> windowHeight = newValue.intValue());
		//scene.widthProperty().addListener((ov, oldValue, newValue) -> windowWidth = newValue.intValue());
		scene.getStylesheets().add(getClass().getResource("menu.css").toExternalForm());
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
		// primaryStage.setMinWidth(640);
		// primaryStage.setMinHeight(480);
		primaryStage.setResizable(false);
		primaryStage.setScene(menu);
		primaryStage.setTitle("Dungeon the RPG");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
