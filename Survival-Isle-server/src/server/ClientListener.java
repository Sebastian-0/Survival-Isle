package server;

public class ClientListener implements Runnable {
	
	private Game game;
	private ServerProtocolCoder client;

	public ClientListener(Game game, ServerProtocolCoder client) {
		this.game = game;
		this.client = client;
	}
	
	@Override
	public void run() {
		while (true) {
			ClientProtocol code = client.receiveCode();
//			System.out.println("Server received:" + code);
			game.parseClientMessage(code, client);
		}
	}
}
