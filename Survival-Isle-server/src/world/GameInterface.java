package world;

public interface GameInterface {

	void addObject(GameObject object);
	void updateObject(GameObject Object);
	
	ServerWorld getWorld();
}
