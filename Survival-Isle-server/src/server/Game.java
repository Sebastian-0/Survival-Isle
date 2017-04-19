package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import world.Enemy;
import world.GameInterface;
import world.GameObject;
import world.Inventory;
import world.PathFinder;
import world.Player;
import world.ServerWorld;
import world.Time;
import world.WorldObjects;

@SuppressWarnings("serial")
public class Game implements GameInterface, Serializable {
	private transient List<ServerProtocolCoder> clients = new ArrayList<>();
	private transient List<ServerProtocolCoder> joiningClients = new ArrayList<>();
	private transient List<ServerProtocolCoder> leavingClients = new ArrayList<>();

	private ServerWorld world;
	private transient WorldObjects worldObjects;
	private transient PathFinder pathFinder;
	private Map<ServerProtocolCoder, Player> players = new HashMap<>();

	private Time time;
	
	public Game() {
		world = new ServerWorld(200, 150, this);
		world.generateTerrain();
		
		worldObjects = new WorldObjects();
		pathFinder = new PathFinder(world);
		
		time = new Time();
	}

	public synchronized void update(double deltaTime) {
		spawnEnemies(deltaTime);
		time.advanceTime(this, deltaTime);
		updateWallTiles();
		sendInventoryUpdates();
		removeLeavingClients();
		initNewClients();
		clients.forEach(client -> client.flush());
	}

	private void spawnEnemies(double deltaTime) {
		if (!time.isDaytime() && Math.random() < deltaTime) {
			Enemy e = new Enemy();
			worldObjects.addObject(e);
			e.setPosition(world.getRandomEnemySpawnPoint());

			for (ServerProtocolCoder client : clients) {
				client.sendCreateObject(e);
			}
		}
	}

	private void updateWallTiles() {
		if (world.shouldUpdateWallTiles()) {
			for (ServerProtocolCoder client : clients) {
				client.sendUpdateWallTiles(world);
			}
			world.clearWallTileUpdateList();
		}
	}
	
	private void sendInventoryUpdates() {
		for (ServerProtocolCoder client : clients) {
			Inventory inventory = players.get(client).getInventory();
			if (inventory.isUpdated())
				client.sendInventory(inventory);
		}
	}

	private void removeLeavingClients() {
		synchronized (leavingClients) {
			for (ServerProtocolCoder client : leavingClients) {
				clients.remove(client);
				removeObject(players.get(client));
			}
			leavingClients.clear();
		}
	}

	private void initNewClients() {
		synchronized (joiningClients) {
			for (ServerProtocolCoder client : joiningClients) {
				if (clients.contains(client)) {
					client.sendFailedToConnect();
					client.disconnect();
				} else {
					initNewClient(client);
				}
			}

			joiningClients.clear();
		}
	}

	private void initNewClient(ServerProtocolCoder client) {
		client.sendWorld(world);
		client.sendCreateWorldObjects(worldObjects);
		client.sendTimeEvent(time.isDaytime() ? 1 : 2);
		clients.add(client);
		Player player;
		if (players.containsKey(client)) {
			player = players.get(client);
		} else {
			player = new Player();
			player.setPosition(world.getNewSpawnPoint());
		}
		addObject(player);
		client.sendSetPlayer(player);
		players.put(client, player);
		new Thread(new ClientListener(this, client)).start();
		System.out.println("Client connected: " + client);
	}
	
	@Override
	public void addObject(GameObject object) {
		worldObjects.addObject(object);
		for (ServerProtocolCoder client : clients) {
			client.sendCreateObject(object);
		}
	}
	
	@Override
	public void doForEachClient(Consumer<ServerProtocolCoder> function) {
		clients.forEach(function);
	}

	public void removeObject(GameObject object) {
		worldObjects.removeObject(object);
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
		case ToPlayer:
			Player player = players.get(client);
			player.parseMessage(client, this);
			break;
		case SendClose:
			client.acknowledgeClose();
			Thread.currentThread().interrupt();
			break;
		case AckClose:
			Thread.currentThread().interrupt();
			break;
		default:
			System.out.println("Server received unexpected message: " + code);
			break;
		}
	}

	@Override
	public ServerWorld getWorld() {
		return world;
	}
	
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		clients = new ArrayList<>();
		joiningClients = new ArrayList<>();
		leavingClients = new ArrayList<>();
		worldObjects = new WorldObjects();
		pathFinder = new PathFinder(world);
		GameObject.idCounter = ois.readInt();
	}
	
	private synchronized void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeInt(GameObject.idCounter);
	}

	public synchronized void stop() {
		clients.forEach(client -> client.sendClose());
	}
}
