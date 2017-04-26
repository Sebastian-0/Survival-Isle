package isle.survival.client;

import server.ClientProtocol;
import server.Connection;
import world.Tool;

public class GameProtocolCoder extends ClientProtocolCoder {
	
	public GameProtocolCoder(String name, Connection connection) {
		super(name, connection);
		connection.sendString(name);
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
	
	public void sendSelectTool(Tool tool) {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.SelectTool);
		connection.sendInt(tool.ordinal());
	}

	public void sendActivateTool() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.ActivateTool);
	}

	public void sendDeactivateTool() {
		connection.sendCode(ClientProtocol.ToPlayer);
		connection.sendCode(ClientProtocol.DeactivateTool);
	}

	public void sendChatMessage(String message) {
		connection.sendCode(ClientProtocol.SendChatMessage);
		connection.sendString(message);
	}

}
