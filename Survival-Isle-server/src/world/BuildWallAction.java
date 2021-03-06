package world;

import java.util.Map;

import world.WallTile.WallType;

public class BuildWallAction implements ToolAction {
	
	private WallType tileToBuild;
	
	public BuildWallAction(WallType tileToBuild) {
		this.tileToBuild = tileToBuild;
	}

	@Override
	public void execute(GameInterface game, Player player) {
		if (game.getWorld().getWallTileAtPosition(player.getPosition()) == null && tileToBuild.payForWith(player.getInventory())) {
			game.getWorld().addWallTileAtPosition(player.getPosition(), tileToBuild);
		}
	}
	
	@Override
	public void playerMoved(GameInterface game, Player player) {
		execute(game, player);
	}
	
	@Override
	public boolean hasAnyResource(Inventory inventory) {
		return tileToBuild.hasAnyResource(inventory);
	}
	
	@Override
	public boolean hasAllResources(Inventory inventory) {
		return tileToBuild.hasAllResources(inventory);
	}

	@Override
	public Map<ItemType, Integer> getResourceCost() {
		return tileToBuild.getResourceCost();
	}
}
