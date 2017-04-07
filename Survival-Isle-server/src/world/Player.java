package world;

import server.Connection;
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
	
	public Point getPosition() {
		return position;
	}

	public void sendCreate(Connection connection) {
		connection.sendInt(id);
		connection.sendInt((int)position.x);
		connection.sendInt((int)position.y);
	}
}
