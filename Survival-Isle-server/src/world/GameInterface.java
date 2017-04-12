package world;

public interface GameInterface {

	void addObject(Player object);
	void updateObject(Player Object);
	
	ServerWorld getWorld();
}
