package world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import util.Point;

@SuppressWarnings("serial")
public class Enemy extends GameObject implements Serializable {
	
	private List<Point> path = new ArrayList<>();
//	private double movementCounter = 0;
	
	public Enemy() {
		textureId = 4;
	}
	
	@Override
	public void update(GameInterface game) {
		if (path.isEmpty()) {
			List<Player> players = game.getObjects().getObjectsOfType(Player.class);

			double minDistance = Float.MAX_VALUE;
			Player closestPlayer = null;
			for (Player player : players) {
				double distance = java.awt.Point.distance(position.x, position.y, player.position.x, player.position.y);
				if (distance < minDistance) {
					minDistance = distance;
					closestPlayer = player;
				}
			}

			path = game.getPathFinder().search(position, closestPlayer.position);
		}
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
