package world;

public interface ToolAction {

	void execute(ServerWorld world, Player player);
	
	static class NoAction implements ToolAction {
		
		@Override
		public void execute(ServerWorld world, Player player) {
		}
	}
}
