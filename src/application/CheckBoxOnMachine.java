package application;

import java.util.Map.Entry;
import java.util.concurrent.Exchanger;

public class CheckBoxOnMachine extends Thread {
	private static Exchanger<Entry<Point, Boolean>> exchanger = new Exchanger<>();
	private static CheckBoxOnMachine machineInstance;
	private static Controller controller;

	public static CheckBoxOnMachine instance() {
		if (machineInstance == null) {
			machineInstance = new CheckBoxOnMachine();
		}
		return machineInstance;
	}

	private CheckBoxOnMachine() {
		super("CheckBoxOnMachine");
		controller = MainFX.getController();
		setDaemon(true);
		start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Entry<Point, Boolean> entry = exchanger.exchange(null);
				Point pointToCheck = entry.getKey();
				Boolean isHead = entry.getValue();
				synchronized (MainFX.class) {
					if (isHead == null)
						controller.setCheckBoxWithoutAddingInMatrix(pointToCheck);
					else
						controller.setCheckBoxOn(pointToCheck, isHead);
				}
			} catch (InterruptedException ie) {
				controller.setYouLose();
			}
		}
	}

	public static Exchanger<Entry<Point, Boolean>> getExchanger() {
		return exchanger;
	}
}