package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientAccepter extends Thread {
	
	private ServerSocket serverSocket;
	private Game game;
	private int port;
	
	public ClientAccepter(Game game, int port) {
		super ("Client accepter");
		this.game = game;
		this.port = port;
	}
	
	public boolean isPortAvailable() {
		if (port != 0) {
			try {
				Socket socket = new Socket("localhost", port);
				socket.close();
				return false;
			}
			catch (UnknownHostException e) { }
			catch (IOException e) { }
		}

		return true;
	}
	
	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			this.serverSocket = serverSocket;
			while (!serverSocket.isClosed()) {
				try {
					Socket socket = serverSocket.accept();
					ServerProtocolCoder client = new ServerProtocolCoder(socket);
					game.addClient(client);
				} catch (SocketException | ConnectionClosedException e) {
					System.out.println("A client failed to join: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			System.out.println("Failed to start the server, maybe the port was already in use?");
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