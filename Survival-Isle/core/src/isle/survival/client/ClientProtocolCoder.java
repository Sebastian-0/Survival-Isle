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

	public ServerProtocol receiveCode() {
		int code = connection.receiveInt();
		return ServerProtocol.values()[code];
	}

	public void sendMoveUp() {
		connection.sendCode(ClientProtocol.MOVE_PLAYER_UP);
	}

	public void sendMoveLeft() {
		connection.sendCode(ClientProtocol.MOVE_PLAYER_LEFT);
	}

	public void sendMoveDown() {
		connection.sendCode(ClientProtocol.MOVE_PLAYER_DOWN);
	}

	public void sendMoveRight() {
		connection.sendCode(ClientProtocol.MOVE_PLAYER_RIGHT);
	}
}
