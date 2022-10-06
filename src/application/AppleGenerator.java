package application;

import java.util.concurrent.ThreadLocalRandom;

public abstract class AppleGenerator {
	public static Point generate(Point head) {
		int field[][] = Field.getCurrentField();
		while (true) {
			int x = ThreadLocalRandom.current().nextInt(9);
			int y = ThreadLocalRandom.current().nextInt(9);
			if (field[y][x] == 0 && (head.getX() != x && head.getY() != y))
				return new Point(x, y);
		}
	}
}
