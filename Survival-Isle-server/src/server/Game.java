package server;

import java.util.ArrayList;
import java.util.List;

import world.World;

public class Game {
	
	private World world;
	private List<ServerProtocolCoder> clients = new ArrayList<>();
	private List<ServerProtocolCoder> joiningClients = new ArrayList<>();
	
	public Game() {
		world = new World(20, 15);
		world.GenerateTerrain(0);
	}

	public void update(double deltaTime) {
		
		
		synchronized (joiningClients) {
			joiningClients.forEach(client -> client.sendWorld(world));
			clients.addAll(joiningClients);
			joiningClients.clear();
		}
	}
	
	public void addClient(ServerProtocolCoder client) {
		synchronized (joiningClients) {
			joiningClients.add(client);
		}
	}
}
