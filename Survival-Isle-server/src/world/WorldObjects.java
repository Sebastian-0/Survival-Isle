package world;

import java.util.ArrayList;
import java.util.List;

import server.Connection;

public class WorldObjects {
	private List<Player> objects;
	
	public WorldObjects() {
		objects = new ArrayList<>();
	}
	
	public void addObject(Player object) {
		objects.add(object);
	}
	
	public void sendCreateAll(Connection connection) {
		connection.sendInt(objects.size());
		for (Player object : objects) {
			object.sendCreate(connection);
		}
	}

	public Player getObject(int id) {
		return objects.stream().filter(player -> player.getId() == id).findAny().orElse(null);
	}
}
