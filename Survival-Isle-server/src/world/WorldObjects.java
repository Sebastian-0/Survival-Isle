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
		
	}
}
