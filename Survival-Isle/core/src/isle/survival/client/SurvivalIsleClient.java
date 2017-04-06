package isle.survival.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.world.ClientWorld;
import isle.survival.world.TextureBase;
import server.MessageHandler;

public class SurvivalIsleClient extends ApplicationAdapter {
	private TextureBase textureBase;
	private SpriteBatch spriteBatch;
	private ClientWorld world;
	private Socket socket;
	
	@Override
	public void create () {
		textureBase = new TextureBase();
		spriteBatch = new SpriteBatch();
		world = new ClientWorld(20, 15, textureBase);
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
		spriteBatch.begin();
		world.drawTerrain(spriteBatch);

		spriteBatch.end();
	}
	
	@Override
	public void dispose() {
		textureBase.dispose();
		spriteBatch.dispose();
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("Client could not close socket when terminating.");
			}
		}
	}
}
