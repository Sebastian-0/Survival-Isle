package server;

import world.ServerWorld;

public class ServerProtocolCoder {
	
	private Connection connection;
	
	public ServerProtocolCoder(Connection connection) {
		this.connection = connection;
	}

	public void sendWorld(ServerWorld world) {
		connection.sendCode(ServerProtocol.SEND_WORLD);
		world.send(connection);
	}

	public ClientProtocol receiveCode() {
		int code = connection.receiveInt();
		return ClientProtocol.values()[code];
	}
}
