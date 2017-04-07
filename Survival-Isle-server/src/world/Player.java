package world;

import util.Point;

public class Player {
	
	private static int idCounter;
	
	private int id;
	private Point position;
	
	public Player() {
		id = idCounter++;
		position = new Point(0, 0);
	}
	
	public int getId() {
		return id;
	}
}
