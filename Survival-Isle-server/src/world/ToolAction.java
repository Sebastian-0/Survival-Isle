package world;

import java.util.Map;

public interface ToolAction {

	static class NoAction implements ToolAction {
		
		@Override
		public void execute(GameInterface game, Player player) {
		}
		
		@Override
		public void playerMoved(GameInterface game, Player player) {
		}
		
		@Override
		public boolean hasAnyResource(Inventory inventory) {
			return true;
		}
		
		@Override
		public boolean hasAllResources(Inventory inventory) {
			return true;
		}

		@Override
		public Map<ItemType, Integer> getResourceCost() {
			return null;
		}
	}

	void execute(GameInterface game, Player player);
	
	void playerMoved(GameInterface game, Player player);

	boolean hasAnyResource(Inventory inventory);

	boolean hasAllResources(Inventory inventory);
	
	Map<ItemType, Integer> getResourceCost();
}
