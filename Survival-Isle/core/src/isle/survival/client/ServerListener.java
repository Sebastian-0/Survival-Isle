package isle.survival.client;

public class ServerListener implements Runnable {
	
	private SurvivalIsleClient client;

	public ServerListener(SurvivalIsleClient client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		while (true) {
			client.parseServerMessage();
		}
	}
}
