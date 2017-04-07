package server;

import java.io.IOException;

public class Server {
	
	private Game game = new Game();

	public void start() throws IOException {
		new Thread(new ClientAccepter(game)).start();
		new Thread(new ServerUpdater(game)).start();
	}
	
	public static void main(String[] args) throws IOException {
		new Server().start();
	}
}
