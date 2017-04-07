package server;

import world.World;

public class ServerProtocolCoder {
	
	private Connection connection;
	
	public ServerProtocolCoder(Connection connection) {
		this.connection = connection;
	}

	public void sendWorld(World world) {
		connection.sendCode(ServerProtocol.SEND_WORLD);
		world.send(connection);
	}
}
