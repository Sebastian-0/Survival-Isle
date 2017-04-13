package world;

import java.util.ArrayList;
import java.util.List;

import server.Connection;

public class WorldObjects {
	private List<GameObject> objects;
	
	public WorldObjects() {
		objects = new ArrayList<>();
	}
	
	public void addObject(GameObject object) {
		objects.add(object);
	}
	
	public void removeObject(GameObject object) {
		objects.remove(object);
	}

	public void sendCreateAll(Connection connection) {
		connection.sendInt(objects.size());
		for (GameObject object : objects) {
			object.sendCreate(connection);
		}
	}

	public GameObject getObject(int id) {
		return objects.stream().filter(object -> object.getId() == id).findAny().orElse(null);
	}
}
