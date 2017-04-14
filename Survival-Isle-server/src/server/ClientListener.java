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
		while (!Thread.interrupted()) {
			try {
				ClientProtocol code = client.receiveCode();
//				System.out.println("Server received: " + code);
				game.parseClientMessage(code, client);
			}
			catch (ConnectionClosedException e) {
				game.removeClient(client);
				System.out.println("Client closed connection unexpectedly: " + client);
				return;
			}
		}
		client.disconnect();
		System.out.println("Client disconnected: " + client);
	}
}
