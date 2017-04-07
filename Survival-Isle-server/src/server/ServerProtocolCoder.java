package server;

import world.Player;
import world.ServerWorld;
import world.WorldObjects;

public class ServerProtocolCoder {
	
	private Connection connection;
	
	public ServerProtocolCoder(Connection connection) {
		this.connection = connection;
	}

	public void sendWorld(ServerWorld world) {
		connection.sendCode(ServerProtocol.SEND_WORLD);
		world.send(connection);
	}

	public void sendCreateWorldObjects(WorldObjects worldObjects) {
		connection.sendCode(ServerProtocol.CREATE_OBJECTS);
		worldObjects.sendCreateAll(connection);
	}

	public void sendSetPlayer(Player newPlayer) {
		connection.sendCode(ServerProtocol.SET_PLAYER);
		connection.sendInt(newPlayer.getId());
	}

	public void sendCreateObject(Player object) {
		connection.sendCode(ServerProtocol.CREATE_OBJECTS);
		connection.sendInt(1);
		object.sendCreate(connection);
	}

	public ClientProtocol receiveCode() {
		int code = connection.receiveInt();
		return ClientProtocol.values()[code];
	}
}
