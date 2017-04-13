package world;

import java.util.function.Consumer;

import server.ServerProtocolCoder;

public interface GameInterface {

	void addObject(Player object);
	void doForEachClient(Consumer<ServerProtocolCoder> function);
	
	ServerWorld getWorld();
}
