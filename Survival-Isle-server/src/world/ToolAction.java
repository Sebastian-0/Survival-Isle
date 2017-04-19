package world;

public interface ToolAction {

	static class NoAction implements ToolAction {
		
		@Override
		public void execute(ServerWorld world, Player player) {
		}
		
		@Override
		public void playerMoved(ServerWorld world, Player player) {
		}
		
		@Override
		public boolean hasAnyResource(Inventory inventory) {
			return true;
		}
		
		@Override
		public boolean hasAllResources(Inventory inventory) {
			return true;
		}
	}

	void execute(ServerWorld world, Player player);
	
	void playerMoved(ServerWorld world, Player player);

	boolean hasAnyResource(Inventory inventory);

	boolean hasAllResources(Inventory inventory);
}
