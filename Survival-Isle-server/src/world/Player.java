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
	public static final int MAX_HEALTH = 100;
	
	
	private Inventory inventory;
	private transient Tool selectedTool;
	private transient boolean toolActive;
	private Queue<ClientProtocol> movesH;
	private Queue<ClientProtocol> movesV;
	private double movementCounterH;
	private double movementCounterV;
	private double reviveCountdown;
	private int deathCount;
	
	public Player() {
		type = ObjectType.Player;
		inventory = new Inventory();
		inventory.addItem(ItemType.RespawnCrystal, 1);
		selectedTool = Tool.Pickaxe;
		movesH = new LinkedList<>();
		movesV = new LinkedList<>();
	}

	public Inventory getInventory() {
		return inventory;
	}
	
	@Override
	public void update(GameInterface game, double deltaTime) {
		super.update(game, deltaTime);
		
		if (movementCounterH < MOVEMENT_TIME)
			movementCounterH += deltaTime;
		if (movementCounterV < MOVEMENT_TIME)
			movementCounterV += deltaTime;

		updateMovement(game, deltaTime);
		
		sendUpdateIfHurt(game);
	}

	private void updateMovement(GameInterface game, double deltaTime) {
		if (!movesH.isEmpty() && movementCounterH >= MOVEMENT_TIME) {
			movementCounterH -= MOVEMENT_TIME;
			ClientProtocol move = movesH.poll();
			move(game, move);
		}
		
		if (!movesV.isEmpty() && movementCounterV >= MOVEMENT_TIME) {
			movementCounterV -= MOVEMENT_TIME;
			ClientProtocol move = movesV.poll();
			move(game, move);
		}
	}

	private void move(GameInterface game, ClientProtocol move) {
		Consumer<ServerProtocolCoder> updateObject = c->c.sendUpdateObject(this);
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

	public void parseMessage(ServerProtocolCoder client, GameInterface game) {
		ClientProtocol code = client.receiveCode();
		switch (code) {
		case MoveUp:
		case MoveDown:
			if (!movesV.isEmpty() && !movesV.contains(code))
				movesV.clear();
			else if (movesV.isEmpty())
				movesV.add(code);
			break;
		case MoveLeft:
		case MoveRight:
			if (!movesH.isEmpty() && !movesH.contains(code))
				movesH.clear();
			else if (movesH.isEmpty())
				movesH.add(code);
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
		return MAX_HEALTH;
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

			game.doForEachClient(c->c.sendPlaySound(SoundType.PlayerDeath, getPosition()));
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

	public void restoreHealth() {
		hp = getMaxHp();
	}
}
