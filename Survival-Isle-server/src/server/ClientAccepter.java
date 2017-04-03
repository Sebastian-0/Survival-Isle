package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientAccepter implements Runnable {
	
	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(1337)) {
			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(new ClientListener(new MessageHandler(socket))).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}