package world;

import world.WallTile.TileType;

public class BuildWallAction implements ToolAction {
	
	private TileType tileToBuild;
	
	public BuildWallAction(TileType tileToBuild) {
		this.tileToBuild = tileToBuild;
	}
	
	public TileType getTileToBuild() {
		return tileToBuild;
	}

	@Override
	public void execute(GameInterface game, Player player) {
		int x = (int) player.getPosition().x;
		int y = (int) player.getPosition().y;
		if (game.getWorld().getWallTileAtPosition(x, y) == null && tileToBuild.payForWith(player.getInventory())) {
			game.getWorld().addWallTileAtPosition(x, y, tileToBuild);
		}
	}
	
	@Override
	public void playerMoved(GameInterface game, Player player) {
		execute(game, player);
	}

}
