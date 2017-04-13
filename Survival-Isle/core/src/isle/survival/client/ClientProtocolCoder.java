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

	public ServerProtocol receiveCode() {
		int code = connection.receiveInt();
		return ServerProtocol.values()[code];
	}

	public synchronized void sendMoveUp() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.MoveUp);
	}

	public synchronized void sendMoveLeft() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.MoveLeft);
	}

	public synchronized void sendMoveDown() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.MoveDown);
	}

	public synchronized void sendMoveRight() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.MoveRight);
	}
	
	public synchronized void sendSelectTool(int tool) {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.SelectTool);
		connection.sendInt(tool);
	}

	public void sendUseTool() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.UseTool);
	}

	public synchronized void flush() {
		connection.flush();
	}
}
