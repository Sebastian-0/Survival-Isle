package world;

import server.ClientProtocol;
import server.Connection;
import server.GameInterface;
import server.ServerProtocolCoder;
import util.Point;

public class Player {
	
	private static int idCounter;
	
	private int id;
	private int textureId;
	private Point position;
	
	public Player() {
		id = idCounter++;
		textureId = 0;
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
			if (game.getWallTileAtPosition((int)position.x, (int)position.y+1) == null)
				position.y += 1;
			break;
		case MOVE_LEFT:
			if (game.getWallTileAtPosition((int)position.x-1, (int)position.y) == null)
				position.x -= 1;
			break;
		case MOVE_DOWN:
			if (game.getWallTileAtPosition((int)position.x, (int)position.y-1) == null)
				position.y -= 1;
			break;
		case MOVE_RIGHT:
			if (game.getWallTileAtPosition((int)position.x+1, (int)position.y) == null)
				position.x += 1;
			break;

		default:
			break;
		}
		game.updateObject(this);
	}
		
	public void sendCreate(Connection connection) {
		sendUpdate(connection);
		connection.sendInt(textureId);
	}

	public void sendUpdate(Connection connection) {
		connection.sendInt(id);
		connection.sendInt((int)position.x);
		connection.sendInt((int)position.y);
	}

	public void sendDestroy(Connection connection) {
		connection.sendInt(id);
	}
}
