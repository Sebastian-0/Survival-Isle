package isle.survival.client;

public class ServerListener implements Runnable {
	
	private ClientInterface client;

	public ServerListener(ClientInterface client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		while (true) {
			client.parseServerMessage();
		}
	}
}
