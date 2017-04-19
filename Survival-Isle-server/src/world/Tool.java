package world;

import world.WallTile.TileType;

public enum Tool {

	Pickaxe,
	WoodWall(new BuildWallAction(TileType.WoodWall)),
	StoneWall(new BuildWallAction(TileType.StoneWall));
	
	private ToolAction action;
	private boolean active;
	
	private Tool() {
		action = new ToolAction.NoAction();
	}
	
	private Tool(ToolAction action) {
		this.action = action;
	}
	
	public void activate(ServerWorld world, Player player) {
		action.execute(world, player);
		active = true;
	}

	public void deactivate(ServerWorld world, Player player) {
		active = false;
	}
	
	public void playerMoved(ServerWorld world, Player player) {
		if (active)
			action.playerMoved(world, player);
	}
}
