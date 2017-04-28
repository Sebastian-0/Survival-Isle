package server;

public class ClientListener extends Thread {
	
	private Game game;
	private ServerProtocolCoder client;

	public ClientListener(Game game, ServerProtocolCoder client) {
		super ("Client listener");
		this.game = game;
		this.client = client;
	}
	
	@Override
	public void run() {
		while (!interrupted()) {
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
		game.removeClient(client);
	}
}
