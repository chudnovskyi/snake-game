package application;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import enums.Difficulties;
import enums.Directions;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.ImageInput;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller implements Initializable {
	
	@FXML private Text youLose;
	@FXML private Text currentSpeed;
	@FXML private AnchorPane scenePane;
	@FXML private ListView<String> listView;
	
	@FXML private RadioButton easyMode;
	@FXML private RadioButton hardMode;
	
	@FXML private Button upButton;
	@FXML private Button rightButton;
	@FXML private Button leftButton;
	@FXML private Button downButton;
	@FXML private Button resumeButton;
	@FXML private Button pauseButton;
	@FXML private Button restartButton;
	@FXML private Button exitButton;
	@FXML private Button clearButton;

	@FXML 
	public void up(ActionEvent e) {
		if (Snake.instance().getSnakeDirection() != Directions.DOWN)
			Snake.instance().setSnakeDirection(Directions.UP);
	}
	
	@FXML 
	public void down(ActionEvent e) {
		if (Snake.instance().getSnakeDirection() != Directions.UP)
			Snake.instance().setSnakeDirection(Directions.DOWN);
	}
	
	@FXML 
	public void left(ActionEvent e) {
		if (Snake.instance().getSnakeDirection() != Directions.RIGHT)
			Snake.instance().setSnakeDirection(Directions.LEFT);
	}
	
	@FXML 
	public void right(ActionEvent e) {
		if (Snake.instance().getSnakeDirection() != Directions.LEFT)
			Snake.instance().setSnakeDirection(Directions.RIGHT);
	}
	
	@FXML 
	private void resume(ActionEvent e) {
		if (resumeButton.getText().equals("Start")) {
			resumeButton.setText("Resume");
			setDifficultDisable();
		}
		Snake.resumeGame();
		setArrowsEnable();
		Snake.setPaused(false);
		pauseButton.setDisable(false);
		resumeButton.setDisable(true);
	}
	
	@FXML 
	private void pause(ActionEvent e) {
		if (!Snake.isPaused()) {
			Snake.setPaused(true);
			Snake.instance().updatePlayingTime();
			pauseButton.setDisable(true);
			resumeButton.setDisable(false);
			setArrowsDisable();
		}
	}
	
	@FXML 
	private void restart(ActionEvent e) throws InterruptedException {
		Snake.instance().restart();
		refreshScoreboard();
		youLose.setText(""); // for the case when uses restart after losing
		setArrowsDisable();
		setDifficultEnable();
		pauseButton.setDisable(true);
		resumeButton.setDisable(false);
		resumeButton.setText("Start");
		difficultInizialization();
	}
	
	@FXML 
	public void saveAndExit(ActionEvent e) {
		if (!Snake.isPaused())
			Snake.instance().updatePlayingTime();
		try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(MainFX.getSnakeFile()))) {
			writer.writeObject(Snake.instance());
		} catch (IOException ioe) {
		} finally {
			Stage stage = (Stage) scenePane.getScene().getWindow();
			stage.close();
			System.exit(0);
		}
	}
	
	@FXML 
	private void clearLeaderboard() {
		Scoreboard.clear();
		refreshScoreboard();
	}
	
	@FXML
	private void setEasyDifficult() {
		if (easyMode.isSelected()) {
			easyMode.setDisable(true);
			hardMode.setDisable(false);
			hardMode.setSelected(false);
			Snake.instance().setEasyMode();
			setCurrentSpeed(Snake.instance().getSpeed());
		} 
	}
	
	@FXML
	private void setHardDifficult() {
		if (hardMode.isSelected()) {
			hardMode.setDisable(true);
			easyMode.setDisable(false);
			easyMode.setSelected(false);
			Snake.instance().setHardMode();
			setCurrentSpeed(Snake.instance().getSpeed());
		}
	}
	
	@Override 
	public void initialize(URL arg0, ResourceBundle arg1) {
		String[] scoreboard = Scoreboard.getScoreboard();
		Platform.setImplicitExit(false);
		listView.getItems().addAll(scoreboard);
	}
	
	public void setCheckBoxOn(Point p, boolean isHead) throws InterruptedException {
		int[][] currentField = Field.getCurrentField();
		int x = p.getX();
		int y = p.getY();
		if (x > Field.X_LENGTH - 1 || x < 0 || y > Field.Y_LENGTH - 1 || y < 0 || currentField[y][x] == 1)
			throw new InterruptedException(); // leads to loss
		currentField[y][x] = 1;
		if (isHead)
			setCheckBoxColor(checkBoxes[y][x], Color.PURPLE);
		else
			setCheckBoxColor(checkBoxes[y][x], Color.GREEN);
	}

	public void setCheckBoxOff(Point p) {
		int[][] currentField = Field.getCurrentField();
		int x = p.getX();
		int y = p.getY();
		currentField[y][x] = 0;
		checkBoxes[y][x].setEffect(new Shadow(0, Color.WHITE));
	}

	private Image appleImage;
	
	public void setAppleCheckBoxOn(Point p) {
		int x = p.getX();
		int y = p.getY();

		if (appleImage == null)
			appleImage = new Image("apple-icon.png");
		checkBoxes[y][x].setEffect(new ImageInput(appleImage, -2, -2));
	}

	// we need this method to color a point from head
	// color to body color without checking for loss.
	public void setCheckBoxWithoutAddingInMatrix(Point p) {
		int x = p.getX();
		int y = p.getY();
		setCheckBoxColor(checkBoxes[y][x], Color.GREEN);
	}
	
	private void setCheckBoxColor(CheckBox checkBox, Color color) {
		checkBox.setEffect(new ColorInput(0, 0, 22, 22, color));
	}

	public void setYouLose() {
		youLose.setText("YOU LOSE");
		Snake.instance().interrupt();
		Snake.instance().updatePlayingTime();
		if (!Snake.instance().isLose()) { // it won't let one snake to append the result in leaderboard more than once
			Scoreboard.append(Snake.instance().getPlayingTime(), Snake.instance().getPoints());
			Snake.instance().setLose(true);
		}
	}
	
	public void setCurrentSpeed(Double speed) {
		currentSpeed.setText(String.format("Current speed: %.2f", 1000 / speed));
	}

	private void refreshScoreboard() {
		listView.getItems().clear();
		initialize(null, null);
	}

	public void FXMLInizialization() {
		checkBoxesInizialization();
		setButtonFocusTraversableOff();
		setArrowsDisable();
		pauseButton.setDisable(true);
	}
	
	public void difficultInizialization() {
		if (Snake.instance().getDifficult() == Difficulties.EASY) {
			easyMode.setSelected(true);
			setEasyDifficult();
		} else if (Snake.instance().getDifficult() == Difficulties.HARD) {
			hardMode.setSelected(true);
			setHardDifficult();
		}
	}
	
	private void setButtonFocusTraversableOff() {
		easyMode.setFocusTraversable(false);
		hardMode.setFocusTraversable(false);
		resumeButton.setFocusTraversable(false);
		pauseButton.setFocusTraversable(false);
		restartButton.setFocusTraversable(false);
		exitButton.setFocusTraversable(false);
		clearButton.setFocusTraversable(false);
		upButton.setFocusTraversable(false);
		leftButton.setFocusTraversable(false);
		rightButton.setFocusTraversable(false);
		downButton.setFocusTraversable(false);
	}
	
	private void setArrowsDisable() {
		upButton.setDisable(true);
		leftButton.setDisable(true);
		rightButton.setDisable(true);
		downButton.setDisable(true);
	}
	
	public void setDifficultDisable() {
		hardMode.setDisable(true);
		easyMode.setDisable(true);
	}
	
	private void setDifficultEnable() {
		hardMode.setDisable(false);
		easyMode.setDisable(false);
	}
	
	private void setArrowsEnable() {
		upButton.setDisable(false);
		leftButton.setDisable(false);
		rightButton.setDisable(false);
		downButton.setDisable(false);
	}
	
	@FXML private CheckBox check0_0;
	@FXML private CheckBox check0_1;
	@FXML private CheckBox check0_2;
	@FXML private CheckBox check0_3;
	@FXML private CheckBox check0_4;
	@FXML private CheckBox check0_5;
	@FXML private CheckBox check0_6;
	@FXML private CheckBox check0_7;
	@FXML private CheckBox check0_8;
	@FXML private CheckBox check0_9;
	@FXML private CheckBox check0_10;
	@FXML private CheckBox check0_11;
	@FXML private CheckBox check1_0;
	@FXML private CheckBox check1_1;
	@FXML private CheckBox check1_2;
	@FXML private CheckBox check1_3;
	@FXML private CheckBox check1_4;
	@FXML private CheckBox check1_5;
	@FXML private CheckBox check1_6;
	@FXML private CheckBox check1_7;
	@FXML private CheckBox check1_8;
	@FXML private CheckBox check1_9;
	@FXML private CheckBox check1_10;
	@FXML private CheckBox check1_11;
	@FXML private CheckBox check2_0;
	@FXML private CheckBox check2_1;
	@FXML private CheckBox check2_2;
	@FXML private CheckBox check2_3;
	@FXML private CheckBox check2_4;
	@FXML private CheckBox check2_5;
	@FXML private CheckBox check2_6;
	@FXML private CheckBox check2_7;
	@FXML private CheckBox check2_8;
	@FXML private CheckBox check2_9;
	@FXML private CheckBox check2_10;
	@FXML private CheckBox check2_11;
	@FXML private CheckBox check3_0;
	@FXML private CheckBox check3_1;
	@FXML private CheckBox check3_2;
	@FXML private CheckBox check3_3;
	@FXML private CheckBox check3_4;
	@FXML private CheckBox check3_5;
	@FXML private CheckBox check3_6;
	@FXML private CheckBox check3_7;
	@FXML private CheckBox check3_8;
	@FXML private CheckBox check3_9;
	@FXML private CheckBox check3_10;
	@FXML private CheckBox check3_11;
	@FXML private CheckBox check4_0;
	@FXML private CheckBox check4_1;
	@FXML private CheckBox check4_2;
	@FXML private CheckBox check4_3;
	@FXML private CheckBox check4_4;
	@FXML private CheckBox check4_5;
	@FXML private CheckBox check4_6;
	@FXML private CheckBox check4_7;
	@FXML private CheckBox check4_8;
	@FXML private CheckBox check4_9;
	@FXML private CheckBox check4_10;
	@FXML private CheckBox check4_11;
	@FXML private CheckBox check5_0;
	@FXML private CheckBox check5_1;
	@FXML private CheckBox check5_2;
	@FXML private CheckBox check5_3;
	@FXML private CheckBox check5_4;
	@FXML private CheckBox check5_5;
	@FXML private CheckBox check5_6;
	@FXML private CheckBox check5_7;
	@FXML private CheckBox check5_8;
	@FXML private CheckBox check5_9;
	@FXML private CheckBox check5_10;
	@FXML private CheckBox check5_11;
	@FXML private CheckBox check6_0;
	@FXML private CheckBox check6_1;
	@FXML private CheckBox check6_2;
	@FXML private CheckBox check6_3;
	@FXML private CheckBox check6_4;
	@FXML private CheckBox check6_5;
	@FXML private CheckBox check6_6;
	@FXML private CheckBox check6_7;
	@FXML private CheckBox check6_8;
	@FXML private CheckBox check6_9;
	@FXML private CheckBox check6_10;
	@FXML private CheckBox check6_11;
	@FXML private CheckBox check7_0;
	@FXML private CheckBox check7_1;
	@FXML private CheckBox check7_2;
	@FXML private CheckBox check7_3;
	@FXML private CheckBox check7_4;
	@FXML private CheckBox check7_5;
	@FXML private CheckBox check7_6;
	@FXML private CheckBox check7_7;
	@FXML private CheckBox check7_8;
	@FXML private CheckBox check7_9;
	@FXML private CheckBox check7_10;
	@FXML private CheckBox check7_11;
	@FXML private CheckBox check8_0;
	@FXML private CheckBox check8_1;
	@FXML private CheckBox check8_2;
	@FXML private CheckBox check8_3;
	@FXML private CheckBox check8_4;
	@FXML private CheckBox check8_5;
	@FXML private CheckBox check8_6;
	@FXML private CheckBox check8_7;
	@FXML private CheckBox check8_8;
	@FXML private CheckBox check8_9;
	@FXML private CheckBox check8_10;
	@FXML private CheckBox check8_11;
	@FXML private CheckBox check9_0;
	@FXML private CheckBox check9_1;
	@FXML private CheckBox check9_2;
	@FXML private CheckBox check9_3;
	@FXML private CheckBox check9_4;
	@FXML private CheckBox check9_5;
	@FXML private CheckBox check9_6;
	@FXML private CheckBox check9_7;
	@FXML private CheckBox check9_8;
	@FXML private CheckBox check9_9;
	@FXML private CheckBox check9_10;
	@FXML private CheckBox check9_11;
	@FXML private CheckBox check10_0;
	@FXML private CheckBox check10_1;
	@FXML private CheckBox check10_2;
	@FXML private CheckBox check10_3;
	@FXML private CheckBox check10_4;
	@FXML private CheckBox check10_5;
	@FXML private CheckBox check10_6;
	@FXML private CheckBox check10_7;
	@FXML private CheckBox check10_8;
	@FXML private CheckBox check10_9;
	@FXML private CheckBox check10_10;
	@FXML private CheckBox check10_11;
	@FXML private CheckBox check11_0;
	@FXML private CheckBox check11_1;
	@FXML private CheckBox check11_2;
	@FXML private CheckBox check11_3;
	@FXML private CheckBox check11_4;
	@FXML private CheckBox check11_5;
	@FXML private CheckBox check11_6;
	@FXML private CheckBox check11_7;
	@FXML private CheckBox check11_8;
	@FXML private CheckBox check11_9;
	@FXML private CheckBox check11_10;
	@FXML private CheckBox check11_11;
	
	private CheckBox[][] checkBoxes = new CheckBox[Field.X_LENGTH][Field.Y_LENGTH];

	private void checkBoxesInizialization() {
		checkBoxes[0][0] = check0_0;
		checkBoxes[0][1] = check0_1;
		checkBoxes[0][2] = check0_2;
		checkBoxes[0][3] = check0_3;
		checkBoxes[0][4] = check0_4;
		checkBoxes[0][5] = check0_5;
		checkBoxes[0][6] = check0_6;
		checkBoxes[0][7] = check0_7;
		checkBoxes[0][8] = check0_8;
		checkBoxes[0][9] = check0_9;
		checkBoxes[0][10] = check0_10;
		checkBoxes[0][11] = check0_11;
		checkBoxes[1][0] = check1_0;
		checkBoxes[1][1] = check1_1;
		checkBoxes[1][2] = check1_2;
		checkBoxes[1][3] = check1_3;
		checkBoxes[1][4] = check1_4;
		checkBoxes[1][5] = check1_5;
		checkBoxes[1][6] = check1_6;
		checkBoxes[1][7] = check1_7;
		checkBoxes[1][8] = check1_8;
		checkBoxes[1][9] = check1_9;
		checkBoxes[1][10] = check1_10;
		checkBoxes[1][11] = check1_11;
		checkBoxes[2][0] = check2_0;
		checkBoxes[2][1] = check2_1;
		checkBoxes[2][2] = check2_2;
		checkBoxes[2][3] = check2_3;
		checkBoxes[2][4] = check2_4;
		checkBoxes[2][5] = check2_5;
		checkBoxes[2][6] = check2_6;
		checkBoxes[2][7] = check2_7;
		checkBoxes[2][8] = check2_8;
		checkBoxes[2][9] = check2_9;
		checkBoxes[2][10] = check2_10;
		checkBoxes[2][11] = check2_11;
		checkBoxes[3][0] = check3_0;
		checkBoxes[3][1] = check3_1;
		checkBoxes[3][2] = check3_2;
		checkBoxes[3][3] = check3_3;
		checkBoxes[3][4] = check3_4;
		checkBoxes[3][5] = check3_5;
		checkBoxes[3][6] = check3_6;
		checkBoxes[3][7] = check3_7;
		checkBoxes[3][8] = check3_8;
		checkBoxes[3][9] = check3_9;
		checkBoxes[3][10] = check3_10;
		checkBoxes[3][11] = check3_11;
		checkBoxes[4][0] = check4_0;
		checkBoxes[4][1] = check4_1;
		checkBoxes[4][2] = check4_2;
		checkBoxes[4][3] = check4_3;
		checkBoxes[4][4] = check4_4;
		checkBoxes[4][5] = check4_5;
		checkBoxes[4][6] = check4_6;
		checkBoxes[4][7] = check4_7;
		checkBoxes[4][8] = check4_8;
		checkBoxes[4][9] = check4_9;
		checkBoxes[4][10] = check4_10;
		checkBoxes[4][11] = check4_11;
		checkBoxes[5][0] = check5_0;
		checkBoxes[5][1] = check5_1;
		checkBoxes[5][2] = check5_2;
		checkBoxes[5][3] = check5_3;
		checkBoxes[5][4] = check5_4;
		checkBoxes[5][5] = check5_5;
		checkBoxes[5][6] = check5_6;
		checkBoxes[5][7] = check5_7;
		checkBoxes[5][8] = check5_8;
		checkBoxes[5][9] = check5_9;
		checkBoxes[5][10] = check5_10;
		checkBoxes[5][11] = check5_11;
		checkBoxes[6][0] = check6_0;
		checkBoxes[6][1] = check6_1;
		checkBoxes[6][2] = check6_2;
		checkBoxes[6][3] = check6_3;
		checkBoxes[6][4] = check6_4;
		checkBoxes[6][5] = check6_5;
		checkBoxes[6][6] = check6_6;
		checkBoxes[6][7] = check6_7;
		checkBoxes[6][8] = check6_8;
		checkBoxes[6][9] = check6_9;
		checkBoxes[6][10] = check6_10;
		checkBoxes[6][11] = check6_11;
		checkBoxes[7][0] = check7_0;
		checkBoxes[7][1] = check7_1;
		checkBoxes[7][2] = check7_2;
		checkBoxes[7][3] = check7_3;
		checkBoxes[7][4] = check7_4;
		checkBoxes[7][5] = check7_5;
		checkBoxes[7][6] = check7_6;
		checkBoxes[7][7] = check7_7;
		checkBoxes[7][8] = check7_8;
		checkBoxes[7][9] = check7_9;
		checkBoxes[7][10] = check7_10;
		checkBoxes[7][11] = check7_11;
		checkBoxes[8][0] = check8_0;
		checkBoxes[8][1] = check8_1;
		checkBoxes[8][2] = check8_2;
		checkBoxes[8][3] = check8_3;
		checkBoxes[8][4] = check8_4;
		checkBoxes[8][5] = check8_5;
		checkBoxes[8][6] = check8_6;
		checkBoxes[8][7] = check8_7;
		checkBoxes[8][8] = check8_8;
		checkBoxes[8][9] = check8_9;
		checkBoxes[8][10] = check8_10;
		checkBoxes[8][11] = check8_11;
		checkBoxes[9][0] = check9_0;
		checkBoxes[9][1] = check9_1;
		checkBoxes[9][2] = check9_2;
		checkBoxes[9][3] = check9_3;
		checkBoxes[9][4] = check9_4;
		checkBoxes[9][5] = check9_5;
		checkBoxes[9][6] = check9_6;
		checkBoxes[9][7] = check9_7;
		checkBoxes[9][8] = check9_8;
		checkBoxes[9][9] = check9_9;
		checkBoxes[9][10] = check9_10;
		checkBoxes[9][11] = check9_11;
		checkBoxes[10][0] = check10_0;
		checkBoxes[10][1] = check10_1;
		checkBoxes[10][2] = check10_2;
		checkBoxes[10][3] = check10_3;
		checkBoxes[10][4] = check10_4;
		checkBoxes[10][5] = check10_5;
		checkBoxes[10][6] = check10_6;
		checkBoxes[10][7] = check10_7;
		checkBoxes[10][8] = check10_8;
		checkBoxes[10][9] = check10_9;
		checkBoxes[10][10] = check10_10;
		checkBoxes[10][11] = check10_11;
		checkBoxes[11][0] = check11_0;
		checkBoxes[11][1] = check11_1;
		checkBoxes[11][2] = check11_2;
		checkBoxes[11][3] = check11_3;
		checkBoxes[11][4] = check11_4;
		checkBoxes[11][5] = check11_5;
		checkBoxes[11][6] = check11_6;
		checkBoxes[11][7] = check11_7;
		checkBoxes[11][8] = check11_8;
		checkBoxes[11][9] = check11_9;
		checkBoxes[11][10] = check11_10;
		checkBoxes[11][11] = check11_11;
		for (int i = 0; i < Field.X_LENGTH; i++) {
			for (int j = 0; j < Field.Y_LENGTH; j++) {
				checkBoxes[i][j].setEffect(new Shadow(0, Color.WHITE));
			}
		}
	}
}