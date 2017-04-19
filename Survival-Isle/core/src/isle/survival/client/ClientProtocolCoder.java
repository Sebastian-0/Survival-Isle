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

	public void sendMoveUp() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.MoveUp);
	}

	public void sendMoveLeft() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.MoveLeft);
	}

	public void sendMoveDown() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.MoveDown);
	}

	public void sendMoveRight() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.MoveRight);
	}
	
	public void sendSelectTool(int tool) {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.SelectTool);
		connection.sendInt(tool);
	}

	public void sendActivateTool() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.ActivateTool);
	}

	public void sendDeactivateTool() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.DeactivateTool);
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
