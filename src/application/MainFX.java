package application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainFX extends Application {
	private static Controller controller;

	private static final String FXML_FILE = "MainScene.fxml";
	private static final String SNAKE_FILE = "Snake.txt";
	private static final String LEADERBOARD = "Leaderboard.txt";

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE));
		stage.setTitle("Snake");
		Image image = new Image("icon.png");
		stage.getIcons().add(image);
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED); // removing minimize, maximize and close buttons
		stage.setScene(new Scene(loader.load()));
		stage.show();

		controllerInizialization(loader);
		machinesInizialization();
		snakeInizialization();
	}

	private void controllerInizialization(FXMLLoader loader) {
		controller = loader.getController();
		controller.inizializeCheckBoxesMatrix();
	}

	private void machinesInizialization() {
		CheckBoxMachine.instance();
	}

	private void snakeInizialization() {
		Snake snake = null;
		try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(SNAKE_FILE))) {
			snake = (Snake) reader.readObject();
			if (snake != null) {
				Snake.setInstance(snake);
			}
		} catch (IOException | ClassNotFoundException e) {
			snake = Snake.instance();
		}
		snake.setDaemon(true);
		snake.setName("Snake");
		snake.start();
	}

	public static String getSnakeFile() {
		return SNAKE_FILE;
	}

	public static String getLeaderboard() {
		return LEADERBOARD;
	}

	public static Controller getController() {
		return controller;
	}
}
