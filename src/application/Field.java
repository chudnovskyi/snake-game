package application;

public abstract class Field {
	public static final int X_LENGTH = 12;
	public static final int Y_LENGTH = 12;
	private static int[][] field = new int[X_LENGTH][Y_LENGTH];

	public static void clearField() {
		field = new int[Y_LENGTH][Y_LENGTH];
	}

	public static int[][] getCurrentField() {
		return field;
	}

	public static void setFieldOn(Point p) {
		int x = p.getX();
		int y = p.getY();
		if (x > X_LENGTH - 1 || x < 0 || y > Y_LENGTH - 1 || y < 0)
			throw new RuntimeException();
		field[y][x] = 1;
	}

	public static void setFieldOff(Point p) {
		int x = p.getX();
		int y = p.getY();
		if (x > X_LENGTH - 1 || x < 0 || y > Y_LENGTH - 1 || y < 0)
			throw new RuntimeException();
		field[y][x] = 0;
	}

	public static void printMatrix() {
		System.out.println("-----------------");
		for (int i = 0; i < X_LENGTH; i++) {
			for (int j = 0; j < Y_LENGTH; j++) {
				System.out.print(field[i][j] + " ");
			}
			System.out.println();
		}
	}
}
