package world;

import java.util.HashMap;
import java.util.Map;

import util.Point;

public abstract class BuildableObject extends GameObject {
	
	private static final long serialVersionUID = 1L;
	
	HashMap<ItemType, Integer> resourceCost;

	public boolean payForWith(Inventory inventory) {
		if (hasAllResources(inventory)) {
			for(HashMap.Entry<ItemType, Integer> resource: resourceCost.entrySet()) {
				inventory.removeItem(resource.getKey(), resource.getValue());
			}
			return true;
		}
		return false;
	}
	
	abstract GameObject instanciate(Point point, GameInterface game);
	
	public boolean hasAnyResource(Inventory inventory) {
		return (inventory.getAmount(ItemType.Stone) > 0 && resourceCost.getOrDefault(ItemType.Stone,0) > 0) || 
			(inventory.getAmount(ItemType.Wood) > 0 && resourceCost.getOrDefault(ItemType.Wood,0) > 0) ||
			(inventory.getAmount(ItemType.RespawnCrystal) > 0 && resourceCost.getOrDefault(ItemType.RespawnCrystal,0) > 0);
	}
	
	public boolean hasAllResources(Inventory inventory) {
		return inventory.getAmount(ItemType.Stone) >= resourceCost.getOrDefault(ItemType.Stone,0) && 
			inventory.getAmount(ItemType.Wood) >= resourceCost.getOrDefault(ItemType.Wood,0) &&
			inventory.getAmount(ItemType.RespawnCrystal) >= resourceCost.getOrDefault(ItemType.RespawnCrystal,0);
	}

	Map<ItemType, Integer> getResourceCost() {
		return resourceCost;
	}
}
