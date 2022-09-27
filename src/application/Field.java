package application;

public abstract class Field {
	private static int[][] field = new int[9][9];

	public static void clearField() {
		field = new int[9][9];
	}

	public static int[][] getCurrentField() {
		return field;
	}

	public static void setFieldOn(Point p) {
		int x = p.getX();
		int y = p.getY();
		if (x > 8 || x < 0 || y > 8 || y < 0)
			throw new RuntimeException();
		field[y][x] = 1;
	}

	public static void setFieldOff(Point p) {
		int x = p.getX();
		int y = p.getY();
		if (x > 8 || x < 0 || y > 8 || y < 0)
			throw new RuntimeException();
		field[y][x] = 0;
	}

	public static void printMatrix() {
		System.out.println("-----------------");
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(field[i][j] + " ");
			}
			System.out.println();
		}
	}
}
