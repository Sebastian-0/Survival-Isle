package world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import util.Point;
import world.GameObject.AnimationState;

public class Enemy extends GameObject implements Serializable {
	
	private static final double MOVEMENT_TIME = .3;

	private static final long serialVersionUID = 1L;

	private static final int ATTACK_DAMAGE = 10;
	
	private List<Point> path = new ArrayList<>();
	private double movementCounter = 0;
	
	public Enemy() {
		textureId = 4;
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
				closestPlayer.damage(ATTACK_DAMAGE);
				animationState = AnimationState.Attacking;
				attackTarget.x = position.x - 1 + (float)Math.floor(Math.random()*3);
				attackTarget.y = position.y - 1 + (float)Math.floor(Math.random()*3);
				game.doForEachClient(c -> c.sendUpdateObject(this));
			} else if (!path.isEmpty()) {
				Point nextPosition = path.remove(0);
				if (game.getWorld().getWallTileAtPosition(nextPosition) == null) {
					setPosition(nextPosition);
					game.doForEachClient(c -> c.sendUpdateObject(this));
				} else {
					path.clear();
				}
			}
		}
	}

	@Override
	protected int getMaxHp() {
		return 100;
	}
	
	@Override
	protected void die() {
		super.die();
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
