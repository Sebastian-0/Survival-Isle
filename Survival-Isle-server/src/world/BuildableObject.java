package world;

import java.util.HashMap;
import java.util.Map;

import util.Point;

public abstract class BuildableObject extends GameObject {
	
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
		return inventory.getAmount(ItemType.Stone) > 0 || inventory.getAmount(ItemType.Wood) > 0;
	}
	
	public boolean hasAllResources(Inventory inventory) {
		return inventory.getAmount(ItemType.Stone) >= resourceCost.get(ItemType.Stone) && inventory.getAmount(ItemType.Wood) >= resourceCost.get(ItemType.Wood);
	}

	Map<ItemType, Integer> getResourceCost() {
		return resourceCost;
	}
}
