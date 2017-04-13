package world;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class WallTile implements Serializable {
	private int id;
	private int health;
	private boolean isBreakable;
	private Map<ItemType, Integer> itemDrops;
	
	public enum TileType {
		Forest(true, 2, ItemType.Wood.ordinal(), 1),
		Mountain(true, 3, ItemType.Stone.ordinal(), 2),
		WoodWall(true, 5, ItemType.Wood.ordinal(), 2),
		StoneWall(true, 10, ItemType.Stone.ordinal(), 2);
		

		public final int health;
		public final boolean isBreakable;
		public final Map<ItemType, Integer> itemDrops;
		
		private TileType(boolean isBreakable, int health, int... itemDrops) {
			this.health = health;
			this.isBreakable = isBreakable;
			this.itemDrops = new HashMap<>();
			for (int i = 0; i < itemDrops.length; i += 2) {
				this.itemDrops.put(ItemType.values()[itemDrops[i]], itemDrops[i + 1]);
			}
		}

		public boolean payForWith(Inventory inventory) {
			for (Map.Entry<ItemType, Integer> itemCost : itemDrops.entrySet()) {
				ItemType item = itemCost.getKey();
				int cost = itemCost.getValue();
				if (inventory.getAmount(item) < cost) {
					return false;
				}
			}
			for (Map.Entry<ItemType, Integer> itemCost : itemDrops.entrySet()) {
				ItemType item = itemCost.getKey();
				int cost = itemCost.getValue();
				inventory.removeItem(item, cost);
			}
			return true;
		}
	}
	
	public WallTile(TileType type) {
		id = type.ordinal();
		isBreakable = type.isBreakable;
		health = type.health;
		itemDrops = type.itemDrops;
	}
	
	
	public int getId() {
		return id;
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
}
