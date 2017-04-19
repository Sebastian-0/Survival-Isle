package world;

import util.Point;
import world.WallTile.TileType;

@SuppressWarnings("serial")
public class Turret extends GameObject implements BuildableObject {
	private final int STONE_COST = 5;
	private final int WOOD_COST = 5;
	private GameInterface game;
	
	public Turret() {
		textureId = 8;
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
	public void update() {
		super.update();
		
		WallTile tile = game.getWorld().getWallTileAtPosition((int)position.x, (int)position.y);
		if (tile == null || tile.getId() != TileType.TurretBase.ordinal()) {
			shouldBeRemoved = true;
		}
	}
	
	

	@Override
	public boolean payForWith(Inventory inventory) {
		if (hasAllResources(inventory)) {
			inventory.removeItem(ItemType.Stone, STONE_COST);
			inventory.removeItem(ItemType.Wood, WOOD_COST);
			return true;
		}
		return false;
	}

	@Override
	public boolean hasAnyResource(Inventory inventory) {
		return inventory.getAmount(ItemType.Stone) > 0 || inventory.getAmount(ItemType.Wood) > 0;
	}

	@Override
	public boolean hasAllResources(Inventory inventory) {
		return inventory.getAmount(ItemType.Stone) >= STONE_COST && inventory.getAmount(ItemType.Wood) >= WOOD_COST;
	}

}
