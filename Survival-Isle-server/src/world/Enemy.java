package world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import util.Point;

public class Enemy extends GameObject implements Serializable {
	
	private static final long serialVersionUID = 1L; 

	private static final double MOVEMENT_TIME = .3;
	private static final int MAXIMUM_PATH_AGE = 1;
	private static final int PLAYER_DAMAGE = 10;
	private static final int TILE_DAMAGE = 1;
	
	private List<Point> path = new ArrayList<>();
	private double movementCounter = 0;
	private int pathAgeInSteps;
	
	public Enemy() {
		type = ObjectType.Enemy;
	}
	
	@Override
	public void update(GameInterface game, double deltaTime) {
		super.update(game, deltaTime);

		List<GameObject> targets = new ArrayList<>();
		targets.addAll(game.getObjects().getObjectsOfType(Player.class));
		targets.addAll(game.getObjects().getObjectsOfType(RespawnCrystal.class));
		GameObject closestPlayer = getClosestObject(targets);
		
		if (path.isEmpty()) {
			if (closestPlayer != null) {
				path = game.getPathFinder().search(position, closestPlayer.position);
				if (!path.isEmpty())
					path.remove(0);
			}
		}

		boolean shouldSendUpdate = isHurt && !shouldBeRemoved;
		
		animationState = AnimationState.Idle;
		movementCounter += deltaTime;
		if (movementCounter > MOVEMENT_TIME) {
			movementCounter = 0;
			if (closestPlayer != null && closestPlayer.position.equals(position)) {
				closestPlayer.damage(game, PLAYER_DAMAGE);
				animationState = AnimationState.Attacking;
				animationTarget.x = position.x - 1 + (float)Math.floor(Math.random()*3);
				animationTarget.y = position.y - 1 + (float)Math.floor(Math.random()*3);
				shouldSendUpdate = true;
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
		return 100;
	}
	
	@Override
	protected void die(GameInterface game) {
		super.die(game);
		shouldBeRemoved = true;
		game.getWorld().increasePathMultiplier(getPosition(), 1, 0.1f);
	}
}
