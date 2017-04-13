package world;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import server.Connection;

public class Inventory implements Serializable {

	private HashMap<ItemType, Integer> items;
	private boolean updated;

	public Inventory() {
		items = new HashMap<ItemType, Integer>();
	}
	
	public void addItem(ItemType item, int amount) {
		updated = true;
		if(items.containsKey(item))
			items.put(item, items.get(item) + amount);
		else
			items.put(item, amount);
	}
	
//	public void setItem(ItemType item, int amount) {
//		items.put(item, amount);
//	}
	
	public boolean removeItem(ItemType item, int amount) {
		if(getAmount(item) < amount)
			return false;

		items.put(item, items.get(item) - amount);
		updated = true;
		return true;
	}
	
	public int getAmount(ItemType item) {
		if(items.containsKey(item))
			return items.get(item).intValue();
		return 0;
	}
	
	public boolean isUpdated() {
		return updated;
	}

	public void sendInventory(Connection connection) {
		connection.sendInt(items.size());
		for (Entry<ItemType, Integer> item : items.entrySet()) {
			connection.sendInt(item.getKey().ordinal());
			connection.sendInt(item.getValue());
		}
		updated = false;
	}
	
	public void receiveInventory(Connection connection) {
		int entries = connection.receiveInt();
		for (int i = 0; i < entries; i++) {
			int itemId = connection.receiveInt();
			ItemType item = ItemType.values()[itemId];
			int amount = connection.receiveInt();
			items.put(item, amount);
		}
	}
}
