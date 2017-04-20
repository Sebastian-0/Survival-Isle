package world;

import java.util.HashMap;

import util.Point;
import world.WallTile.TileType;

public class Turret extends BuildableObject {
	
	private static final long serialVersionUID = 1L;
	
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
		game.getWorld().addWallTileAtPosition(position, TileType.TurretBase);
		
		return turret;
	}
	
	@Override
	public void update(GameInterface game, double deltaTime) {
		super.update(game, deltaTime);
		
		WallTile tile = game.getWorld().getWallTileAtPosition(position);
		if (tile == null || tile.getId() != TileType.TurretBase.ordinal()) {
			shouldBeRemoved = true;

			//game.doForEachClient(c->c.sendDestroyObject(this));
			//game.removeObject(this);
		}
	}
	

	@Override
	protected int getMaxHp() {
		return 0;
	}
}
