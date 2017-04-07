package world;

import java.util.ArrayList;
import java.util.List;

import server.Connection;

public class WorldObjects {
	private List<Player> players;
	
	public WorldObjects() {
		players = new ArrayList<>();
	}
	
	public void addObject(Player player) {
		players.add(player);
	}
	
	public void sendCreateAll(Connection connection) {
		
	}
}
