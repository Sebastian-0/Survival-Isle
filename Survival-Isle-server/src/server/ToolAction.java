package server;

import util.Point;
import world.ServerWorld;

public interface ToolAction {

	void execute(ServerWorld world, Point playerPosition);
	
	static class NoAction implements ToolAction {
		
		@Override
		public void execute(ServerWorld world, Point playerPosition) {
		}
	}
}
