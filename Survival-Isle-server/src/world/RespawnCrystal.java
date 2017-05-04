package world;

import java.util.HashMap;

import util.Point;
import world.WallTile.WallType;

public class RespawnCrystal extends BuildableObject {
	
	private static final long serialVersionUID = 1L;
	
	public RespawnCrystal() {
		type = ObjectType.RespawnCrystal;
		resourceCost = new HashMap<ItemType, Integer>();
		resourceCost.put(ItemType.RespawnCrystal, 1);
	}
	
	@Override
	public GameObject instanciate(Point position, GameInterface game) {
		RespawnCrystal crystal = new RespawnCrystal();
		crystal.position = position;
		game.getWorld().addWallTileAtPosition(position, WallType.RespawnCrystal);
		
		return crystal;
	}
	
	@Override
	public void update(GameInterface game, double deltaTime) {
		super.update(game, deltaTime);
		
		WallTile tile = game.getWorld().getWallTileAtPosition(position);
		if (tile == null || tile.getType() != WallType.RespawnCrystal) {
			shouldBeRemoved = true;
			game.doForEachClient(c->c.sendChatMessage("Echoes", "A crystal was shattered!"));
			game.checkForRespawnCrystals();
		}
		
		sendUpdateIfHurt(game);
	}
	
	@Override
	protected int getMaxHp() {
		return 0;
	}
}
