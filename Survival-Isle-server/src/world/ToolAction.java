package world;

public interface ToolAction {

	static class NoAction implements ToolAction {
		
		@Override
		public void execute(GameInterface game, Player player) {
		}
		
		@Override
		public void playerMoved(GameInterface game, Player player) {
		}
	}

	void execute(GameInterface game, Player player);
	
	void playerMoved(GameInterface game, Player player);
}
