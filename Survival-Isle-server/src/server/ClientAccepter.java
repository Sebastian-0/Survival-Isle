package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientAccepter implements Runnable {
	
	private Game game;
	
	public ClientAccepter(Game game) {
		this.game = game;
	}
	
	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(1337)) {
			while (true) {
				Socket socket = serverSocket.accept();
				ServerProtocolCoder client = new ServerProtocolCoder(new Connection(socket));
				new Thread(new ClientListener(game, client)).start();
				game.addClient(client);
				System.out.println("Client connected.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}