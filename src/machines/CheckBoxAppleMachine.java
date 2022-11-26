package machines;

import java.util.concurrent.Exchanger;

import application.Controller;
import application.MainFX;
import application.Point;
import application.Snake;

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
				controller.setCurrentSpeed(Snake.instance().getSpeed());
			} catch (InterruptedException ie) {
				controller.setYouLose();
			}
		}
	}

	public static Exchanger<Point> getExchanger() {
		return exchanger;
	}
}