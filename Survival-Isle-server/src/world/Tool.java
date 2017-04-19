package world;

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
	
	public ToolAction getAction() {
		return action;
	}
	
	public void use(GameInterface game, Player player) {
		action.execute(game, player);
	}
	
	public void playerMoved(GameInterface game, Player player) {
		action.playerMoved(game, player);
	}
	
	public boolean hasAnyResource(Inventory inventory) {
		return action.hasAnyResource(inventory);
	}
	
	public boolean hasAllResources(Inventory inventory) {
		return action.hasAllResources(inventory);
	}
}
