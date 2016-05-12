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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.tilemap.TiledMapEncodingException;

/*
 * TODO ComboBox css.
 */
public class Main extends Application {

	private Settings settings;

	Scene getMenuScene(Stage stage) {
		BorderPane root = new BorderPane();
		VBox menuButtons = new VBox();
		menuButtons.setSpacing(10 * Math.pow(settings.getScale(), 2.5));

		Button loadGame = new Button("Load Game");
		loadGame.setScaleX(settings.getScale());
		loadGame.setScaleY(settings.getScale());
		loadGame.setOnMouseClicked(event -> {
			try {
				Game game = Game.loadGame(settings);
				setScene(stage, game.getGameScene(stage, this));
			} catch (ParserConfigurationException | SAXException | IOException | TiledMapEncodingException e) {
				throw new RuntimeException(e);
			}
		});
		menuButtons.getChildren().add(loadGame);

		Button newGame = new Button("New Game");
		newGame.setScaleX(settings.getScale());
		newGame.setScaleY(settings.getScale());
		newGame.setOnMouseClicked(event -> {
			try {
				Game game = new Game(settings);
				setScene(stage, game.getGameScene(stage, this));
			} catch (ParserConfigurationException | SAXException | IOException | TiledMapEncodingException e) {
				throw new RuntimeException(e);
			}
		});
		menuButtons.getChildren().add(newGame);

		Button settingsButton = new Button("Settings");
		settingsButton.setScaleX(settings.getScale());
		settingsButton.setScaleY(settings.getScale());
		settingsButton.setOnMouseClicked(event -> setScene(stage, getSettingsScene(stage)));
		menuButtons.getChildren().add(settingsButton);

		Button helpButton = new Button("Help");
		helpButton.setScaleX(settings.getScale());
		helpButton.setScaleY(settings.getScale());
		helpButton.setOnMouseClicked(event -> setScene(stage, getHelpScene(stage)));
		menuButtons.getChildren().add(helpButton);

		Button exitButton = new Button("Exit");
		exitButton.setScaleX(settings.getScale());
		exitButton.setScaleY(settings.getScale());
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
		backButton.setOnMouseClicked(event -> setScene(stage, getMenuScene(stage)));
		bottom.getChildren().add(backButton);
		bottom.setAlignment(Pos.CENTER);
		root.setBottom(bottom);

		return getDefaultScene(stage, root);
	}

	private void setScene(Stage stage, Scene scene) {
		stage.setScene(scene);
		stage.setFullScreen(settings.isFullscreen());
	}

	private Scene getSettingsScene(Stage stage) {
		Font labelFont = new Font("verdana", 24 * settings.getScale());
		BorderPane root = new BorderPane();
		VBox bottom = new VBox();
		HBox center = new HBox();

		VBox centLeft = new VBox();
		centLeft.setSpacing(10 * settings.getScale());

		VBox centRight = new VBox();
		center.setAlignment(Pos.CENTER);
		centLeft.setAlignment(Pos.CENTER);
		centRight.setAlignment(Pos.CENTER);
		center.getChildren().add(centLeft);
		center.getChildren().add(centRight);

		Button backButton = new Button("Back");
		backButton.setOnMouseClicked(event -> setScene(stage, getMenuScene(stage)));
		bottom.getChildren().add(backButton);
		bottom.setAlignment(Pos.CENTER);
		root.setBottom(bottom);
		root.setCenter(center);

		Label resLabel = new Label("Resolution:");
		resLabel.setFont(labelFont);
		resLabel.setTextFill(Color.WHITE);

		ComboBox<String> resolutions = new ComboBox<String>(Settings.getResolutionLabels(settings.getAspectRatio()));
		resolutions.setValue(settings.getWinWidth() + "x" + settings.getWinHeight());
		resolutions.valueProperty().addListener((ov, s1, s2) -> {
			if (s2 != null) {
				String[] res = s2.split("x");
				int width = Integer.parseInt(res[0]);
				int height = Integer.parseInt(res[1]);
				settings.setWindowSize(width, height);
			}
		});

		Label aspectRatioLabel = new Label("Aspect ratio:");
		aspectRatioLabel.setFont(labelFont);
		aspectRatioLabel.setTextFill(Color.WHITE);
		centLeft.getChildren().add(aspectRatioLabel);

		ComboBox<String> aspectRatios = new ComboBox<String>(Settings.getAspectRatioLabels());
		aspectRatios.valueProperty().addListener((ov, s1, s2) -> {
			settings.setAspectRatio(s2);
			resolutions.setItems(Settings.getResolutionLabels(s2));
		});
		aspectRatios.setValue(settings.getAspectRatio());
		centLeft.getChildren().add(aspectRatios);

		centLeft.getChildren().add(resLabel);
		centLeft.getChildren().add(resolutions);

		// CheckBox fullscreen = new CheckBox("Fullscreen");
		// fullscreen.setTextFill(Color.WHITE);
		// fullscreen.setTextAlignment(TextAlignment.LEFT);
		// fullscreen.setFont(labelFont);
		// centLeft.getChildren().add(fullscreen);

		return getDefaultScene(stage, root);
	}

	private Scene getDefaultScene(Stage stage, Parent root) {
		Scene scene = new Scene(root, settings.getWinWidth(), settings.getWinHeight());
		root.setId("pane");
		scene.getStylesheets().add(getClass().getResource("/menu.css").toExternalForm());
		scene.getRoot().setStyle("-fx-background-image: url('images/background.jpg'); -fx-background-size: "
				+ settings.getWinWidth() + "px " + settings.getWinHeight() + "px;");
		return scene;
	}

	@Override
	public void stop() {
		System.out.println("Application closed! Save game in this method or something");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		/*
		 * BACKGROUND MUSIC String source = new
		 * File("resources/Glorious-Morning-2-.mp3").toURI().toString();
		 * MediaPlayer mediaPlayer = new MediaPlayer(new Media(source));
		 * mediaPlayer.setCycleCount(Timeline.INDEFINITE);
		 * mediaPlayer.setAutoPlay(true); // TODO
		 * mediaPlayer.setVolume(settings.getAttribute("music_volume"));
		 */
		settings = Settings.loadSettings();
		setScene(primaryStage, getMenuScene(primaryStage));
		primaryStage.setResizable(false);
		primaryStage.setTitle("Dungeon the RPG");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
