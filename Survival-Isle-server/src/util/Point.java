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
	
	@Override
	public String toString() {
		return String.format("(%d, %d)", (int) x, (int) y);
	}
}
