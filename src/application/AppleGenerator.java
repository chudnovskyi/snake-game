package application;

import java.util.concurrent.ThreadLocalRandom;

public abstract class AppleGenerator {
	public static Point generate() {
		int field[][] = Field.getCurrentField();
		while (true) {
			int x = ThreadLocalRandom.current().nextInt(Field.X_LENGTH);
			int y = ThreadLocalRandom.current().nextInt(Field.Y_LENGTH);
			if (field[y][x] == 0)
				return new Point(x, y);
		}
	}
}
