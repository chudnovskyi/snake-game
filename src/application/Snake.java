package application;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("serial")
public class Snake extends Thread implements Serializable {
	private Difficulties difficult;
	private Directions currentDirection;
	private Directions tempDirection;
	private Instant startingTime;
	private long playingTime;
	private Integer sleepTime;
	private boolean isLose;

	private static Lock lock = new ReentrantLock();
	private static Condition condition = lock.newCondition();
	private static volatile boolean isPaused = true;

	private List<Point> snake;
	private Point apple;
	private int points;

	private static Snake snakeInstance;

	{
		setEasyMode();
		currentDirection = Directions.RIGHT;
		tempDirection = Directions.RIGHT;
		snake = new ArrayList<>(List.of(new Point(3, 4), new Point(2, 4), new Point(1, 4)));
		isLose = false;
	}

	private Snake() {
	}

	public static Snake instance() {
		if (snakeInstance == null) {
			snakeInstance = new Snake();
		}
		return snakeInstance;
	}

	public static void setInstance(Snake snake) {
		if (snakeInstance == null) {
			snakeInstance = snake;
		}
	}

	@Override
	public void run() {
		try {
			snakeInizialization();
			appleInizialization();
			while (true) {
				sleepBetweenMoves();
				pauseGameIfPaused();
				moveSnake();
			}
		} catch (InterruptedException ie) {
		}
	}

	private void moveSnake() throws InterruptedException {
		Point head = snake.get(0);
		addNewPoint(head);
		changeHeadDirectionAndMove(head);

		if (head.equals(apple)) {
			selectHeadCheckBox(head);
			eatApple(); // if head eats apple - nothing will happen with snake coordinates
		} else {
			removeTail(); // if not - removing snake's tail
			selectHeadCheckBox(head);
		}
	}

	private void addNewPoint(Point head) throws InterruptedException {
		Point newPoint = head.copyThisPoint();
		snake.add(1, newPoint); // adding newPoint to the place where the head was before moving
		repaintCheckBox(newPoint);
	}

	private void changeHeadDirectionAndMove(Point head) throws InterruptedException {
		currentDirection = tempDirection;
		if (currentDirection == Directions.UP) {
			head.moveUp();
		} else if (currentDirection == Directions.DOWN) {
			head.moveDown();
		} else if (currentDirection == Directions.RIGHT) {
			head.moveRight();
		} else if (currentDirection == Directions.LEFT) {
			head.moveLeft();
		}
	}

	private void eatApple() throws InterruptedException {
		points++;
		speedUpSnake();
		generateNewApple();
	}

	private void removeTail() throws InterruptedException {
		Point tail = snake.get(snake.size() - 1);
		snake.remove(snake.size() - 1);
		deselectCheckBox(tail);
		Thread.sleep(5); // Gives the tail enough time to avoid the head
	}

	private void sleepBetweenMoves() {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			interrupt();
		}
	}

	private void speedUpSnake() {
		if (sleepTime > 600)
			sleepTime -= 40;
		else if (sleepTime > 500)
			sleepTime -= 30;
		else if (sleepTime > 400)
			sleepTime -= 20;
		else if (sleepTime > 300)
			sleepTime -= 10;
		else if (sleepTime > 200)
			sleepTime -= 5;
		else
			sleepTime--;
	}

	private void snakeInizialization() throws InterruptedException {
		selectHeadCheckBox(snake.get(0));
		for (Point point : snake.subList(1, snake.size())) {
			selectBodyCheckBox(point);
		}
	}

	private void appleInizialization() throws InterruptedException {
		if (apple == null) // Will not allow a new apple to be created after deserialization
			generateNewApple();
		else if (!isLose) // Will show apple after restart
			selectAppleCheckBox(apple);
	}

	private void generateNewApple() throws InterruptedException {
		apple = AppleGenerator.generate();
		selectAppleCheckBox(apple);
	}

	private void pauseGameIfPaused() throws InterruptedException {
		if (Snake.isPaused()) {
			lock.lock();
			try {
				condition.await();
			} finally {
				lock.unlock();
			}
		}
	}

	private void refreshPlayingTime() {
		playingTime = 0;
	}

	private void updateStartingTime() {
		startingTime = Instant.now();
	}

	public void restart() throws InterruptedException {
		for (int i = 0; i < Field.X_LENGTH; i++) {
			for (int j = 0; j < Field.Y_LENGTH; j++) {
				deselectCheckBox(new Point(i, j));
			}
		}
		
		updateStartingTime();
		refreshPlayingTime();
		Field.clearField();
		
		snakeInstance.interrupt();
		snakeInstance = new Snake();
		Snake.setPaused(true);
		
		if (difficult == Difficulties.EASY) {
			snakeInstance.setEasyMode();
		} else if (difficult == Difficulties.HARD) {
			snakeInstance.setHardMode();
		}
		
		snakeInstance.start();
	}

	public static void resumeGame() {
		if (Snake.isPaused()) {
			lock.lock();
			try {
				condition.signalAll();
				snakeInstance.updateStartingTime();
			} finally {
				lock.unlock();
			}
		}
	}

	public boolean isGameStarted() {
		return startingTime != null;
	}

	public void updatePlayingTime() {
		playingTime += Duration.between(startingTime, Instant.now()).toMillis();
	}

	public int getPoints() {
		return points;
	}

	public double getSpeed() {
		return sleepTime;
	}

	public Difficulties getDifficult() {
		return difficult;
	}

	public Directions getSnakeDirection() {
		return currentDirection;
	}

	public Long getPlayingTime() {
		return playingTime;
	}

	public void setSnakeDirection(Directions snakeDirection) {
		this.tempDirection = snakeDirection;
	}

	public static void setPaused(boolean isCansel) {
		Snake.isPaused = isCansel;
	}
	

	public void setLose(boolean isLose) {
		this.isLose = isLose;
	}

	public void setEasyMode() {
		difficult = Difficulties.EASY;
		if (!isGameStarted())
			sleepTime = 700;
	}

	public void setHardMode() {
		difficult = Difficulties.HARD;
		if (!isGameStarted())
			sleepTime = 200;
	}

	public static boolean isPaused() {
		return isPaused;
	}

	public boolean isLose() {
		return isLose;
	}

	private void selectHeadCheckBox(Point point) throws InterruptedException {
		CheckBoxOnMachine.getExchanger().exchange(new SimpleEntry<Point, Boolean>(point, true));
	}

	private void selectBodyCheckBox(Point point) throws InterruptedException {
		CheckBoxOnMachine.getExchanger().exchange(new SimpleEntry<Point, Boolean>(point, false));
	}

	private void repaintCheckBox(Point point) throws InterruptedException {
		CheckBoxOnMachine.getExchanger().exchange(new SimpleEntry<Point, Boolean>(point, null));
	}

	private void deselectCheckBox(Point point) throws InterruptedException {
		CheckBoxOFFMachine.getExchanger().exchange(point);
	}

	private void selectAppleCheckBox(Point point) throws InterruptedException {
		CheckBoxAppleMachine.getExchanger().exchange(point);
	}

	@Override
	public String toString() {
		return "Snake size: " + snake.size();
	}
}

enum Directions {
	UP, DOWN, RIGHT, LEFT
}

enum Difficulties {
	EASY, HARD
}
