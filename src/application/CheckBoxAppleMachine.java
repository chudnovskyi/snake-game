package application;

import java.util.concurrent.Exchanger;

public class CheckBoxAppleMachine extends Thread {
	private static Exchanger<Point> exchanger = new Exchanger<>();
	private static CheckBoxAppleMachine machineInstance;
	private static Controller controller;

	public static CheckBoxAppleMachine instance() {
		if (machineInstance == null) {
			machineInstance = new CheckBoxAppleMachine();
		}
		return machineInstance;
	}

	private CheckBoxAppleMachine() {
		super("CheckBoxAppleMachine");
		controller = MainFX.getController();
		setDaemon(true);
		start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Point applePoint = exchanger.exchange(null);
				synchronized (MainFX.class) {
					controller.setAppleCheckBoxOn(applePoint);
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