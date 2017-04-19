package world;

import world.WallTile.TileType;

public class BuildWallAction implements ToolAction {
	
	private TileType tileToBuild;
	
	public BuildWallAction(TileType tileToBuild) {
		this.tileToBuild = tileToBuild;
	}

	@Override
	public void execute(ServerWorld world, Player player) {
		int x = (int) player.getPosition().x;
		int y = (int) player.getPosition().y;
		if (world.getWallTileAtPosition(x, y) == null && tileToBuild.payForWith(player.getInventory())) {
			world.addWallTileAtPosition(x, y, tileToBuild);
		}
	}
	
	@Override
	public void playerMoved(ServerWorld world, Player player) {
		execute(world, player);
	}

}
