package server;

import java.io.Serializable;
import java.net.Socket;

import world.EffectType;
import world.GameObject;
import world.Inventory;
import world.ServerWorld;
import world.WorldObjects;

public class ServerProtocolCoder implements Serializable {
	
	private transient Connection connection;
	private String name;
	
	public ServerProtocolCoder(Socket socket) {
		this.connection = new Connection(socket);
		name = connection.receiveString();
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

	public void sendSetPlayer(GameObject newPlayer) {
		connection.sendCode(ServerProtocol.SetPlayer);
		connection.sendInt(newPlayer.getId());
	}

	public void sendUpdateWallTiles(ServerWorld world) {
		connection.sendCode(ServerProtocol.SendWorldWallTiles);
		world.sendWallTileUpdate(connection);
	}

	public void sendCreateObject(GameObject object) {
		connection.sendCode(ServerProtocol.CreateObjects);
		connection.sendInt(1);
		object.sendCreate(connection);
	}

	public void sendUpdateObject(GameObject object) {
		connection.sendCode(ServerProtocol.SendObjects);
		connection.sendInt(1);
		object.sendUpdate(connection);
	}

	public void sendDestroyObject(GameObject object) {
		connection.sendCode(ServerProtocol.DestroyObject);
		connection.sendInt(1);
		object.sendDestroy(connection);
	}
	
	public void sendCreateEffect(EffectType type, int... data) {
		connection.sendCode(ServerProtocol.CreateEffect);
		connection.sendInt(type.ordinal());
		for (int i : data) {
			connection.sendInt(i);
		}
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

	public void sendClose() {
		connection.sendCode(ServerProtocol.SendClose);
		flush();
	}

	public void acknowledgeClose() {
		connection.sendCode(ServerProtocol.AckClose);
		flush();
	}

	public void flush() {
		connection.flush();
	}

	public void disconnect() {
		connection.close();
	}
}
