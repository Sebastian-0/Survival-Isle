package isle.survival.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.ApplicationAdapter;

import isle.survival.world.ClientWorld;
import server.MessageHandler;

public class SurvivalIsleClient extends ApplicationAdapter {
	ClientWorld world;
	Socket socket;
	
	@Override
	public void create () {
		world = new ClientWorld(20, 15);
		world.GenerateTerrain(0); //TODO: move to server
		connectToServer();
	}
	
	private void connectToServer() {
		try {
			socket = new Socket("localhost", 1337);
			
			new Thread(new ServerListener(new MessageHandler(socket)));

			System.out.println("Connected to host.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Error: Host not found.");
		} catch (IOException e) {
			System.out.println("Error: Could not connect to host. Connection refused.");
		}
	}
	
	@Override
	public void render () {
		update();
		draw();
	}
	
	private void update() {
		//Sync objects
	}
	
	private void draw() {
		world.drawTerrain();
	}
	
	@Override
	public void dispose() {
		world.dispose();
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("Client could not close socket when terminating.");
			}
		}
	}
}
