package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ClientAccepter extends Thread {
	
	private ServerSocket serverSocket;
	private Game game;
	private int port;
	
	public ClientAccepter(Game game, int port) {
		super ("Client accepter");
		this.game = game;
		this.port = port;
	}
	
	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
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