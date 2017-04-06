package server;

import java.io.IOException;

public class Server {
	
	private Game game;

	public void start() throws IOException {
		new Thread(new ClientAccepter()).start();
		new Thread(new ServerUpdater(game)).start();
	}
	
	public static void main(String[] args) throws IOException {
		new Server().start();
	}
}
