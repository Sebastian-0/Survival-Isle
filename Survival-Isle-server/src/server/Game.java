package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import util.Point;
import world.Enemy;
import world.GameInterface;
import world.GameObject;
import world.Inventory;
import world.ItemType;
import world.PathFinder;
import world.Player;
import world.RespawnCrystal;
import world.ServerWorld;
import world.SoundType;
import world.Time;
import world.Undead;
import world.WorldObjects;

public class Game implements GameInterface, TimeListener, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private transient List<ServerProtocolCoder> clients = new ArrayList<>();
	private transient List<ServerProtocolCoder> joiningClients = new ArrayList<>();
	private transient List<ServerProtocolCoder> leavingClients = new ArrayList<>();

	private ServerWorld world;
	private WorldObjects worldObjects;
	private transient PathFinder pathFinder;
	private Map<ServerProtocolCoder, Player> players = new HashMap<>();
	private List<Player> deadPlayers = new ArrayList<>();

	private Time time;

	private boolean gameOver;
	private boolean shouldStopUpdating;
	
	public Game(int width, int height) {
		world = new ServerWorld(width, height, this);
		world.generateTerrain();
		
		worldObjects = new WorldObjects();
		pathFinder = new PathFinder(world);
		
		time = new Time();
	}

	public synchronized void update(double deltaTime) {
		if (!shouldStopUpdating && !clients.isEmpty()) {
			if (gameOver)
				shouldStopUpdating = true;
			spawnEnemies(deltaTime);
			time.advanceTime(this, deltaTime);
			worldObjects.update(this, deltaTime);
			updateDeadPlayers(deltaTime);
			
			updateWallTiles();
			sendInventoryUpdates();
		}
		removeLeavingClients();
		initNewClients();
		clients.forEach(client -> client.flush());
	}

	private void spawnEnemies(double deltaTime) {
		double difficulty = 3 / (0.5+clients.size()/2.0);
		if (!time.isDaytime() && Math.random() * difficulty / Math.min(time.getDay()+1,5) < deltaTime) {
			Enemy e = new Enemy(time.getDay());
			worldObjects.addObject(e);
			e.setPosition(world.getRandomEnemySpawnPoint());

			for (ServerProtocolCoder client : clients) {
				client.sendCreateObject(e);
			}
		}
	}

	private void updateDeadPlayers(double deltaTime) {
		for (int i = 0; i < deadPlayers.size(); i++) {
			Player player = deadPlayers.get(i);
			if (player.revive(deltaTime)) {
				player.setPosition(getRespawnPoint(player));
				addObjectNow(player);
				players.entrySet().stream().filter((entry)->(entry.getValue() == player)).findAny().orElse(null).getKey().sendSetPlayer(player);
				deadPlayers.remove(i);
				i--;
			}
		}
	}

	private Point getRespawnPoint(Player player) {
		List<GameObject> respawnLocations = new ArrayList<>();
		respawnLocations.addAll(worldObjects.getObjectsOfType(RespawnCrystal.class));
		worldObjects.getObjectsOfType(Player.class).stream().filter(p -> p.getInventory().getAmount(ItemType.RespawnCrystal) > 0).forEach(p -> respawnLocations.add(p));
		
		GameObject respawn = player.getClosestObject(respawnLocations);
		if (respawn != null)
			return respawn.getPosition();
		else
			return world.getNewSpawnPoint();
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
				Player player = players.get(client);
				removeObject(player);
				dropPlayerCrystals(player);
				doForEachClient(c -> c.sendChatMessage(client.getName(), "has left the game"));
				if (deadPlayers.contains(player)) {
					deadPlayers.remove(player);
					player.revive(Double.POSITIVE_INFINITY);
					player.setPosition(getRespawnPoint(player));
				}
			}
			leavingClients.clear();
		}
	}

	private void dropPlayerCrystals(Player player) {
		int amount = player.getInventory().getAmount(ItemType.RespawnCrystal);
		player.getInventory().removeItem(ItemType.RespawnCrystal, amount);
		for (; amount > 0; amount--) {
			Point p = world.getFreeWallTile(player.getPosition());
			if (p != null) {
				new RespawnCrystal().instanciate(p, this);
			} else {
				checkForRespawnCrystals();
			}
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
		doForEachClient(c -> c.sendChatMessage(client.getName(), "has joined the game"));
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
		addObjectNow(player);
		client.sendSetPlayer(player);
		client.sendInventory(player.getInventory());
		players.remove(client); // You must remove the pair first, otherwise the key won't be replaced (only the value will be)
		players.put(client, player);
		new ClientListener(this, client).start();
		if (gameOver)
			client.sendGameOver();
	}
	
	public void addObjectNow(GameObject object) {
		worldObjects.addObject(object);
		clients.forEach((c)->c.sendCreateObject(object));
	}
	
	@Override
	public void addObject(GameObject object) {
		worldObjects.addObjectLater(object);
		clients.forEach((c)->c.sendCreateObject(object));
	}
	
	@Override
	public void doForEachClient(Consumer<ServerProtocolCoder> function) {
		clients.forEach(function);
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

	private void removeObject(GameObject object) {
		worldObjects.removeObject(object);
		clients.forEach((c)->c.sendDestroyObject(object));
	}
	
	@Override
	public void dayBegun(int day) {
		doForEachClient(c->c.sendTimeEvent(1));
		System.out.println("Dawn of day " + day);
		players.forEach((c, p) -> {
			p.restoreHealth();
			if (clients.contains(c))
				c.sendUpdateObject(p);
		});
		List<Enemy> enemies = worldObjects.getObjectsOfType(Enemy.class);
		enemies.forEach(e->e.dieByDawn((float)Math.random()+0.01f));
		List<Undead> undead = worldObjects.getObjectsOfType(Undead.class);
		undead.forEach(e->e.dieByDawn((float)Math.random()+0.01f));
	}
	
	@Override
	public void nightBegun(int day) {
		doForEachClient(c->c.sendTimeEvent(0));
		System.out.println("Dusk of day " + day);
		world.decreaseTemporaryPathCost();
		
		for (Player p : worldObjects.getObjectsOfType(Player.class)) {
			if (p.getInventory().getAmount(ItemType.RespawnCrystal) == 0 && p.getDeathCount() > 0) {
				for (int i = 0; i < p.getDeathCount(); i++) {
					Undead e = new Undead(p, time.getDay());
					worldObjects.addObject(e);
					e.setPosition(world.getRandomEnemySpawnPoint());
			
					for (ServerProtocolCoder client : clients) {
						client.sendCreateObject(e);
					}	
				}
				
				if (Math.random() < p.getDeathCount() * 0.1) {
					String name = "Noname";
					ServerProtocolCoder spc = getClientFromPlayer(p);
					if (spc != null) {
						name = spc.getName();
					}
					final String n = name;
					doForEachClient(c->c.sendChatMessage("Echoes", "Chills run down " + n + "'s back. They come for you!"));
				}
			}
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
		case SendChatMessage:
			String message = client.getConnection().receiveString();
			if (message.startsWith("/")) {
				parseCheatCode(client, message);
			}
			else
				doForEachClient(c -> c.sendChatMessage(client.getName(), message));
			break;
		case DebugRequest:
			client.sendDebug(this);
			break;
		default:
			System.out.println("Server received unexpected message: " + code);
			break;
		}
	}

	private void parseCheatCode(ServerProtocolCoder client, String message) {
		if (message.equals("/night")) {
			time.advanceToNight(this);
			doForEachClient(c -> c.sendChatMessage("", "Night suddenly falls on " + client.getName() + "."));
		} else if (message.equals("/kill")) {
			doForEachClient(c -> c.sendChatMessage("", "The pressure proved too much for " + client.getName() + "..."));
			players.get(client).damage(this, 100000);
		}else if (message.equals("/day")) {
			time.advanceToDay(this);
			doForEachClient(c -> c.sendChatMessage("", "Dawn comes quickly as " + client.getName() + " praises the sun."));
		} else if (message.equals("/payday")) {
			players.get(client).getInventory().addItem(ItemType.Stone, 100);
			players.get(client).getInventory().addItem(ItemType.Wood, 100);
			doForEachClient(c -> c.sendChatMessage("", "Through shady stock market deals, " + client.getName() + " suddenly got filthy rich."));
		}
	}

	@Override
	public ServerWorld getWorld() {
		return world;
	}
	
	@Override
	public WorldObjects getObjects() {
		return worldObjects;
	}
	
	@Override
	public PathFinder getPathFinder() {
		return pathFinder;
	}
	
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		clients = new ArrayList<>();
		joiningClients = new ArrayList<>();
		leavingClients = new ArrayList<>();
		pathFinder = new PathFinder(world);
		deadPlayers = new ArrayList<>();
		GameObject.idCounter = ois.readInt();
	}
	
	private synchronized void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeInt(GameObject.idCounter);
	}

	public synchronized void stop() {
		clients.forEach(client -> client.sendClose());
	}

	@Override
	public void playerDied(Player player, int crystalCount, int deathCount) {
		deadPlayers.add(player);

		String name = "Noname";
		ServerProtocolCoder spc = getClientFromPlayer(player);
		if (spc != null) {
			name = spc.getName();
			spc.sendDeathCount(deathCount);
		}
		final String n = name;

		if (crystalCount == 0)
			doForEachClient(c->c.sendChatMessage("Echoes", n + " has died."));
		else if (crystalCount == 1)
			doForEachClient(c->c.sendChatMessage("Echoes", "As " + n + " dies, their crystal shatters!"));
		else
			doForEachClient(c->c.sendChatMessage("Echoes", "As " + n + " dies, their " + crystalCount + " crystals shatter!"));
		
		checkForRespawnCrystals();
	}

	private ServerProtocolCoder getClientFromPlayer(Player player) {
		Iterator<Entry<ServerProtocolCoder, Player>> iterator = players.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ServerProtocolCoder, Player> e = iterator.next();
			if (e.getValue() == player) {
				return e.getKey();
			}
		}
		return null;
	}
	
	@Override
	public void checkForRespawnCrystals() {
		if (players.values().stream().filter(p->p.getInventory().getAmount(ItemType.RespawnCrystal) > 0).findAny().orElse(null) == null) {
			List<RespawnCrystal> crystals = getObjects().getObjectsOfType(RespawnCrystal.class);
			if (crystals.size() == 0 || crystals.stream().filter(c->!c.shouldBeRemoved()).findAny().orElse(null) == null) {
				gameOver();
			}
		}
	}

	private void gameOver() {
		if (!gameOver) {
			gameOver = true;
			doForEachClient(c->c.sendGameOver());
			doForEachClient(c->c.sendChatMessage("Echoes", "With the breaking of the last crystal, the world is plunged into darkness..."));
			doForEachClient(c->c.sendPlaySound(SoundType.GameOver));
			System.out.println("GAME OVER!");
		}
	}
	
	@Override
	public boolean isGameOver() {
		return gameOver;
	}
	
	public List<String> getPlayerNames() {
		List<String> names = new ArrayList<>();
		clients.stream().forEach(c->names.add(c.getName()));
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		names.sort(collator);
		return names;
	}
}
