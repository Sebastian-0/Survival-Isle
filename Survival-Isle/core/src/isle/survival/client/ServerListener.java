package isle.survival.client;

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
			} catch (Throwable e) {
				System.out.println("Server closed connection unexpectedly");
				e.printStackTrace();
				break;
			}
		}
	}
}
