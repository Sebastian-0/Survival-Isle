package world;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WallTile implements Serializable {
	private int id;
	private int health;
	private boolean isBreakable;
	private Map<ItemType, Integer> itemDrops;
	
	public enum TileType {
		Forest(0, true, 2, ItemType.wood.ordinal(), 1);
		

		public final int id;
		public final int health;
		public final boolean isBreakable;
		public final Map<ItemType, Integer> itemDrops;
		
		private TileType(int id, boolean isBreakable, int health, int... itemDrops) {
			this.id = id;
			this.health = health;
			this.isBreakable = isBreakable;
			this.itemDrops = new HashMap<>();
			for (int i = 0; i < itemDrops.length; i += 2) {
				this.itemDrops.put(ItemType.values()[itemDrops[i]], itemDrops[i + 1]);
			}
		}
	}
	
	public WallTile(TileType type) {
		id = type.id;
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
