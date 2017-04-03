package server;

public class ClientListener implements Runnable {
	
	private MessageHandler messageHandler;

	public ClientListener(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}
	
	@Override
	public void run() {
	}
}
