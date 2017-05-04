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
			catch (Throwable e) {
				System.out.println("Client closed connection unexpectedly: " + client);
				e.printStackTrace();
				break;
			}
		}
		client.disconnect();
		game.removeClient(client);
	}
}
