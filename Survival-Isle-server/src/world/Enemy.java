package world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import util.Point;

public class Enemy extends GameObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<Point> path = new ArrayList<>();
	private double movementCounter = 0;
	
	public Enemy() {
		textureId = 4;
	}
	
	@Override
	public void update(GameInterface game, double deltaTime) {
		super.update(game, deltaTime);
		
		if (path.isEmpty()) {
			List<Player> players = game.getObjects().getObjectsOfType(Player.class);
			GameObject closestPlayer = getClosestObject(game, players);
			path = game.getPathFinder().search(position, closestPlayer.position);
		}

		movementCounter += deltaTime;
		if (movementCounter > .3) {
			movementCounter = 0;
			if (!path.isEmpty()) {
				setPosition(path.remove(0));
				game.doForEachClient(c -> c.sendUpdateObject(this));
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
