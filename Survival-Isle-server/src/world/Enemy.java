package world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import util.Point;

public class Enemy extends GameObject implements Serializable {
	
	private static final double MOVEMENT_TIME = .3;

	private static final long serialVersionUID = 1L;

	private static final int ATTACK_DAMAGE = 5;
	
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
		
		movementCounter += deltaTime;
		if (movementCounter > MOVEMENT_TIME) {
			movementCounter = 0;
			if (closestPlayer.position.equals(position)) {
				closestPlayer.damage(ATTACK_DAMAGE);
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
