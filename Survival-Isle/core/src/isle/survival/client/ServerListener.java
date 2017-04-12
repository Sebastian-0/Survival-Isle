package isle.survival.client;

import server.ConnectionClosedException;

public class ServerListener implements Runnable {
	
	private ClientInterface client;

	public ServerListener(ClientInterface client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				client.parseServerMessage();
			} catch (ConnectionClosedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
