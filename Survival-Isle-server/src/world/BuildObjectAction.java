package world;

import java.util.Map;

public class BuildObjectAction implements ToolAction {
	
	private BuildableObject objectToBuild;
	
	public BuildObjectAction(BuildableObject objectToBuild) {
		this.objectToBuild = objectToBuild;
	}

	@Override
	public void execute(GameInterface game, Player player) {
		if (game.getWorld().getWallTileAtPosition(player.getPosition()) == null && objectToBuild.payForWith(player.getInventory())) {
			GameObject object = objectToBuild.instanciate(player.getPosition(), game);
			game.addObject(object);
		}
	}
	
	@Override
	public void playerMoved(GameInterface game, Player player) {
		execute(game, player);
	}

	@Override
	public boolean hasAnyResource(Inventory inventory) {
		return objectToBuild.hasAnyResource(inventory);
	}

	@Override
	public boolean hasAllResources(Inventory inventory) {
		return objectToBuild.hasAllResources(inventory);
	}

	@Override
	public Map<ItemType, Integer> getResourceCost() {
		return objectToBuild.getResourceCost();
	}

}
