package server;

import world.Player;
import world.ServerWorld;
import world.WorldObjects;

public class ServerProtocolCoder {
	
	private Connection connection;
	
	public ServerProtocolCoder(Connection connection) {
		this.connection = connection;
	}

	public synchronized void sendWorld(ServerWorld world) {
		connection.sendCode(ServerProtocol.SEND_WORLD);
		world.send(connection);
	}

	public synchronized void sendCreateWorldObjects(WorldObjects worldObjects) {
		connection.sendCode(ServerProtocol.CREATE_OBJECTS);
		worldObjects.sendCreateAll(connection);
	}

	public synchronized void sendSetPlayer(Player newPlayer) {
		connection.sendCode(ServerProtocol.SET_PLAYER);
		connection.sendInt(newPlayer.getId());
	}

	public synchronized void sendCreateObject(Player object) {
		connection.sendCode(ServerProtocol.CREATE_OBJECTS);
		connection.sendInt(1);
		object.sendCreate(connection);
	}

	public synchronized void sendUpdateObject(Player object) {
		connection.sendCode(ServerProtocol.SEND_OBJECTS);
		connection.sendInt(1);
		object.sendUpdate(connection);
	}

	public ClientProtocol receiveCode() {
		int code = connection.receiveInt();
		return ClientProtocol.values()[code];
	}

	public synchronized void flush() {
		connection.flush();
	}
}
