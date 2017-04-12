package world;

import java.io.Serializable;
import java.util.HashMap;
import server.Connection;

public class Inventory implements Serializable {

	private HashMap<ItemType, Integer> items;

	public Inventory() {
		items = new HashMap<ItemType, Integer>();
	}
	
	public void addItem(ItemType item, int amount) {
		if(items.containsKey(item))
			items.put(item, items.get(item) + amount);
		else
			items.put(item, amount);
	}
	
	public void setItem(ItemType item, int amount) {
		items.put(item, amount);
	}
	
	public boolean removeItem(ItemType item, int amount) {
		if(items.containsKey(item)) {
			if(amount >= items.get(item))
				return false;
			
			items.put(item, items.get(item) - amount);
			return true;
		}
		return false;
	}
	
	public int getAmount(ItemType item) {
		if(items.containsKey(item))
			return items.get(item).intValue();
		return 0;
	}
	
	public void setInventory(Connection coder) {
		int itemId = coder.receiveInt();
		ItemType item = ItemType.values()[itemId];
		int amount = coder.receiveInt();
		setItem(item, amount);
	}
}
