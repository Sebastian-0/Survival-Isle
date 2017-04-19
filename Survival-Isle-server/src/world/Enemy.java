package world;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

import server.ServerProtocolCoder;
import util.Point;

@SuppressWarnings("serial")
public class Enemy extends GameObject implements Serializable {
	
	private GameInterface game; 
	private List<Point> positions;
	
	private long millis;
	
	public Enemy(GameInterface game) {
		this.game = game;
		textureId = 4;
	}
	
	public void pathToCoast() {
		Point target = new Point(game.getWorld().getNewSpawnPoint());
		positions = game.getPathFinder().search(getPosition(), target);
		millis = System.currentTimeMillis();
	}
	
	@Override
	public void update() {
		if (System.currentTimeMillis() - millis > 250) {
			millis = System.currentTimeMillis();
			if (!positions.isEmpty()) {
				setPosition(positions.remove(0));
				Consumer<ServerProtocolCoder> updateObject = c->c.sendUpdateObject(this);
				game.doForEachClient(updateObject);
			}
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
