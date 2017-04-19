package isle.survival.client;

import server.ConnectionClosedException;

public class ServerListener extends Thread {
	
	private ClientInterface client;

	public ServerListener(ClientInterface client) {
		super ("Server listener");
		this.client = client;
	}
	
	@Override
	public void run() {
		while (!interrupted()) {
			try {
				client.parseServerMessage();
			} catch (ConnectionClosedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
