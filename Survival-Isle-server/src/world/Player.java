package world;

import java.io.Serializable;

import server.ClientProtocol;
import server.Connection;
import server.ServerProtocolCoder;
import util.Point;

@SuppressWarnings("serial")
public class Player implements Serializable {
	
	public static int idCounter;
	
	private int id;
	private int textureId;
	private Point position;
	private Inventory inv;
	
	public Player() {
		id = idCounter++;
		textureId = 0;
		position = new Point(0, 0);
		inv = new Inventory();
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
			actOnWorld(client, game, 0, 1);
			break;
		case MOVE_LEFT:
			actOnWorld(client, game, -1, 0);
			break;
		case MOVE_DOWN:
			actOnWorld(client, game, 0, -1);
			break;
		case MOVE_RIGHT:
			actOnWorld(client, game, 1, 0);
			break;
		default:
			break;
		}
		game.updateObject(this);
	}
	
	private void actOnWorld(ServerProtocolCoder client, GameInterface game, int dx, int dy) {
		if (position.x + dx < 0 || position.y + dy < 0 || position.x + dx >= game.getWorld().width || position.y + dy >= game.getWorld().height)
			return;
		
		WallTile tile = game.getWorld().getWallTileAtPosition((int) position.x + dx, (int) position.y + dy);
		if (tile == null) {
			position.x += dx;
			position.y += dy;
		}
		else if (tile.isBreakable()) {
			if (game.getWorld().attackWallTileAtPosition((int)position.x+dx, (int)position.y+dy, 1, this)) {
				//Attacking = true
			}
				//Attacking = false
		}
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
