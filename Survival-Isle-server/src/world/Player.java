package world;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.function.Consumer;

import server.ClientProtocol;
import server.ServerProtocolCoder;
import util.Point;

public class Player extends GameObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final double REVIVE_TIME = 5;
	private Inventory inventory;
	private transient Tool selectedTool;
	private transient boolean toolActive;
	private GameInterface game;
	private double reviveCountdown;
	
	public Player(GameInterface game) {
		textureId = 0;
		inventory = new Inventory();
		inventory.addItem(ItemType.Respawn, 1);
		selectedTool = Tool.Pickaxe;
		this.game = game;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void parseMessage(ServerProtocolCoder client, GameInterface game) {
		Consumer<ServerProtocolCoder> updateObject = c->c.sendUpdateObject(this);
		
		ClientProtocol code = client.receiveCode();
		switch (code) {
		case MoveUp:
			if (!shouldBeRemoved) {
				actOnWorld(game, 0, 1);
				game.doForEachClient(updateObject);
				updateToolAfterPlayerMove(game);
			}
			break;
		case MoveLeft:
			if (!shouldBeRemoved) {
				actOnWorld(game, -1, 0);
				game.doForEachClient(updateObject);
				updateToolAfterPlayerMove(game);
			}
			break;
		case MoveDown:
			if (!shouldBeRemoved) {
				actOnWorld(game, 0, -1);
				game.doForEachClient(updateObject);
				updateToolAfterPlayerMove(game);
			}
			break;
		case MoveRight:
			if (!shouldBeRemoved) {
				actOnWorld(game, 1, 0);
				game.doForEachClient(updateObject);
				updateToolAfterPlayerMove(game);
			}
			break;
		case SelectTool:
			int toolIndex = client.getConnection().receiveInt();
			if (!shouldBeRemoved) {
				try {
					selectedTool = Tool.values()[toolIndex];
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Tool selection failed");
				}
			}
			break;
		case ActivateTool:
			if (!shouldBeRemoved) {
				toolActive = true;
				selectedTool.use(game, this);
			}
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
			selectedTool.playerMoved(game, this);
	}
	
	private void actOnWorld(GameInterface game, int dx, int dy) {
		if (position.x + dx < 0 || 
			position.y + dy < 0 ||
			position.x + dx >= game.getWorld().getWidth() ||
			position.y + dy >= game.getWorld().getHeight())
			return;

		animationState = AnimationState.Idle;
		Point newPosition = new Point(position.x + dx, position.y + dy);
		WallTile tile = game.getWorld().getWallTileAtPosition(newPosition);
		if (tile == null) {
			position = newPosition;
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

	@Override
	protected int getMaxHp() {
		return 100;
	}

	@Override
	protected void die() {
		if (!isDead) {
			reviveCountdown = REVIVE_TIME;
			System.out.println("DEAD!");
			game.playerDied(this);
			shouldBeRemoved = true;
			super.die();
		}
	}

	public boolean revive(double deltaTime) {
		reviveCountdown -= deltaTime;
		if (reviveCountdown <= 0) {
			System.out.println("REVIVED!");
			shouldBeRemoved = false;
			isDead = false;
			hp = getMaxHp();
			animationState = AnimationState.Idle;
			return true;
		}
		return false;
	}
}
