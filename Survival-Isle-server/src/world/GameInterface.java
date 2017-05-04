package world;

import java.util.function.Consumer;

import server.ServerProtocolCoder;

public interface GameInterface {

	void addObject(GameObject object);
	void doForEachClient(Consumer<ServerProtocolCoder> function);
	void playerDied(Player player, int crystalCount);
	
	PathFinder getPathFinder();
	
	ServerWorld getWorld();
	WorldObjects getObjects();
	
	void checkForRespawnCrystals();
	boolean isGameOver();
}
