package isle.survival.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.ui.BuildMenu;
import isle.survival.world.ClientWorld;
import isle.survival.world.NetworkObject;
import isle.survival.world.TextureBase;
import isle.survival.world.WorldObjects;
import server.Connection;
import server.ServerProtocol;
import world.Inventory;
import world.World;

public class SurvivalIsleClient extends ApplicationAdapter implements ClientInterface {
	private String name;
	private TextureBase textureBase;
	private SpriteBatch spriteBatch;
	private InputProcessor inputProcessor;
	private Socket socket;
	private ClientProtocolCoder coder;
	
	private ClientWorld world;
	private WorldObjects worldObjects;
	private Inventory inventory;
	private float xView;
	private float yView;
	
	private BuildMenu buildMenu;
	
	public SurvivalIsleClient(String name) {
		this.name = name;
	}
	
	@Override
	public void create () {
		textureBase = new TextureBase();
		spriteBatch = new SpriteBatch();
		world = new ClientWorld(textureBase, spriteBatch);
		worldObjects = new WorldObjects(textureBase, spriteBatch);
		inventory = new Inventory();
		connectToServer();
		
		inputProcessor = new InputProcessor();
		Gdx.input.setInputProcessor(inputProcessor);
		
		buildMenu = new BuildMenu(textureBase);
	}
	
	private void connectToServer() {
		try {
			socket = new Socket("localhost", 1337);
			coder = new ClientProtocolCoder(name, new Connection(socket));
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
		float deltaTime = Gdx.graphics.getDeltaTime();
		worldObjects.update(deltaTime);
		
		NetworkObject player = worldObjects.getPlayer();
		if (player != null) {
			xView = player.getX() * World.TILE_WIDTH - Gdx.graphics.getWidth()/2;
			yView = player.getY() * World.TILE_HEIGHT - Gdx.graphics.getHeight()/2;
		}
		
		inputProcessor.update(deltaTime);
		coder.flush();
	}
	
	private void draw() {
		spriteBatch.begin();
		world.drawTerrain(xView, yView);
		worldObjects.draw(xView, yView);
		
		int x = Gdx.graphics.getWidth()/2 - buildMenu.getWidth()/2;
		spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(x, 0, 0));
		buildMenu.draw(spriteBatch);
		spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(-x, 0, 0));
		
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

	@Override
	public void parseServerMessage() {
		ServerProtocol code = coder.receiveCode();
		System.out.println("Client received: " + code);
		
		synchronized (this) {
			switch (code) {
			case SendWorld:
				world.receive(coder.getConnection());
				break;
			case CreateObjects:
				worldObjects.createObjects(coder.getConnection());
				break;
			case SetPlayer:
				worldObjects.setPlayer(coder.getConnection().receiveInt());
				break;
			case SendObjects:
				worldObjects.updateObjects(coder.getConnection());
				break;
			case DestroyObject:
				worldObjects.destroyObjects(coder.getConnection());
				break;
			case SetInventory:
				inventory.setInventory(coder.getConnection());
				break;
			case FailedToConnect:
				System.out.println("User name already in use.");
				Gdx.app.exit();
				Thread.currentThread().interrupt();
				break;
			default:
				break;
			}
		}
	}
	
	private class InputProcessor extends InputAdapter {
		
		private static final float MOVEMENT_TIME = NetworkObject.MOVEMENT_TIME;
		
		private float movingUpCounter;
		private float movingLeftCounter;
		private float movingDownCounter;
		private float movingRightCounter;
		
		@Override
		public boolean keyDown(int keycode) {
			switch (keycode) {
			case Input.Keys.W:
			case Input.Keys.UP:
				coder.sendMoveUp();
				movingUpCounter = MOVEMENT_TIME;
				break;
			case Input.Keys.A:
			case Input.Keys.LEFT:
				coder.sendMoveLeft();
				movingLeftCounter = MOVEMENT_TIME;
				break;
			case Input.Keys.S:
			case Input.Keys.DOWN:
				coder.sendMoveDown();
				movingDownCounter = MOVEMENT_TIME;
				break;
			case Input.Keys.D:
			case Input.Keys.RIGHT:
				coder.sendMoveRight();
				movingRightCounter = MOVEMENT_TIME;
				break;
			case Input.Keys.Q:
				buildMenu.decrementSelection();
				break;
			case Input.Keys.E:
				buildMenu.incrementSelection();
				break;
			case Input.Keys.NUM_1:
			case Input.Keys.NUM_2:
			case Input.Keys.NUM_3:
			case Input.Keys.NUM_4:
			case Input.Keys.NUM_5:
			case Input.Keys.NUM_6:
			case Input.Keys.NUM_7:
			case Input.Keys.NUM_8:
			case Input.Keys.NUM_9:
				buildMenu.setSelectedIndex(keycode - Input.Keys.NUM_1);
				break;
			default:
				return false;
			}
			return true;
		}
		
		@Override
		public boolean scrolled(int amount) {
			if (amount > 0)
				buildMenu.incrementSelection();
			else
				buildMenu.decrementSelection();
			return true;
		}
		
		public void update(float deltaTime) {
			if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))
				movingUpCounter -= deltaTime;
			if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
				movingLeftCounter-= deltaTime;
			if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))
				movingDownCounter -= deltaTime;
			if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
				movingRightCounter -= deltaTime;
			
			if (movingUpCounter < 0) {
				movingUpCounter += MOVEMENT_TIME;
				coder.sendMoveUp();
			}

			if (movingLeftCounter < 0) {
				movingLeftCounter += MOVEMENT_TIME;
				coder.sendMoveLeft();
			}

			if (movingDownCounter < 0) {
				movingDownCounter += MOVEMENT_TIME;
				coder.sendMoveDown();
			}

			if (movingRightCounter < 0) {
				movingRightCounter += MOVEMENT_TIME;
				coder.sendMoveRight();
			}
		}
	}
}
