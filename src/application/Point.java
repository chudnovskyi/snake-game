package application;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class Point implements Serializable {
	private int x;
	private int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void moveUp() {
		y--;
	}

	public void moveDown() {
		y++;
	}

	public void moveLeft() {
		x--;
	}

	public void moveRight() {
		x++;
	}
	

	public Point copyThisPoint() {
		return new Point(x, y);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		Point point = (Point) obj;
		if (this.getX() == point.getX() && this.getY() == point.getY())
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
}
