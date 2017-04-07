package server;

import java.util.ArrayList;
import java.util.List;

import world.Player;
import world.ServerWorld;
import world.WorldObjects;

public class Game {
	private List<ServerProtocolCoder> clients = new ArrayList<>();
	private List<ServerProtocolCoder> joiningClients = new ArrayList<>();

	private ServerWorld world;
	private WorldObjects worldObjects;
	
	public Game() {
		world = new ServerWorld(20, 15);
		world.GenerateTerrain(0);
		
		worldObjects = new WorldObjects();
	}

	public void update(double deltaTime) {
		
		
		initNewClients();
	}

	private void initNewClients() {
		synchronized (joiningClients) {
			for (ServerProtocolCoder client : clients) {
				client.sendWorld(world);
				client.sendCreateWorldObjects(worldObjects);
				clients.add(client);
				Player newPlayer = new Player();
				addObject(newPlayer);
				client.sendSetPlayer(newPlayer);
			}

			joiningClients.clear();
		}
	}
	
	public void addObject(Player object) {
		worldObjects.addObject(object);
		for (ServerProtocolCoder client : clients) {
			client.sendCreateObject(object);
		}
	}
	
	public void addClient(ServerProtocolCoder client) {
		synchronized (joiningClients) {
			joiningClients.add(client);
		}
	}
}
