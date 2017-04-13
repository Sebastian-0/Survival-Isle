package server;

import world.Player;
import world.ServerWorld;
import world.WallTile.TileType;

public enum Tool {

	Pickaxe,
	WoodWall(new BuildWallAction(TileType.WoodWall)),
	StoneWall(new BuildWallAction(TileType.StoneWall));
	
	private ToolAction action;
	
	private Tool() {
		action = new ToolAction.NoAction();
	}
	
	private Tool(ToolAction action) {
		this.action = action;
	}
	
	public void use(ServerWorld world, Player player) {
		action.execute(world, player);
	}
}
