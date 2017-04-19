package world;

import java.util.HashMap;

import util.Point;
import world.WallTile.TileType;

@SuppressWarnings("serial")
public class Turret extends BuildableObject {
	
	public Turret() {
		textureId = 8;
		resourceCost = new HashMap<ItemType, Integer>();
		resourceCost.put(ItemType.Stone, 5);
		resourceCost.put(ItemType.Wood, 5);
	}
	
	public GameObject instanciate(Point position, GameInterface game) {
		Turret turret = new Turret();
		turret.position = position;
		game.getWorld().addWallTileAtPosition((int)position.x, (int)position.y, TileType.TurretBase);
		
		return turret;
	}
}
