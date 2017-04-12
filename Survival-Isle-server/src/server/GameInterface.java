package server;

import world.Player;
import world.ServerWorld;

public interface GameInterface {

	void addObject(Player object);
	void updateObject(Player Object);
	
	ServerWorld getWorld();
}
