package world;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

import server.ClientProtocol;
import server.ServerProtocolCoder;
import util.Point;

public class Player extends GameObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final double REVIVE_TIME = 5;
	private static final int DAMAGE = 5;
	public static final double MOVEMENT_TIME = .3;
	
	
	private Inventory inventory;
	private transient Tool selectedTool;
	private transient boolean toolActive;
	private Queue<ClientProtocol> moves;
	private double movementCounter;
	private double reviveCountdown;
	private int deathCount;
	
	public Player() {
		type = ObjectType.Player;
		inventory = new Inventory();
		inventory.addItem(ItemType.RespawnCrystal, 1);
		selectedTool = Tool.Pickaxe;
		moves = new LinkedList<>();
	}

	public Inventory getInventory() {
		return inventory;
	}
	
	@Override
	public void update(GameInterface game, double deltaTime) {
		super.update(game, deltaTime);
			
		movementCounter += deltaTime;
		if (moves.size() > 0) {
			updateMovement(game, deltaTime);
		}
		
		sendUpdateIfHurt(game);
	}

	private void updateMovement(GameInterface game, double deltaTime) {
		if (movementCounter > MOVEMENT_TIME) {
			movementCounter = 0;
			Consumer<ServerProtocolCoder> updateObject = c->c.sendUpdateObject(this);
			ClientProtocol move = moves.poll();
			switch (move) {
			case MoveUp:
				if (!shouldBeRemoved && !game.isGameOver()) {
					actOnWorld(game, 0, 1);
					game.doForEachClient(updateObject);
					updateToolAfterPlayerMove(game);
				}
				break;
			case MoveLeft:
				if (!shouldBeRemoved && !game.isGameOver()) {
					actOnWorld(game, -1, 0);
					game.doForEachClient(updateObject);
					updateToolAfterPlayerMove(game);
				}
				break;
			case MoveDown:
				if (!shouldBeRemoved && !game.isGameOver()) {
					actOnWorld(game, 0, -1);
					game.doForEachClient(updateObject);
					updateToolAfterPlayerMove(game);
				}
				break;
			case MoveRight:
				if (!shouldBeRemoved && !game.isGameOver()) {
					actOnWorld(game, 1, 0);
					game.doForEachClient(updateObject);
					updateToolAfterPlayerMove(game);
				}
				break;
			default:
				break;
			}
		}
	}

	public void parseMessage(ServerProtocolCoder client, GameInterface game) {
		ClientProtocol code = client.receiveCode();
		switch (code) {
		case MoveUp:
		case MoveLeft:
		case MoveDown:
		case MoveRight:
			moves.add(code);
			break;
		case SelectTool:
			int toolIndex = client.getConnection().receiveInt();
			if (!shouldBeRemoved && !game.isGameOver()) {
				try {
					selectedTool = Tool.values()[toolIndex];
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Tool selection failed");
				}
			}
			break;
		case ActivateTool:
			if (!shouldBeRemoved && !game.isGameOver()) {
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
			if (game.getWorld().attackWallTileAtPosition((int)position.x+dx, (int)position.y+dy, DAMAGE, this)) {
				animationState = AnimationState.Attacking;
				animationTarget.x = position.x+dx;
				animationTarget.y = position.y+dy;
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
	protected void die(GameInterface game) {
		if (!isDead) {
			reviveCountdown = REVIVE_TIME;
			System.out.println("DEAD!");
			shouldBeRemoved = true;
			
			int crystalCount = inventory.getAmount(ItemType.RespawnCrystal);
			
			if (crystalCount > 0) {
				inventory.removeItem(ItemType.RespawnCrystal, inventory.getAmount(ItemType.RespawnCrystal));
				deathCount = 0;
			} else {
				deathCount++;
			}

			game.playerDied(this, crystalCount, deathCount);
			super.die(game);
		}
	}

	public boolean revive(double deltaTime) {
		reviveCountdown -= deltaTime;
		if (reviveCountdown <= 0) {
			shouldBeRemoved = false;
			isDead = false;
			hp = getMaxHp();
			animationState = AnimationState.Idle;
			return true;
		}
		return false;
	}
}
