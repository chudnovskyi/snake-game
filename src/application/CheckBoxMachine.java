package application;

import java.util.Map.Entry;
import java.util.concurrent.Exchanger;

public class CheckBoxMachine extends Thread {
	private static Exchanger<Entry<Point, Boolean>> exchanger = new Exchanger<>();
	private static CheckBoxMachine machineInstance;
	private static Controller controller;

	public static CheckBoxMachine instance() {
		if (machineInstance == null) {
			machineInstance = new CheckBoxMachine();
		}
		return machineInstance;
	}

	private CheckBoxMachine() {
		super("CheckBoxMachine");
		controller = MainFX.getController();
		setDaemon(true);
		start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Entry<Point, Boolean> entry = exchanger.exchange(null);
				if (entry.getValue() == Boolean.FALSE)
					controller.setCheckBoxOff(entry.getKey());
				else if (entry.getValue() == Boolean.TRUE)
					controller.setCheckBoxOn(entry.getKey());
				else if (entry.getValue() == null) 
					controller.setAppleCheckBoxOn(entry.getKey());
			} catch (InterruptedException ie) {
				controller.setYouLose();
			}
		}
	}

	public static Exchanger<Entry<Point, Boolean>> getExchanger() {
		return exchanger;
	}
}