package isle.survival.client;

import server.ClientProtocol;
import server.Connection;
import server.ServerProtocol;

public class ClientProtocolCoder {
	
	protected Connection connection;
	
	public ClientProtocolCoder(String name, Connection connection) {
		this.connection = connection;
	}
	
	public Connection getConnection() {
		return connection;
	}

	public ServerProtocol receiveCode() {
		int code = connection.receiveInt();
		return ServerProtocol.values()[code];
	}

	public void sendClose() {
		connection.sendCode(ClientProtocol.SendClose);
		flush();
	}

	public void acknowledgeClose() {
		connection.sendCode(ClientProtocol.AckClose);
		flush();
	}

	public void flush() {
		connection.flush();
	}
}
