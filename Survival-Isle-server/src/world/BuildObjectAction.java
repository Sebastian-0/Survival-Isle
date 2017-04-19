package world;

import java.util.Map;

import util.Point;

public class BuildObjectAction implements ToolAction {
	
	private BuildableObject objectToBuild;
	
	public BuildObjectAction(BuildableObject objectToBuild) {
		this.objectToBuild = objectToBuild;
	}

	@Override
	public void execute(GameInterface game, Player player) {
		int x = (int) player.getPosition().x;
		int y = (int) player.getPosition().y;
		if (game.getWorld().getWallTileAtPosition(x, y) == null && objectToBuild.payForWith(player.getInventory())) {
			GameObject object = objectToBuild.instanciate(new Point(x, y), game);
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
