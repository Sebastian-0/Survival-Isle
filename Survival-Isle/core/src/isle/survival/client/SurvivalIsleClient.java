package isle.survival.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.input.InputProcessor;
import isle.survival.ui.Ui;
import isle.survival.world.ClientWorld;
import isle.survival.world.NetworkObject;
import isle.survival.world.SoundBase;
import isle.survival.world.TextureBase;
import isle.survival.world.WorldObjects;
import server.Connection;
import server.ServerProtocol;
import world.EffectType;
import world.Inventory;
import world.World;

public class SurvivalIsleClient extends ApplicationAdapter implements ClientInterface {
	private String name;
	private TextureBase textureBase;
	private SpriteBatch spriteBatch;
	private SoundBase soundBase;
	private InputProcessor inputProcessor;
	private Socket socket;
	private ClientProtocolCoder coder;
	
	private ClientWorld world;
	private WorldObjects worldObjects;
	private Inventory inventory;
	private float xView;
	private float yView;
	
	private Ui ui;
	
	public SurvivalIsleClient(String name) {
		this.name = name;
	}
	
	@Override
	public void create () {
		textureBase = new TextureBase();
		spriteBatch = new SpriteBatch();
		soundBase = new SoundBase();
		world = new ClientWorld(textureBase, spriteBatch);
		worldObjects = new WorldObjects(textureBase, spriteBatch);
		inventory = new Inventory();
		connectToServer();

		ui = new Ui(textureBase, inventory);
		
		inputProcessor = new InputProcessor(ui, coder);
		Gdx.input.setInputProcessor(inputProcessor);
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
	public void resize(int width, int height) {
		spriteBatch.setProjectionMatrix(spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height));
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
		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		world.drawTerrain(xView, yView);
		worldObjects.draw(xView, yView);
		
		ui.draw(spriteBatch);
		
		spriteBatch.end();
	}
	
	
	@Override
	public void dispose() {
		textureBase.dispose();
		spriteBatch.dispose();
		soundBase.dispose();
		
		synchronized (this) {
			coder.sendClose();
		}
	}

	@Override
	public void parseServerMessage() {
		ServerProtocol code = coder.receiveCode();
//		System.out.println("Client received: " + code);
		
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
			case CreateEffect:
				EffectType type = EffectType.values()[coder.getConnection().receiveInt()];
				if (type == EffectType.TileDestroyed) {
					int tileX = coder.getConnection().receiveInt();
					int tileY = coder.getConnection().receiveInt();
					int playerX = coder.getConnection().receiveInt();
					int playerY = coder.getConnection().receiveInt();
					// TODO Spawn effect here!
				}
				break;
			case SetInventory:
				inventory.receiveInventory(coder.getConnection());
				break;
			case SendWorldWallTiles:
				world.receiveWallTiles(coder.getConnection());
				break;
			case FailedToConnect:
				System.out.println("User name already in use.");
				Gdx.app.exit();
				Thread.currentThread().interrupt();
				break;
			case PlaySound:
				soundBase.playSound(coder.getConnection());
				break;
			case AckClose:
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Thread.currentThread().interrupt();
				break;
			default:
				break;
			}
		}
	}
}
