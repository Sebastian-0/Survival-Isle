package world;

public interface ToolAction {

	static class NoAction implements ToolAction {
		
		@Override
		public void execute(ServerWorld world, Player player) {
		}
		
		@Override
		public void playerMoved(ServerWorld world, Player player) {
		}
	}

	void execute(ServerWorld world, Player player);
	
	void playerMoved(ServerWorld world, Player player);
}
