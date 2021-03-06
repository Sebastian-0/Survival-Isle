package world;

import java.util.Map;

import world.WallTile.WallType;

public enum Tool {

	Pickaxe,
	WoodWall(new BuildWallAction(WallType.WoodWall)),
	StoneWall(new BuildWallAction(WallType.StoneWall)),
	Turret(new BuildObjectAction(new Turret())),
	RespawnCrystal(new BuildObjectAction(new RespawnCrystal()));
	
	private ToolAction action;
	
	private Tool() {
		action = new ToolAction.NoAction();
	}
	
	private Tool(ToolAction action) {
		this.action = action;
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
	
	public Map<ItemType, Integer> getResourceCost() {
		return action.getResourceCost();
	}
}
