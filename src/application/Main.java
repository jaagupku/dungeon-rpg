package application;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {
	
	public static int windowWidth = 800, windowHeight = 600;
	
	private Scene getMenuScene(Stage stage){
		BorderPane root = new BorderPane();
		VBox menuButtons = new VBox();
		menuButtons.setSpacing(10);
		
		Button loadGame = new Button("Load Game");
		loadGame.setOnMouseClicked(event -> System.out.println("load game"));
		menuButtons.getChildren().add(loadGame);
		
		Button newGame = new Button("New Game");
		newGame.setOnMouseClicked(event -> System.out.println("new game"));
		menuButtons.getChildren().add(newGame);
		
		Button helpButton = new Button("Help");
		helpButton.setOnMouseClicked(event -> stage.setScene(getHelpScene(stage)));
		menuButtons.getChildren().add(helpButton);
		
		Button exitButton = new Button("Exit");
		exitButton.setOnMouseClicked(event -> Platform.exit());
		menuButtons.getChildren().add(exitButton);
		
		menuButtons.setAlignment(Pos.CENTER);
		root.setCenter(menuButtons);
		Scene scene = new Scene(root, windowWidth, windowHeight);
		scene.getStylesheets().add(getClass().getResource("menu.css").toExternalForm());
		return scene;
	}
	
	private Scene getHelpScene(Stage stage){
		BorderPane root = new BorderPane();
		VBox bottom = new VBox();
		Button backButton = new Button("Back");
		backButton.setOnMouseClicked(event -> stage.setScene(getMenuScene(stage)));
		bottom.getChildren().add(backButton);
		bottom.setAlignment(Pos.CENTER);
		
		root.setBottom(bottom);
		Scene scene = new Scene(root, windowWidth, windowHeight);
		scene.getStylesheets().add(getClass().getResource("menu.css").toExternalForm());
		return scene;
	}
	
	@Override
	public void stop(){
		System.out.println("Application closed! Save game in this method or something");
	}
	
	@Override
	public void start(Stage primaryStage) {
		Scene menu = getMenuScene(primaryStage);
		primaryStage.setScene(menu);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
