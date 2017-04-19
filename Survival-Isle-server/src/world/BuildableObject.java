package world;

import util.Point;

public interface BuildableObject {

	boolean payForWith(Inventory inventory);
	GameObject instanciate(Point point, GameInterface game);
	boolean hasAnyResource(Inventory inventory);
	boolean hasAllResources(Inventory inventory);
	

}
