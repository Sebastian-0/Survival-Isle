package world;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.function.Consumer;

import server.ClientProtocol;
import server.ServerProtocolCoder;

@SuppressWarnings("serial")
public class Player extends GameObject implements Serializable {
	
	private Inventory inv;
	private transient Tool selectedTool;
	private transient boolean toolActive;
	
	public Player() {
		textureId = 0;
		inv = new Inventory();
		selectedTool = Tool.Pickaxe;
	}

	public Inventory getInventory() {
		return inv;
	}

	public void parseMessage(ServerProtocolCoder client, GameInterface game) {
		Consumer<ServerProtocolCoder> updateObject = c->c.sendUpdateObject(this);
		
		ClientProtocol code = client.receiveCode();
		switch (code) {
		case MoveUp:
			actOnWorld(game, 0, 1);
			game.doForEachClient(updateObject);
			updateToolAfterPlayerMove(game);
			break;
		case MoveLeft:
			actOnWorld(game, -1, 0);
			game.doForEachClient(updateObject);
			updateToolAfterPlayerMove(game);
			break;
		case MoveDown:
			actOnWorld(game, 0, -1);
			game.doForEachClient(updateObject);
			updateToolAfterPlayerMove(game);
			break;
		case MoveRight:
			actOnWorld(game, 1, 0);
			game.doForEachClient(updateObject);
			updateToolAfterPlayerMove(game);
			break;
		case SelectTool:
			int toolIndex = client.getConnection().receiveInt();
			try {
				selectedTool = Tool.values()[toolIndex];
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Tool selection failed");
			}
			break;
		case ActivateTool:
			toolActive = true;
			selectedTool.use(game.getWorld(), this);
			break;
		case DeactivateTool:
			toolActive = false;
			break;
		default:
			break;
		}
	}

	public void updateToolAfterPlayerMove(GameInterface game) {
		if (toolActive)
			selectedTool.playerMoved(game.getWorld(), this);
	}
	
	private void actOnWorld(GameInterface game, int dx, int dy) {
		if (position.x + dx < 0 || 
			position.y + dy < 0 ||
			position.x + dx >= game.getWorld().width ||
			position.y + dy >= game.getWorld().height)
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
	
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		selectedTool = Tool.Pickaxe;
	}
}
