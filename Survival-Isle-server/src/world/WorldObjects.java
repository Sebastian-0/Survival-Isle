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
	
	private static final long serialVersionUID = 1L;

	private transient List<GameObject> objects;
	private transient List<GameObject> addList;
	
	public WorldObjects() {
		objects = new ArrayList<>();
		addList = new ArrayList<>();
	}

	public void update(GameInterface game, double deltaTime) {
		for (GameObject gameObject : addList) {
			objects.add(gameObject);
		}
		addList.clear();
		
		Iterator<GameObject> iterator = objects.iterator();
		while (iterator.hasNext()) {
			GameObject object = iterator.next();
			object.update(game, deltaTime);
			
			if (object.shouldBeRemoved()) {
				iterator.remove();
				game.doForEachClient(c->c.sendDestroyObject(object));
			}
		}
	}
	
	public void addObject(GameObject object) {
		objects.add(object);
	}
	
	public void addObjectLater(GameObject object) {
		addList.add(object);
	}
	
	public void removeObject(GameObject object) {
		objects.remove(object);
	}
	
	public void sendCreateAll(Connection connection) {
		connection.sendInt(objects.size());
		objects.forEach((o)->o.sendCreate(connection));
	}

	public GameObject getObject(int id) {
		return objects.stream().filter(object -> object.getId() == id).findAny().orElse(null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends GameObject> List<T> getObjectsOfType(Class<T> type) {
		return (List<T>) objects.stream().filter(o -> (type.isInstance(o))).collect(Collectors.toCollection(ArrayList::new));
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
