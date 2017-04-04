package isle.survival.client;

import server.MessageHandler;

public class ServerListener implements Runnable {
	
	private MessageHandler messageHandler;

	public ServerListener(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}
	
	@Override
	public void run() {
	}
}
