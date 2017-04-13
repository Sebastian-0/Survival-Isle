package world;

import java.io.Serializable;

import server.ClientProtocol;
import server.Connection;
import server.ServerProtocolCoder;
import server.Tool;
import util.Point;

@SuppressWarnings("serial")
public class Player implements Serializable {
	
	public static int idCounter;
	
	public enum AnimationState {
		Idle(0),
		Attacking(1);
		
		public final int id;
		
		AnimationState(int id) {
			this.id = id;
		}
	}
	
	private int id;
	private int textureId;
	private Point position;
	private Point attackTarget;
	private Inventory inv;
	private AnimationState animationState;
	private Tool selectedTool = Tool.Pickaxe;
	
	public Player() {
		id = idCounter++;
		textureId = 0;
		position = new Point(0, 0);
		attackTarget = new Point(0, 0);
		inv = new Inventory();
		animationState = AnimationState.Idle;
	}
	
	public int getId() {
		return id;
	}
	
	public Point getPosition() {
		return position;
	}

	public Inventory getInventory() {
		return inv;
	}

	public void parseMessage(ServerProtocolCoder client, GameInterface game) {
		ClientProtocol code = client.receiveCode();
		switch (code) {
		case MoveUp:
			actOnWorld(client, game, 0, 1);
			game.updateObject(this);
			break;
		case MoveLeft:
			actOnWorld(client, game, -1, 0);
			game.updateObject(this);
			break;
		case MoveDown:
			actOnWorld(client, game, 0, -1);
			game.updateObject(this);
			break;
		case MoveRight:
			actOnWorld(client, game, 1, 0);
			game.updateObject(this);
			break;
		case SelectTool:
			int toolIndex = client.getConnection().receiveInt();
			try {
				selectedTool = Tool.values()[toolIndex];
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Tool selection failed");
			}
			break;
		default:
			break;
		}
	}
	
	private void actOnWorld(ServerProtocolCoder client, GameInterface game, int dx, int dy) {
		if (position.x + dx < 0 || position.y + dy < 0 || position.x + dx >= game.getWorld().width || position.y + dy >= game.getWorld().height)
			return;

		animationState = AnimationState.Idle;
		WallTile tile = game.getWorld().getWallTileAtPosition((int) position.x + dx, (int) position.y + dy);
		if (tile == null) {
			position.x += dx;
			position.y += dy;
		}
		else if (tile.isBreakable() && selectedTool == Tool.Pickaxe) {
			if (game.getWorld().attackWallTileAtPosition((int)position.x+dx, (int)position.y+dy, 1, this)) {
				animationState = AnimationState.Attacking;
				attackTarget.x = position.x+dx;
				attackTarget.y = position.y+dy;
			}
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
		connection.sendInt(animationState.id);
		
		if (animationState == AnimationState.Attacking) {
			connection.sendInt((int)attackTarget.x);
			connection.sendInt((int)attackTarget.y);
		}
	}
	
	public void sendDestroy(Connection connection) {
		connection.sendInt(id);
	}

	public void setPosition(Point position) {
		this.position = position;
	}
}
