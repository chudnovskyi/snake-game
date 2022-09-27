package application;

import java.util.concurrent.ThreadLocalRandom;

public abstract class AppleGenerator {
	public static Point generate(Point head) {
		int field[][] = Field.getCurrentField();
		int x;
		int y;
		while (true) {
			x = ThreadLocalRandom.current().nextInt(9);
			y = ThreadLocalRandom.current().nextInt(9);
			if (field[y][x] == 0 && (head.getX() != x && head.getY() != y))
				break;
		}
		return new Point(x, y);
	}
}
