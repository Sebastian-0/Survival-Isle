package util;

public class Point {
	public float x;
	public float y;
	
	public Point() {
		this (0, 0);
	}
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(Point other) {
		set(other.x, other.y);
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d)", (int) x, (int) y);
	}

	public Point interpolateTo(Point target, float i) {
		if (i == 0)
			return this;
		if (i == 1)
			return target;
		return new Point(x * (1 - i) + i * target.x, y * (1 - i) + i * target.y);
	}
}
