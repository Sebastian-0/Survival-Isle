package world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import util.Point;

public class Enemy extends GameObject implements Serializable {
	
	private static final long serialVersionUID = 1L; 

	private static final double MOVEMENT_TIME = .3;
	private static final int MAXIMUM_PATH_AGE = 1;
	private static final int ATTACK_DAMAGE = 10;

	private static final int DAMAGE = 1;
	
	private List<Point> path = new ArrayList<>();
	private double movementCounter = 0;
	private int pathAgeInSteps;
	
	public Enemy() {
		type = ObjectType.Enemy;
	}
	
	@Override
	public void update(GameInterface game, double deltaTime) {
		super.update(game, deltaTime);

		List<Player> players = game.getObjects().getObjectsOfType(Player.class);
		GameObject closestPlayer = getClosestObject(players);
		
		if (path.isEmpty()) {
			if (closestPlayer != null) {
				path = game.getPathFinder().search(position, closestPlayer.position);
				if (!path.isEmpty())
					path.remove(0);
			}
		}

		animationState = AnimationState.Idle;
		movementCounter += deltaTime;
		if (movementCounter > MOVEMENT_TIME) {
			movementCounter = 0;
			if (closestPlayer != null && closestPlayer.position.equals(position)) {
				closestPlayer.damage(game, ATTACK_DAMAGE);
				animationState = AnimationState.Attacking;
				attackTarget.x = position.x - 1 + (float)Math.floor(Math.random()*3);
				attackTarget.y = position.y - 1 + (float)Math.floor(Math.random()*3);
				game.doForEachClient(c -> c.sendUpdateObject(this));
			} else if (!path.isEmpty()) {
				Point nextPosition = path.remove(0);
				WallTile wallTile = game.getWorld().getWallTileAtPosition(nextPosition);
				if (wallTile == null) {
					setPosition(nextPosition);
					game.doForEachClient(c -> c.sendUpdateObject(this));
				} else if (wallTile.isBreakable()) {
					if (game.getWorld().attackWallTileAtPosition(nextPosition, DAMAGE)) {
						animationState = AnimationState.Attacking;
						attackTarget.set(nextPosition);
						game.doForEachClient(c -> c.sendUpdateObject(this));
					}
				} else {
					path.clear();
				}
			}
			
			if (++pathAgeInSteps > MAXIMUM_PATH_AGE) {
				path.clear();
			}
		}
	}

	@Override
	protected int getMaxHp() {
		return 100;
	}
	
	@Override
	protected void die(GameInterface game) {
		super.die(game);
		shouldBeRemoved = true;
	}
	
	/*
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
	}
	*/
}
