package world;

import server.ClientProtocol;
import server.Connection;
import server.GameInterface;
import server.ServerProtocolCoder;
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

	public void parseMessage(ServerProtocolCoder client, GameInterface game) {
		ClientProtocol code = client.receiveCode();
		switch (code) {
		case MOVE_UP:
			position.y += 1;
			break;
		case MOVE_LEFT:
			position.x -= 1;
			break;
		case MOVE_DOWN:
			position.y -= 1;
			break;
		case MOVE_RIGHT:
			position.x += 1;
			break;

		default:
			break;
		}
		System.out.println("Player position: " + position);
		game.updateObject(this);
	}
		
	public void sendCreate(Connection connection) {
		connection.sendInt(id);
		connection.sendInt((int)position.x);
		connection.sendInt((int)position.y);
	}

	public void sendUpdate(Connection connection) {
		connection.sendInt(id);
		connection.sendInt((int)position.x);
		connection.sendInt((int)position.y);
	}
}
