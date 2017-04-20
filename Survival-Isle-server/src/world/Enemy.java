package world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import util.Point;

public class Enemy extends GameObject implements Serializable {
	
	private static final double MOVEMENT_TIME = .3;

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
			if (!players.isEmpty()) {
				double minDistance = Float.MAX_VALUE;
				Player closestPlayer = null;
				for (Player player : players) {
					double distance = Point.distanceSq(position.x, position.y, player.position.x, player.position.y);
					if (distance < minDistance) {
						minDistance = distance;
						closestPlayer = player;
					}
				}
				path = game.getPathFinder().search(position, closestPlayer.position);
				if (!path.isEmpty())
					path.remove(0);
			}
		}
		
		movementCounter += deltaTime;
		if (movementCounter > MOVEMENT_TIME) {
			movementCounter = 0;
			if (!path.isEmpty()) {
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
