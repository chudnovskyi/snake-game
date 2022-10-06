package application;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainFX extends Application {
	private static Controller controller;

	private static final String FXML_FILE = "MainScene.fxml";
	private static final String SNAKE_FILE = "Snake.txt";
	private static final String SCOREBOARD_FILE = "Scoreboard.txt";

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		stage.setTitle("Snake");
		Image image = new Image("icon.png");
		stage.getIcons().add(image);
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED); // removing minimize, maximize and close buttons

		controllerInizialization(loader);
		keyEventInizialization(scene);
		machinesInizialization();
		snakeInizialization();

		stage.setScene(scene);
		stage.show();
	}

	private void keyEventInizialization(Scene scene) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case UP: controller.up(null); break;
				case RIGHT: controller.right(null); break;
				case LEFT: controller.left(null); break;
				case DOWN: controller.down(null); break;
				case W: controller.up(null); break;
				case D:	controller.right(null); break;
				case A:	controller.left(null); break;
				case S:	controller.down(null); break;
				default: break;
				}
			}
		});
	}

	private void controllerInizialization(FXMLLoader loader) {
		controller = loader.getController();
		controller.inizializeCheckBoxesMatrix();
	}

	private void machinesInizialization() {
		CheckBoxAppleMachine.instance();
		CheckBoxOnMachine.instance();
		CheckBoxOFFMachine.instance();
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

	public static String getScoreboardFile() {
		return SCOREBOARD_FILE;
	}

	public static Controller getController() {
		return controller;
	}
}
