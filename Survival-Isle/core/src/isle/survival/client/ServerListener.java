package isle.survival.client;

import server.Connection;

public class ServerListener implements Runnable {
	
	private Connection messageHandler;

	public ServerListener(Connection messageHandler) {
		this.messageHandler = messageHandler;
	}
	
	@Override
	public void run() {
	}
}
