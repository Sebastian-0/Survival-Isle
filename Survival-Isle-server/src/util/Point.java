package util;

import java.io.Serializable;

public class Point implements Serializable {
	public float x;
	public float y;
	
	public Point() {
		this (0, 0);
	}
	
	public Point(Point other) {
		this (other.x, other.y);
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
	
	public Point interpolateTo(Point target, float i) {
		if (i == 0)
			return this;
		if (i == 1)
			return target;
		return new Point(x * (1 - i) + i * target.x, y * (1 - i) + i * target.y);
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", (int) x, (int) y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point other = (Point) obj;
			return other.x == x && other.y == y;
		}
		return false;
	}

	public static double distanceSq(double x1, double y1, double x2, double y2)	{
		x1 -= x2;
		y1 -= y2;
		return (x1 * x1 + y1 * y1);
	}
}
