package isle.survival.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import isle.survival.world.ClientWorld;
import isle.survival.world.NetworkObject;
import isle.survival.world.TextureBase;
import server.Connection;
import server.ServerProtocol;
import world.World;

public class SurvivalIsleClient extends ApplicationAdapter {
	private TextureBase textureBase;
	private SpriteBatch spriteBatch;
	private InputProcessor inputProcessor;
	private Socket socket;
	private ClientProtocolCoder coder;
	
	private ClientWorld world;
	private Array<NetworkObject> networkObjects;
	private NetworkObject playerObject;
	private float xView;
	private float yView;
	
	
	@Override
	public void create () {
		textureBase = new TextureBase();
		spriteBatch = new SpriteBatch();
		networkObjects = new Array<>();
		world = new ClientWorld(textureBase);
		connectToServer();

		playerObject = new NetworkObject(10, 7, 0, 0); //TODO don't create here
		networkObjects.add(playerObject);
		
		inputProcessor = new InputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
	}
	
	private void connectToServer() {
		try {
			socket = new Socket("localhost", 1337);
			coder = new ClientProtocolCoder(new Connection(socket));
			new Thread(new ServerListener(this)).start();

			System.out.println("Connected to host.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Error: Host not found.");
		} catch (IOException e) {
			System.out.println("Error: Could not connect to host. Connection refused.");
		}
	}
	
	@Override
	public void render() {
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
			if (isPointOnScreen(networkObject.getX() * World.TILE_WIDTH, networkObject.getY() * World.TILE_HEIGHT))
				networkObject.draw(spriteBatch, textureBase, xView, yView); //TODO: add offset.
		}
		
		spriteBatch.end();
	}
	
	private boolean isPointOnScreen(float x, float y) {
		int dx = 32;
		int dy = 32;
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		if (x >= xView - dx && x < xView + w + dx && y >= yView - dy && y < yView + h + dy)
			return true;
		return false;
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

	public void parseServerMessage() {
		ServerProtocol code = coder.receiveCode();
//		System.out.println("Client received: " + code);
		switch (code) {
		case SEND_WORLD:
			world.receive(coder.getConnection());
			break;
		default:
			break;
		}
	}
	
	private class InputProcessor extends InputAdapter {
		
		@Override
		public boolean keyDown(int keycode) {
			switch (keycode) {
			case Input.Keys.W:
			case Input.Keys.UP:
				coder.sendMoveUp();
				break;
			case Input.Keys.A:
			case Input.Keys.LEFT:
				coder.sendMoveLeft();
				break;
			case Input.Keys.S:
			case Input.Keys.DOWN:
				coder.sendMoveDown();				
				break;
			case Input.Keys.D:
			case Input.Keys.RIGHT:
				coder.sendMoveRight();
				break;
			default:
				return false;
			}
			return true;
		}
	}
}
