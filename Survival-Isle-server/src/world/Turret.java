package world;

import java.util.HashMap;

import util.Point;
import world.WallTile.TileType;

@SuppressWarnings("serial")
public class Turret extends BuildableObject {
	private GameInterface game;
	
	public Turret() {
		textureId = 8;
		resourceCost = new HashMap<ItemType, Integer>();
		resourceCost.put(ItemType.Stone, 5);
		resourceCost.put(ItemType.Wood, 5);
	}
	
	@Override
	public GameObject instanciate(Point position, GameInterface game) {
		Turret turret = new Turret();
		turret.position = position;
		turret.game = game;
		game.getWorld().addWallTileAtPosition((int)position.x, (int)position.y, TileType.TurretBase);
		
		return turret;
	}
	
	@Override
	public void update(GameInterface game, double deltaTime) {
		super.update(game, deltaTime);
		
		WallTile tile = game.getWorld().getWallTileAtPosition((int)position.x, (int)position.y);
		if (tile == null || tile.getId() != TileType.TurretBase.ordinal()) {
			shouldBeRemoved = true;

			//game.doForEachClient(c->c.sendDestroyObject(this));
			//game.removeObject(this);
		}
	}
}
