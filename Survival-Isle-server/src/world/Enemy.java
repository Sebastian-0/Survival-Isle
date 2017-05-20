package world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import util.Point;
import world.WallTile.WallType;

public class Enemy extends GameObject implements Serializable {
	
	private static final long serialVersionUID = 1L; 

	private static final double MOVEMENT_TIME = .3;
	private static final int MAXIMUM_PATH_AGE = 1;
	private static final int PLAYER_DAMAGE = 10;
	private static final int TILE_DAMAGE = 1;
	
	private List<Point> path = new ArrayList<>();
	private double movementCounter = 0;
	private int pathAgeInSteps;
	private int maxHp;

	private boolean lockTarget;
	private boolean prioritizeCrystals;
	private GameObject target;
	
	private float timeToLive = 0;
	
	public Enemy(int difficulty) {
		type = ObjectType.Enemy;
		
		maxHp = 100 + difficulty * 5;
		hp = getMaxHp();
		
		lockTarget = Math.random() < 0.66;
		prioritizeCrystals = Math.random() < 0.66;
	}
	
	@Override
	public void update(GameInterface game, double deltaTime) {
		super.update(game, deltaTime);
		
		if (timeToLive > 0) {
			timeToLive -= deltaTime;
			if (timeToLive <= 0)
				die(game);;
		}

		if (target == null || target.shouldBeRemoved || !lockTarget) {
			List<GameObject> targets = new ArrayList<>();
			targets.addAll(game.getObjects().getObjectsOfType(Player.class));
			if (prioritizeCrystals) {
				for (int i = 0; i < targets.size(); i++) {
					if (((Player)targets.get(i)).getInventory().getAmount(ItemType.RespawnCrystal) == 0) {
						targets.remove(i);
					}
				}
			}
			targets.addAll(game.getObjects().getObjectsOfType(RespawnCrystal.class));
			target = getClosestObject(targets);
		}
		
		if (path.isEmpty()) {
			if (target != null) {
				path = game.getPathFinder().search(position, target.position);
				if (!path.isEmpty())
					path.remove(0);
			}
		}

		boolean shouldSendUpdate = isHurt && !shouldBeRemoved;
		
		animationState = AnimationState.Idle;
		movementCounter += deltaTime;
		if (movementCounter > MOVEMENT_TIME) {
			movementCounter = 0;
			if (target != null && target.position.equals(position)) {
				target.damage(game, PLAYER_DAMAGE);
				animationState = AnimationState.Attacking;
				animationTarget.x = position.x - 1 + (float)Math.floor(Math.random()*3);
				animationTarget.y = position.y - 1 + (float)Math.floor(Math.random()*3);
				shouldSendUpdate = true;
				game.doForEachClient(c->c.sendPlaySound(SoundType.EnemyAttack, getPosition()));
			} else if (!path.isEmpty()) {
				Point nextPosition = path.remove(0);
				WallTile wallTile = game.getWorld().getWallTileAtPosition(nextPosition);
				if (wallTile == null) {
					setPosition(nextPosition);
					shouldSendUpdate = true;
				} else if (wallTile.isBreakable()) {
					if (game.getWorld().attackWallTileAtPosition(nextPosition, TILE_DAMAGE)) {
						animationState = AnimationState.Attacking;
						animationTarget.set(nextPosition);
						shouldSendUpdate = true;
						
						if (wallTile.getType() == WallType.RespawnCrystal && 
								game.getWorld().getWallTileAtPosition(nextPosition) == null) {
							game.doForEachClient(c->c.sendChatMessage("Echoes", "A crystal was shattered!"));
							game.doForEachClient(c->c.sendPlaySound(SoundType.CrystalBreak));
						}
					}
				} else {
					path.clear();
				}
			}
			
			if (++pathAgeInSteps > MAXIMUM_PATH_AGE) {
				path.clear();
			}
		}
		
		if (shouldSendUpdate)
			game.doForEachClient(c -> c.sendUpdateObject(this));
		if (isHurt)
			isHurt = false;
	}

	@Override
	protected int getMaxHp() {
		return maxHp;
	}
	
	@Override
	protected void die(GameInterface game) {
		super.die(game);
		shouldBeRemoved = true;
		game.getWorld().increaseTemporaryPathCost(getPosition(), 1, 1f);
		spawnDeathEffect(game);
	}
	
	private void spawnDeathEffect(GameInterface game) {
		game.doForEachClient(s->s.sendCreateEffect(EffectType.EnemyDied, getId()));
	}
	
	public void dieByDawn(float ttl) {
		timeToLive = ttl;
	}
}
