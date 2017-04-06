package isle.survival.client;

import server.Connection;
import server.Protocol;

public class ClientProtocolCoder {
	
	private Connection connection;
	
	public ClientProtocolCoder(Connection connection) {
		this.connection = connection;
	}
	
	public Connection getConnection() {
		return connection;
	}

	public Protocol receiveCode() {
		int code = connection.receiveInt();
		return Protocol.values()[code];
	}
}
