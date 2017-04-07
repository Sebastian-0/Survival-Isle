package isle.survival.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.world.ClientWorld;
import isle.survival.world.NetworkObject;
import isle.survival.world.TextureBase;
import isle.survival.world.WorldObjects;
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
	private WorldObjects worldObjects;
	private float xView;
	private float yView;
	
	
	@Override
	public void create () {
		textureBase = new TextureBase();
		spriteBatch = new SpriteBatch();
		world = new ClientWorld(textureBase, spriteBatch);
		worldObjects = new WorldObjects(textureBase, spriteBatch);
		connectToServer();
		
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
		synchronized (this) {
			update();
			draw();			
		}
	}
	
	private void update() {
		//Sync objects
		
		NetworkObject player = worldObjects.getPlayer();
		if (player != null) {
			xView = player.getX() * World.TILE_WIDTH - Gdx.graphics.getWidth()/2;
			yView = player.getY() * World.TILE_HEIGHT - Gdx.graphics.getHeight()/2;
		}
		coder.flush();
	}
	
	private void draw() {
		spriteBatch.begin();
		world.drawTerrain(xView, yView);
		worldObjects.draw(xView, yView);
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

	public void parseServerMessage() {
		ServerProtocol code = coder.receiveCode();
		System.out.println("Client received: " + code);
		
		synchronized (this) {
			switch (code) {
			case SEND_WORLD:
				world.receive(coder.getConnection());
				break;
			case CREATE_OBJECTS:
				worldObjects.createObjects(coder.getConnection());
				break;
			case SET_PLAYER:
				worldObjects.setPlayer(coder.getConnection().receiveInt());
				break;
			case SEND_OBJECTS:
				worldObjects.updateObjects(coder.getConnection());
			case DESTROY_OBJECTS:
				worldObjects.destroyObjects(coder.getConnection());
			default:
				break;
			}
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
