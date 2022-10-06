package application;

import java.util.concurrent.Exchanger;

public class CheckBoxOFFMachine extends Thread {
	private static Exchanger<Point> exchanger = new Exchanger<>();
	private static CheckBoxOFFMachine machineInstance;
	private static Controller controller;

	public static CheckBoxOFFMachine instance() {
		if (machineInstance == null) {
			machineInstance = new CheckBoxOFFMachine();
		}
		return machineInstance;
	}

	private CheckBoxOFFMachine() {
		super("CheckBoxOFFMachine");
		controller = MainFX.getController();
		setDaemon(true);
		start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Point pointToUncheck = exchanger.exchange(null);
				synchronized (MainFX.class) {
					controller.setCheckBoxOff(pointToUncheck);
				}
			} catch (InterruptedException ie) {
				controller.setYouLose();
			}
		}
	}

	public static Exchanger<Point> getExchanger() {
		return exchanger;
	}
}