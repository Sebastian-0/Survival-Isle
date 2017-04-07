package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import world.Player;
import world.ServerWorld;
import world.WorldObjects;

public class Game implements GameInterface {
	private List<ServerProtocolCoder> clients = new ArrayList<>();
	private List<ServerProtocolCoder> joiningClients = new ArrayList<>();
	private List<ServerProtocolCoder> leavingClients = new ArrayList<>();

	private ServerWorld world;
	private WorldObjects worldObjects;
	private Map<ServerProtocolCoder, Integer> playerIds = new HashMap<>();
	
	public Game() {
		world = new ServerWorld(20, 15);
		world.GenerateTerrain(0);
		
		worldObjects = new WorldObjects();
	}

	public synchronized void update(double deltaTime) {
		
		removeLeavingClients();
		initNewClients();
		clients.forEach(client -> client.flush());
	}

	private void removeLeavingClients() {
		synchronized (leavingClients) {
			for (ServerProtocolCoder client : leavingClients) {
				clients.remove(client);
				removeObject(worldObjects.getObject(playerIds.get(client)));
				playerIds.remove(client);
			}
			leavingClients.clear();
		}
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
	
	@Override
	public void addObject(Player object) {
		worldObjects.addObject(object);
		for (ServerProtocolCoder client : clients) {
			client.sendCreateObject(object);
		}
	}
	
	@Override
	public void updateObject(Player object) {
		for (ServerProtocolCoder client : clients) {
			client.sendUpdateObject(object);
		}
	}

	public void removeObject(Player object) {
		worldObjects.removeObject(object.getId());
		for (ServerProtocolCoder client : clients) {
			client.sendDestroyObject(object);
		}
	}
	
	public void addClient(ServerProtocolCoder client) {
		synchronized (joiningClients) {
			joiningClients.add(client);
		}
	}

	public void removeClient(ServerProtocolCoder client) {
		synchronized (leavingClients) {
			leavingClients.add(client);
		}
	}

	public synchronized void parseClientMessage(ClientProtocol code, ServerProtocolCoder client) {
		switch (code) {
		case TO_PLAYER:
			int id = playerIds.get(client);
			Player player = (Player) worldObjects.getObject(id);
			player.parseMessage(client, this);
			break;
		default:
			break;
		}
	}
}
