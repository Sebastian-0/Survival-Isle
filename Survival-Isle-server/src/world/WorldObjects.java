package world;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import server.Connection;

public class WorldObjects implements Serializable {
	
	private transient List<GameObject> objects;
	
	public WorldObjects() {
		objects = new ArrayList<>();
	}

	public void update(GameInterface game) {
		Iterator<GameObject> iterator = objects.iterator();
		while (iterator.hasNext()) {
			GameObject object = iterator.next();
			object.update();
			
			if (object.shouldBeRemoved()) {
				iterator.remove();
				game.doForEachClient(c->c.sendDestroyObject(object));
			}
		}
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
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		objects = (List<GameObject>) ois.readObject();
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeObject(objects.stream().filter(o -> !(o instanceof Player)).collect(Collectors.toCollection(ArrayList::new)));
	}
}
