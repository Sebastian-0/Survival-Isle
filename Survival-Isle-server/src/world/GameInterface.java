package world;

import java.util.function.Consumer;

import server.ServerProtocolCoder;

public interface GameInterface {

	void addObject(GameObject object);
	void doForEachClient(Consumer<ServerProtocolCoder> function);
	
	PathFinder getPathFinder();
	
	ServerWorld getWorld();
}
