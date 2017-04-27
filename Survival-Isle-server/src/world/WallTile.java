package world;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WallTile implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private WallType type;
	private int health;
	private boolean isBreakable;
	private Map<ItemType, Integer> itemDrops;
	
	public enum WallType {
		Water(false, 3),
		Forest(true, 2, ItemType.Wood.ordinal(), 1), 
		Mountain(true, 3, ItemType.Stone.ordinal(), 2),
		WoodWall(true, 5, ItemType.Wood.ordinal(), 2),
		StoneWall(true, 10, ItemType.Stone.ordinal(), 2),
		EnemySpawn(false, 1), 
		TurretBase(true, 4, ItemType.Wood.ordinal(), 5, ItemType.Stone.ordinal(), 5), 
		RespawnCrystal(true, 10, ItemType.RespawnCrystal.ordinal(), 1);
		

		public final int health;
		public final boolean isBreakable;
		public final Map<ItemType, Integer> itemDrops;
		
		private WallType(boolean isBreakable, int health, int... itemDrops) {
			this.health = health;
			this.isBreakable = isBreakable;
			this.itemDrops = new HashMap<>();
			for (int i = 0; i < itemDrops.length; i += 2) {
				this.itemDrops.put(ItemType.values()[itemDrops[i]], itemDrops[i + 1]);
			}
		}

		public boolean payForWith(Inventory inventory) {
			if (!hasAllResources(inventory))
				return false;
			
			for (Map.Entry<ItemType, Integer> itemCost : itemDrops.entrySet()) {
				ItemType item = itemCost.getKey();
				int cost = itemCost.getValue();
				inventory.removeItem(item, cost);
			}
			return true;
		}
		
		public boolean hasAnyResource(Inventory inventory) {
			for (Map.Entry<ItemType, Integer> itemCost : itemDrops.entrySet()) {
				ItemType item = itemCost.getKey();
				if (inventory.getAmount(item) > 0)
					return true;
			}
			return false;
		}
		
		public boolean hasAllResources(Inventory inventory) {
			for (Map.Entry<ItemType, Integer> itemCost : itemDrops.entrySet()) {
				ItemType item = itemCost.getKey();
				int cost = itemCost.getValue();
				if (inventory.getAmount(item) < cost)
					return false;
			}
			return true;
		}
		
		public Map<ItemType, Integer> getResourceCost() {
			return itemDrops;
		}
	}
	
	public WallTile(WallType type) {
		this.type = type;
		isBreakable = type.isBreakable;
		health = type.health;
		itemDrops = type.itemDrops;
	}
	
	
	public WallType getType() {
		return type;
	}
	
	
	public boolean isBreakable() {
		return isBreakable;
	}


	public boolean damage(int amount) {
		health -= amount;
		if (health <= 0) {
			return true;
		}
		return false;
	}


	public void dropItems(Inventory inventory) {
		for (Map.Entry<ItemType, Integer> itemDrop : itemDrops.entrySet()) {
			inventory.addItem(itemDrop.getKey(), itemDrop.getValue());
		}
	}
	
	public Map<ItemType, Integer> getItemDrops() {
		return itemDrops;
	}
}
