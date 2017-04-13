package server;

import world.Player;
import world.ServerWorld;

public interface ToolAction {

	void execute(ServerWorld world, Player player);
	
	static class NoAction implements ToolAction {
		
		@Override
		public void execute(ServerWorld world, Player player) {
		}
	}
}
