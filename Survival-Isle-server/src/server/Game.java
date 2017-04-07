package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import world.Player;
import world.ServerWorld;
import world.WorldObjects;

public class Game {
	private List<ServerProtocolCoder> clients = new ArrayList<>();
	private List<ServerProtocolCoder> joiningClients = new ArrayList<>();

	private ServerWorld world;
	private WorldObjects worldObjects;
	private Map<ServerProtocolCoder, Integer> playerIds = new HashMap<>();
	
	public Game() {
		world = new ServerWorld(20, 15);
		world.GenerateTerrain(0);
		
		worldObjects = new WorldObjects();
	}

	public synchronized void update(double deltaTime) {
		
		
		initNewClients();
	}

	private void initNewClients() {
		synchronized (joiningClients) {
			for (ServerProtocolCoder client : joiningClients) {
				client.sendWorld(world);
				client.sendCreateWorldObjects(worldObjects);
				clients.add(client);
				Player newPlayer = new Player();
				addObject(newPlayer);
				client.sendSetPlayer(newPlayer);
				playerIds.put(client, newPlayer.getId());
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

	public synchronized void parseClientMessage(ClientProtocol code, ServerProtocolCoder client) {
		switch (code) {
		case TO_PLAYER:
			int id = playerIds.get(client);
			Player player = (Player) worldObjects.getObject(id);
			player.parseMessage(client);
			break;
		default:
			break;
		}
	}
}
