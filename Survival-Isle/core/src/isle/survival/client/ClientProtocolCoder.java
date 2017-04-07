package isle.survival.client;

import server.ClientProtocol;
import server.Connection;
import server.ServerProtocol;

public class ClientProtocolCoder {
	
	private Connection connection;
	
	public ClientProtocolCoder(Connection connection) {
		this.connection = connection;
	}
	
	public Connection getConnection() {
		return connection;
	}

	public void sendMoveUp() {
		connection.sendCode(ClientProtocol.TO_PLAYER);
		connection.sendCode(ClientProtocol.MOVE_UP);
	}

	public void sendMoveLeft() {
		connection.sendCode(ClientProtocol.TO_PLAYER);
		connection.sendCode(ClientProtocol.MOVE_LEFT);
	}

	public void sendMoveDown() {
		connection.sendCode(ClientProtocol.TO_PLAYER);
		connection.sendCode(ClientProtocol.MOVE_DOWN);
	}

	public void sendMoveRight() {
		connection.sendCode(ClientProtocol.TO_PLAYER);
		connection.sendCode(ClientProtocol.MOVE_RIGHT);
	}

	public ServerProtocol receiveCode() {
		int code = connection.receiveInt();
		return ServerProtocol.values()[code];
	}
}
