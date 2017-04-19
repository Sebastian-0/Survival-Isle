package world;

public interface BuildableObject {

	boolean payForWith(Inventory inventory);
	GameObject instanciate();
	boolean hasAnyResource(Inventory inventory);
	boolean hasAllResources(Inventory inventory);
	

}
