package server;

import world.Player;
import world.ServerWorld;
import world.WorldObjects;

public class ServerProtocolCoder {
	
	private Connection connection;
	private String name;
	
	public ServerProtocolCoder(Connection connection) {
		this.connection = connection;
		name = connection.receiveStringParameter();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ServerProtocolCoder) {
			return name.equals(((ServerProtocolCoder) obj).name);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return name;
	}

	public synchronized void sendWorld(ServerWorld world) {
		connection.sendCode(ServerProtocol.SendWorld);
		world.send(connection);
	}

	public synchronized void sendCreateWorldObjects(WorldObjects worldObjects) {
		connection.sendCode(ServerProtocol.CreateObjects);
		worldObjects.sendCreateAll(connection);
	}

	public synchronized void sendSetPlayer(Player newPlayer) {
		connection.sendCode(ServerProtocol.SetPlayer);
		connection.sendInt(newPlayer.getId());
	}

	public synchronized void sendUpdateWallTiles(ServerWorld world) {
		connection.sendCode(ServerProtocol.SendWorldWallTiles);
		world.sendWallTileUpdate(connection);
	}

	public synchronized void sendCreateObject(Player object) {
		connection.sendCode(ServerProtocol.CreateObjects);
		connection.sendInt(1);
		object.sendCreate(connection);
	}

	public synchronized void sendUpdateObject(Player object) {
		connection.sendCode(ServerProtocol.SendObjects);
		connection.sendInt(1);
		object.sendUpdate(connection);
	}

	public void sendDestroyObject(Player object) {
		connection.sendCode(ServerProtocol.DestroyObject);
		connection.sendInt(1);
		object.sendDestroy(connection);
	}
	
	public void sendSetInventory(int id, int amount) {
		connection.sendCode(ServerProtocol.SetInventory);
		connection.sendInt(id);
		connection.sendInt(amount);
	}

	public void sendFailedToConnect() {
		connection.sendCode(ServerProtocol.FailedToConnect);
		connection.flush();
		connection.close();
	}

	public ClientProtocol receiveCode() {
		int code = connection.receiveInt();
		return ClientProtocol.values()[code];
	}

	public synchronized void flush() {
		connection.flush();
	}
}
