package server;

import util.Point;
import world.ServerWorld;
import world.WallTile.TileType;

public class BuildWallAction implements ToolAction {
	
	private TileType tileToBuild;
	
	public BuildWallAction(TileType tileToBuild) {
		this.tileToBuild = tileToBuild;
	}

	@Override
	public void execute(ServerWorld world, Point playerPosition) {
		world.addWallTileAtPosition((int) playerPosition.x, (int) playerPosition.y, tileToBuild);
	}

}
