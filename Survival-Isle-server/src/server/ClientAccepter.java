package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ClientAccepter extends Thread {
	
	private Game game;
	private ServerSocket serverSocket;
	
	public ClientAccepter(Game game) {
		super ("Client accepter");
		this.game = game;
	}
	
	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(1337)) {
			this.serverSocket = serverSocket;
			while (true) {
				try {
					Socket socket = serverSocket.accept();
					ServerProtocolCoder client = new ServerProtocolCoder(socket);
					game.addClient(client);
				} catch (SocketException e) {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
		}
	}
}