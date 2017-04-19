package world;

@SuppressWarnings("serial")
public class Turret extends GameObject implements BuildableObject {

	public Turret() {
		textureId = 8;
	}
	
	@Override
	public boolean payForWith(Inventory inventory) {
		if (inventory.getAmount(ItemType.Stone) > 5 && inventory.getAmount(ItemType.Wood) > 5) {
			inventory.removeItem(ItemType.Stone, 5);
			inventory.removeItem(ItemType.Wood, 5);
			return true;
		}
		return false;
	}

	@Override
	public GameObject instanciate() {
		return new Turret();
	}

}
