package server;

import java.io.Serializable;

import world.Inventory;
import world.Player;
import world.ServerWorld;
import world.WorldObjects;

public class ServerProtocolCoder implements Serializable {
	
	private transient Connection connection;
	private String name;
	
	public ServerProtocolCoder(Connection connection) {
		this.connection = connection;
		name = connection.receiveStringParameter();
	}
	
	public Connection getConnection() {
		return connection;
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

	public ClientProtocol receiveCode() {
		int code = connection.receiveInt();
		return ClientProtocol.values()[code];
	}

	public void sendWorld(ServerWorld world) {
		connection.sendCode(ServerProtocol.SendWorld);
		world.send(connection);
	}

	public void sendCreateWorldObjects(WorldObjects worldObjects) {
		connection.sendCode(ServerProtocol.CreateObjects);
		worldObjects.sendCreateAll(connection);
	}

	public void sendSetPlayer(Player newPlayer) {
		connection.sendCode(ServerProtocol.SetPlayer);
		connection.sendInt(newPlayer.getId());
	}

	public void sendUpdateWallTiles(ServerWorld world) {
		connection.sendCode(ServerProtocol.SendWorldWallTiles);
		world.sendWallTileUpdate(connection);
	}

	public void sendCreateObject(Player object) {
		connection.sendCode(ServerProtocol.CreateObjects);
		connection.sendInt(1);
		object.sendCreate(connection);
	}

	public void sendUpdateObject(Player object) {
		connection.sendCode(ServerProtocol.SendObjects);
		connection.sendInt(1);
		object.sendUpdate(connection);
	}

	public void sendDestroyObject(Player object) {
		connection.sendCode(ServerProtocol.DestroyObject);
		connection.sendInt(1);
		object.sendDestroy(connection);
	}
	
	public void sendInventory(Inventory inventory) {
		connection.sendCode(ServerProtocol.SetInventory);
		inventory.sendInventory(connection);
	}

	public void sendFailedToConnect() {
		connection.sendCode(ServerProtocol.FailedToConnect);
		connection.flush();
	}
	
	public void sendPlaySound(int id) {
		connection.sendCode(ServerProtocol.PlaySound);
		connection.sendInt(id);
	}

	public void flush() {
		connection.flush();
	}

	public void disconnect() {
		connection.close();
	}
}
