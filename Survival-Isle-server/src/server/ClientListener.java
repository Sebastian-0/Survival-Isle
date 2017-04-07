package server;

public class ClientListener implements Runnable {
	
	private ServerProtocolCoder coder;

	public ClientListener(ServerProtocolCoder coder) {
		this.coder = coder;
	}
	
	@Override
	public void run() {
		while (true) {
			System.out.println(coder.receiveCode());
		}
	}
}
