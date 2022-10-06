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
	private Directions currentDirection;
	private Directions tempDirection;
	private Instant startingTime;
	private long playingTime;
	private int sleepTime;
	private boolean isLose;

	private static Lock lock = new ReentrantLock();
	private static Condition condition = lock.newCondition();

	private List<Point> snake;
	private Point apple;
	private int points;

	private static volatile boolean isPaused = true;
	private static Snake snakeInstance;

	public static Snake instance() {
		if (snakeInstance == null) {
			snakeInstance = new Snake();
		}
		return snakeInstance;
	}

	public static Snake setInstance(Snake snake) {
		if (snakeInstance == null) {
			snakeInstance = snake;
		}
		return snakeInstance;
	}

	{
		currentDirection = Directions.RIGHT;
		tempDirection = Directions.RIGHT;
		snake = new ArrayList<>(List.of(new Point(3, 4), new Point(2, 4), new Point(1, 4)));
		sleepTime = 800;
		isLose = false;
	}

	private Snake() {
	}

	@Override
	public void run() {
		try {
			startingInizializations();
			while (true) {
				sleepBetweenMoves();
				pauseThreadIfPaused();
				moveSnake();
			}
		} catch (InterruptedException ie) {
		}
	}

	private void startingInizializations() throws InterruptedException {
		if (startingTime == null)
			startingTime = Instant.now();
		if (apple == null) // Will not allow a new apple to be created after deserialization
			newAppleInizialization(snake.get(0));
		else if (!isLose) // Will show apple after restart
			CheckBoxAppleMachine.getExchanger().exchange(apple);
		snakeInizialization();
	}

	private void moveSnake() throws InterruptedException {
		Point head = snake.get(0);
		Point newPoint = head.copyThisPoint();
		snake.add(1, newPoint); // adding newPoint to the place where the head was before moving
		CheckBoxOnMachine.getExchanger().exchange(new SimpleEntry<Point, Boolean>(newPoint, null));
		changeHeadDirectionAndMove(head);
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
		eatApple(head); // Eats an apple if the head is at the apple coordinate
		CheckBoxOnMachine.getExchanger().exchange(new SimpleEntry<Point, Boolean>(head, true));
	}

	private void eatApple(Point head) throws InterruptedException {
		if (head.equals(apple)) { // if head eats apple - nothing will happen with snake coordinates
			points++;
			newAppleInizialization(head);
			speedUpSnake();
		} else { // if its not - removing snake's tail
			Point tail = snake.get(snake.size() - 1);
			snake.remove(snake.size() - 1);
			CheckBoxOFFMachine.getExchanger().exchange(tail);
			Thread.sleep(5); // Will not let you eat the last point of the tail
		}
	}

	// I pass "head" to the method to prevent a rare situation when a new
	// apple appears in the place of a new head and the program crushes
	private void newAppleInizialization(Point head) throws InterruptedException {
		apple = AppleGenerator.generate(head);
		CheckBoxAppleMachine.getExchanger().exchange(apple);
	}
	
	private void snakeInizialization() throws InterruptedException {
		CheckBoxOnMachine.getExchanger().exchange(new SimpleEntry<Point, Boolean>(snake.get(0), true));
		for (Point point : snake.subList(1, snake.size())) {
			CheckBoxOnMachine.getExchanger().exchange(new SimpleEntry<Point, Boolean>(point, false));
		}
	}
	
	private void sleepBetweenMoves() {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			interrupt();
		}
	}

	private void speedUpSnake() {
		if (sleepTime > 700)
			sleepTime -= 50;
		else if (sleepTime > 600)
			sleepTime -= 40;
		else if (sleepTime > 500)
			sleepTime -= 30;
		else if (sleepTime > 400)
			sleepTime -= 20;
		else if (sleepTime > 300)
			sleepTime -= 10;
		else
			sleepTime -= 5;
	}

	public void restart() throws InterruptedException {
		CheckBoxOFFMachine.getExchanger().exchange(apple);
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				CheckBoxOFFMachine.getExchanger().exchange(new Point(i, j));
			}
		}
		updateStartingTime();
		refreshPlayingTime();
		Field.clearField();

		snakeInstance.interrupt();
		snakeInstance = new Snake();
		Snake.setPause(true);
		snakeInstance.start();
	}

	private void pauseThreadIfPaused() throws InterruptedException {
		lock.lock();
		try {
			if (isPaused) {
				condition.await();
			}
		} finally {
			lock.unlock();
		}
	}

	public int getPoints() {
		return points;
	}

	public Directions getSnakeDirection() {
		return currentDirection;
	}

	public void setSnakeDirection(Directions snakeDirection) {
		this.tempDirection = snakeDirection;
	}

	public Long getPlayingTime() {
		return playingTime;
	}

	public void updatePlayingTime() {
		playingTime += Duration.between(startingTime, Instant.now()).toMillis();
	}

	public void refreshPlayingTime() {
		playingTime = 0;
	}

	public void updateStartingTime() {
		startingTime = Instant.now();
	}

	public static Lock getLock() {
		return lock;
	}

	public static Condition getCondition() {
		return condition;
	}

	public static boolean isPaused() {
		return isPaused;
	}

	public static void setPause(boolean isCansel) {
		Snake.isPaused = isCansel;
	}

	public boolean isLose() {
		return isLose;
	}

	public void setLose(boolean isLose) {
		this.isLose = isLose;
	}

	@SuppressWarnings("unused")
	private void printDetails() { // method for testing current field and snake
		Field.printMatrix();
		snake.forEach(System.out::println);
	}

	@Override
	public String toString() {
		return "Snake size: " + snake.size();
	}
}

enum Directions {
	UP, DOWN, RIGHT, LEFT
}