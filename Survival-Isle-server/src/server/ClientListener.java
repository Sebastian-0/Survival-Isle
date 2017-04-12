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
			try {
				ClientProtocol code = client.receiveCode();
				System.out.println("Server received: " + code);
				game.parseClientMessage(code, client);
			}
			catch (ConnectionClosedException e) {
				game.removeClient(client);
				return;
			}
		}
	}
}
