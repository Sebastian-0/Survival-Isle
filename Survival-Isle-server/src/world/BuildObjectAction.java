package world;

public class BuildObjectAction implements ToolAction {
	
	private BuildableObject objectToBuild;
	
	public BuildObjectAction(BuildableObject objectToBuild) {
		this.objectToBuild = objectToBuild;
	}
	
	public GameObject getObjectToBuild() {
		return objectToBuild.instanciate();
	}

	@Override
	public void execute(GameInterface game, Player player) {
		int x = (int) player.getPosition().x;
		int y = (int) player.getPosition().y;
		if (game.getWorld().getWallTileAtPosition(x, y) == null && objectToBuild.payForWith(player.getInventory())) {
			game.addObject(objectToBuild.instanciate());
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

}
