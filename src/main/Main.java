package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.animation.Timeline;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import main.game.Gamestate;
import main.tilemap.TiledMapEncodingException;

/*
 * TODO ComboBox css.
 */
public class Main extends Application {

	private Settings settings;
	private Font titleFont;

	Scene getMenuScene(Stage stage) {
		BorderPane root = new BorderPane();
		VBox menuButtons = new VBox();
		menuButtons.setSpacing(10 * Math.pow(settings.getScale(), 2.5));
		
		Label title = new Label("Dungeons & Rats");
		title.setFont(titleFont);
		title.setScaleX(settings.getScale());
		title.setScaleY(settings.getScale());
		title.setTextFill(Color.WHITE);
		menuButtons.getChildren().add(title);
		
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
	
	Scene getGameOverScene(Stage stage, Gamestate state){
		BorderPane root = new BorderPane();
		VBox bottom = new VBox();
		Label endText = new Label();
		endText.setTextFill(Color.WHITE);
		endText.setFont(new Font(30));
		endText.setTextAlignment(TextAlignment.CENTER);
		switch(state){
		case PLAYER_WIN:
			endText.setText("Congratsiolations!\nYou won!\nGG WP".toUpperCase());
			break;
		case PLAYER_LOSE:
			endText.setText("You lost!\nBe more careful next time.".toUpperCase());
			break;
		default:
			endText.setText("wow");
			break;
		}
		endText.setScaleX(settings.getScale());
		endText.setScaleY(settings.getScale());
		root.setCenter(endText);
		
		Button mainMenuButton = new Button("Main menu");
		mainMenuButton.setScaleX(settings.getScale());
		mainMenuButton.setScaleY(settings.getScale());
		mainMenuButton.setOnMouseClicked(event -> setScene(stage, getMenuScene(stage)));
		bottom.getChildren().add(mainMenuButton);
		bottom.setAlignment(Pos.CENTER);
		root.setBottom(bottom);
		
		
		return getDefaultScene(stage, root);
	}

	private Scene getHelpScene(Stage stage) {
		BorderPane root = new BorderPane();
		VBox bottom = new VBox();
		Button backButton = new Button("Back");
		backButton.setOnMouseClicked(event -> setScene(stage, getMenuScene(stage)));
		bottom.getChildren().add(backButton);
		bottom.setAlignment(Pos.CENTER);

		Label helpText = new Label(
				"The goal of the game is to explore dungeons.\nButtons:\nW - to move up\nA - to move left\nS - to move down\nD - to move right.\n\nBackground music by Waterflame. Track name is \"Waterflame - Glorious Morning 2\"\nwww.youtube.com/waterflame89");
		helpText.setFont(new Font("verdana", 15 * settings.getScale()));
		helpText.setTextFill(Color.WHITE);
		helpText.setWrapText(true);
		root.setCenter(helpText);
		root.setBottom(bottom);

		return getDefaultScene(stage, root);
	}

	void setScene(Stage stage, Scene scene) {
		stage.setScene(scene);
		stage.setFullScreen(settings.isFullscreen());
	}

	private Scene getSettingsScene(Stage stage) {
		Font labelFont = new Font("verdana", 15 * settings.getScale());
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
		backButton.setScaleX(settings.getScale());
		backButton.setScaleY(settings.getScale());
		backButton.setOnMouseClicked(event -> setScene(stage, getMenuScene(stage)));
		bottom.getChildren().add(backButton);
		bottom.setAlignment(Pos.CENTER);
		root.setBottom(bottom);
		root.setCenter(center);

		Label resLabel = new Label("Resolution:");
		resLabel.setScaleX(settings.getScale());
		resLabel.setScaleY(settings.getScale());
		resLabel.setFont(labelFont);
		resLabel.setTextFill(Color.WHITE);

		ComboBox<String> resolutions = new ComboBox<String>(Settings.getResolutionLabels(settings.getAspectRatio()));
		resolutions.setScaleX(settings.getScale());
		resolutions.setScaleY(settings.getScale());
		resolutions.setPrefWidth(85*settings.getScale());
		
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
		aspectRatioLabel.setScaleX(settings.getScale());
		aspectRatioLabel.setScaleY(settings.getScale());
		aspectRatioLabel.setFont(labelFont);
		aspectRatioLabel.setTextFill(Color.WHITE);
		centLeft.getChildren().add(aspectRatioLabel);

		ComboBox<String> aspectRatios = new ComboBox<String>(Settings.getAspectRatioLabels());
		aspectRatios.setScaleX(settings.getScale());
		aspectRatios.setScaleY(settings.getScale());
		aspectRatios.setPrefWidth(85*settings.getScale());
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
		// TODO
		//System.out.println("Application closed! Save game in this method or something");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		titleFont = Font.loadFont(new FileInputStream(new File("./resources/fonts/almendra-sc-regular/AlmendraSC-Regular.ttf")), 55);
		
		String source = new File("resources/Glorious-Morning-2-.mp3").toURI().toString();
		MediaPlayer mediaPlayer = new MediaPlayer(new Media(source));
		mediaPlayer.setCycleCount(Timeline.INDEFINITE);
		mediaPlayer.setAutoPlay(true); // TODO

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
