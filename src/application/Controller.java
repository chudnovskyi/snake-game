package application;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller implements Initializable {
	@FXML
	private Text youLose;

	@FXML
	private AnchorPane scenePane;

	@FXML
	private ListView<String> listView;

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
	private void pause(ActionEvent e) {
		if (!Snake.isPaused()) {
			Snake.setPause(true);
			Snake.instance().updatePlayingTime();
		}
	}

	@FXML
	private void resume(ActionEvent e) {
		if (Snake.isPaused()) {
			Snake.getLock().lock();
			try {
				Snake.setPause(false);
				Snake.getCondition().signalAll();
				Snake.instance().updateStartingTime();
			} finally {
				Snake.getLock().unlock();
			}
		}
	}

	@FXML
	private void restart(ActionEvent e) throws InterruptedException {
		Snake.instance().restart();
		refreshLeaderboard();
		youLose.setText(""); // for the case when clicked restart after losing
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		List<String> results = Leaderboard.getLeaderboard();
		String[] leaderboard = new String[results.size()];
		int i = 0;
		for (String result : results) {
			String[] term = result.split(",");
			long ms = Long.parseLong(term[0]);
			int points = Integer.parseInt(term[1]);
			leaderboard[i] = String.format("%d points | %ds %dms", points, ms / 1000, ms % 1000);
			i++;
		}
		Platform.setImplicitExit(false);
		listView.getItems().addAll(leaderboard);
	}

	@FXML
	private void saveAndExit(ActionEvent e) {
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
		Leaderboard.clear();
		refreshLeaderboard();
	}

	public void setCheckBoxOn(Point point) throws InterruptedException {
		int[][] currentField = Field.getCurrentField();
		int x = point.getX();
		int y = point.getY();
		if (x > 8 || x < 0 || y > 8 || y < 0 || currentField[y][x] == 1)
			throw new InterruptedException(); // THIS EXCEPTION LEADS TO LOSS AND RECORDING THE RESULT IN THE
												// LEADERBOARD
		currentField[y][x] = 1;
		checkBoxes[y][x].setSelected(true);
	}

	public void setCheckBoxOff(Point p) {
		int[][] currentField = Field.getCurrentField();
		int x = p.getX();
		int y = p.getY();
		currentField[y][x] = 0;
		checkBoxes[y][x].setSelected(false);
	}

	public void setAppleCheckBoxOn(Point p) {
		int x = p.getX();
		int y = p.getY();
		checkBoxes[y][x].setSelected(true);
	}

	public void setYouLose() {
		youLose.setText("YOU LOSE");
		Snake.instance().interrupt();
		Snake.instance().updatePlayingTime();
		if (!Snake.instance().isLose()) { // it won't let one snake to append the result in leaderboard more than once
			Leaderboard.append(Snake.instance().getPlayingTime(), Snake.instance().getPoints());
			Snake.instance().setLose(true);
		}
	}

	private void refreshLeaderboard() {
		listView.getItems().clear();
		initialize(null, null);
	}

	private CheckBox[][] checkBoxes = new CheckBox[9][9];

	@FXML
	private CheckBox check0_0;
	@FXML
	private CheckBox check0_1;
	@FXML
	private CheckBox check0_2;
	@FXML
	private CheckBox check0_3;
	@FXML
	private CheckBox check0_4;
	@FXML
	private CheckBox check0_5;
	@FXML
	private CheckBox check0_6;
	@FXML
	private CheckBox check0_7;
	@FXML
	private CheckBox check0_8;
	@FXML
	private CheckBox check1_0;
	@FXML
	private CheckBox check1_1;
	@FXML
	private CheckBox check1_2;
	@FXML
	private CheckBox check1_3;
	@FXML
	private CheckBox check1_4;
	@FXML
	private CheckBox check1_5;
	@FXML
	private CheckBox check1_6;
	@FXML
	private CheckBox check1_7;
	@FXML
	private CheckBox check1_8;
	@FXML
	private CheckBox check2_0;
	@FXML
	private CheckBox check2_1;
	@FXML
	private CheckBox check2_2;
	@FXML
	private CheckBox check2_3;
	@FXML
	private CheckBox check2_4;
	@FXML
	private CheckBox check2_5;
	@FXML
	private CheckBox check2_6;
	@FXML
	private CheckBox check2_7;
	@FXML
	private CheckBox check2_8;
	@FXML
	private CheckBox check3_0;
	@FXML
	private CheckBox check3_1;
	@FXML
	private CheckBox check3_2;
	@FXML
	private CheckBox check3_3;
	@FXML
	private CheckBox check3_4;
	@FXML
	private CheckBox check3_5;
	@FXML
	private CheckBox check3_6;
	@FXML
	private CheckBox check3_7;
	@FXML
	private CheckBox check3_8;
	@FXML
	private CheckBox check4_0;
	@FXML
	private CheckBox check4_1;
	@FXML
	private CheckBox check4_2;
	@FXML
	private CheckBox check4_3;
	@FXML
	private CheckBox check4_4;
	@FXML
	private CheckBox check4_5;
	@FXML
	private CheckBox check4_6;
	@FXML
	private CheckBox check4_7;
	@FXML
	private CheckBox check4_8;
	@FXML
	private CheckBox check5_0;
	@FXML
	private CheckBox check5_1;
	@FXML
	private CheckBox check5_2;
	@FXML
	private CheckBox check5_3;
	@FXML
	private CheckBox check5_4;
	@FXML
	private CheckBox check5_5;
	@FXML
	private CheckBox check5_6;
	@FXML
	private CheckBox check5_7;
	@FXML
	private CheckBox check5_8;
	@FXML
	private CheckBox check6_0;
	@FXML
	private CheckBox check6_1;
	@FXML
	private CheckBox check6_2;
	@FXML
	private CheckBox check6_3;
	@FXML
	private CheckBox check6_4;
	@FXML
	private CheckBox check6_5;
	@FXML
	private CheckBox check6_6;
	@FXML
	private CheckBox check6_7;
	@FXML
	private CheckBox check6_8;
	@FXML
	private CheckBox check7_0;
	@FXML
	private CheckBox check7_1;
	@FXML
	private CheckBox check7_2;
	@FXML
	private CheckBox check7_3;
	@FXML
	private CheckBox check7_4;
	@FXML
	private CheckBox check7_5;
	@FXML
	private CheckBox check7_6;
	@FXML
	private CheckBox check7_7;
	@FXML
	private CheckBox check7_8;
	@FXML
	private CheckBox check8_0;
	@FXML
	private CheckBox check8_1;
	@FXML
	private CheckBox check8_2;
	@FXML
	private CheckBox check8_3;
	@FXML
	private CheckBox check8_4;
	@FXML
	private CheckBox check8_5;
	@FXML
	private CheckBox check8_6;
	@FXML
	private CheckBox check8_7;
	@FXML
	private CheckBox check8_8;

	public void inizializeCheckBoxesMatrix() {
		checkBoxes[0][0] = check0_0;
		checkBoxes[0][1] = check0_1;
		checkBoxes[0][2] = check0_2;
		checkBoxes[0][3] = check0_3;
		checkBoxes[0][4] = check0_4;
		checkBoxes[0][5] = check0_5;
		checkBoxes[0][6] = check0_6;
		checkBoxes[0][7] = check0_7;
		checkBoxes[0][8] = check0_8;
		checkBoxes[1][0] = check1_0;
		checkBoxes[1][1] = check1_1;
		checkBoxes[1][2] = check1_2;
		checkBoxes[1][3] = check1_3;
		checkBoxes[1][4] = check1_4;
		checkBoxes[1][5] = check1_5;
		checkBoxes[1][6] = check1_6;
		checkBoxes[1][7] = check1_7;
		checkBoxes[1][8] = check1_8;
		checkBoxes[2][0] = check2_0;
		checkBoxes[2][1] = check2_1;
		checkBoxes[2][2] = check2_2;
		checkBoxes[2][3] = check2_3;
		checkBoxes[2][4] = check2_4;
		checkBoxes[2][5] = check2_5;
		checkBoxes[2][6] = check2_6;
		checkBoxes[2][7] = check2_7;
		checkBoxes[2][8] = check2_8;
		checkBoxes[3][0] = check3_0;
		checkBoxes[3][1] = check3_1;
		checkBoxes[3][2] = check3_2;
		checkBoxes[3][3] = check3_3;
		checkBoxes[3][4] = check3_4;
		checkBoxes[3][5] = check3_5;
		checkBoxes[3][6] = check3_6;
		checkBoxes[3][7] = check3_7;
		checkBoxes[3][8] = check3_8;
		checkBoxes[4][0] = check4_0;
		checkBoxes[4][1] = check4_1;
		checkBoxes[4][2] = check4_2;
		checkBoxes[4][3] = check4_3;
		checkBoxes[4][4] = check4_4;
		checkBoxes[4][5] = check4_5;
		checkBoxes[4][6] = check4_6;
		checkBoxes[4][7] = check4_7;
		checkBoxes[4][8] = check4_8;
		checkBoxes[5][0] = check5_0;
		checkBoxes[5][1] = check5_1;
		checkBoxes[5][2] = check5_2;
		checkBoxes[5][3] = check5_3;
		checkBoxes[5][4] = check5_4;
		checkBoxes[5][5] = check5_5;
		checkBoxes[5][6] = check5_6;
		checkBoxes[5][7] = check5_7;
		checkBoxes[5][8] = check5_8;
		checkBoxes[6][0] = check6_0;
		checkBoxes[6][1] = check6_1;
		checkBoxes[6][2] = check6_2;
		checkBoxes[6][3] = check6_3;
		checkBoxes[6][4] = check6_4;
		checkBoxes[6][5] = check6_5;
		checkBoxes[6][6] = check6_6;
		checkBoxes[6][7] = check6_7;
		checkBoxes[6][8] = check6_8;
		checkBoxes[7][0] = check7_0;
		checkBoxes[7][1] = check7_1;
		checkBoxes[7][2] = check7_2;
		checkBoxes[7][3] = check7_3;
		checkBoxes[7][4] = check7_4;
		checkBoxes[7][5] = check7_5;
		checkBoxes[7][6] = check7_6;
		checkBoxes[7][7] = check7_7;
		checkBoxes[7][8] = check7_8;
		checkBoxes[8][0] = check8_0;
		checkBoxes[8][1] = check8_1;
		checkBoxes[8][2] = check8_2;
		checkBoxes[8][3] = check8_3;
		checkBoxes[8][4] = check8_4;
		checkBoxes[8][5] = check8_5;
		checkBoxes[8][6] = check8_6;
		checkBoxes[8][7] = check8_7;
		checkBoxes[8][8] = check8_8;
	}
}