package isle.survival.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.world.ClientWorld;
import isle.survival.world.NetworkObject;
import isle.survival.world.TextureBase;
import server.Connection;
import world.World;

public class SurvivalIsleClient extends ApplicationAdapter {
	private TextureBase textureBase;
	private SpriteBatch spriteBatch;
	private Socket socket;
	
	private ClientWorld world;
	private ArrayList<NetworkObject> networkObjects;
	private NetworkObject playerObject;
	private float xView;
	private float yView;
	
	
	@Override
	public void create () {
		textureBase = new TextureBase();
		spriteBatch = new SpriteBatch();
		networkObjects = new ArrayList<>();
		world = new ClientWorld(20, 15, textureBase);
		world.GenerateTerrain(0); //TODO: move to server
		connectToServer();
		
		playerObject = new NetworkObject(0, 0, 0, 0); //TODO
		networkObjects.add(playerObject);
	}
	
	private void connectToServer() {
		try {
			socket = new Socket("localhost", 1337);
			
			new Thread(new ServerListener(new Connection(socket)));

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
		
		if (playerObject != null) {
			xView = playerObject.getX() * World.TILE_WIDTH - Gdx.graphics.getWidth()/2;
			yView = playerObject.getY() * World.TILE_HEIGHT - Gdx.graphics.getHeight()/2;
		}
	}
	
	private void draw() {
		spriteBatch.begin();
		world.drawTerrain(spriteBatch, xView, yView);
		for (NetworkObject networkObject : networkObjects) {
			networkObject.draw(spriteBatch, textureBase, xView, yView); //TODO: add offset.
		}
		
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
