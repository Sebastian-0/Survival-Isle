package isle.survival.client;

import server.ClientProtocol;
import server.Connection;
import server.ServerProtocol;

public class ClientProtocolCoder {
	
	private Connection connection;
	
	public ClientProtocolCoder(String name, Connection connection) {
		this.connection = connection;
		connection.sendString(name);
	}
	
	public Connection getConnection() {
		return connection;
	}

	public synchronized void sendMoveUp() {
		connection.sendCode(ClientProtocol.TO_PLAYER);
		connection.sendCode(ClientProtocol.MOVE_UP);
	}

	public synchronized void sendMoveLeft() {
		connection.sendCode(ClientProtocol.TO_PLAYER);
		connection.sendCode(ClientProtocol.MOVE_LEFT);
	}

	public synchronized void sendMoveDown() {
		connection.sendCode(ClientProtocol.TO_PLAYER);
		connection.sendCode(ClientProtocol.MOVE_DOWN);
	}

	public synchronized void sendMoveRight() {
		connection.sendCode(ClientProtocol.TO_PLAYER);
		connection.sendCode(ClientProtocol.MOVE_RIGHT);
	}

	public ServerProtocol receiveCode() {
		int code = connection.receiveInt();
		return ServerProtocol.values()[code];
	}

	public synchronized void flush() {
		connection.flush();
	}
}
