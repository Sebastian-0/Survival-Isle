package isle.survival.client;

import isle.survival.input.InputProcessor;
import isle.survival.ui.Ui;
import isle.survival.world.ClientWorld;
import isle.survival.world.NetworkObject;
import isle.survival.world.SoundBase;
import isle.survival.world.TextureBase;
import isle.survival.world.WorldEffects;
import isle.survival.world.WorldObjects;
import isle.survival.world.effects.ProjectileEffect;
import isle.survival.world.effects.ProjectileType;
import isle.survival.world.effects.ResourceEffect;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import server.Connection;
import server.ServerProtocol;
import world.BuildWallAction;
import world.EffectType;
import world.Inventory;
import world.ItemType;
import world.Tool;
import world.World;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
	private WorldEffects worldEffects;
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
		worldEffects = new WorldEffects(textureBase, spriteBatch);
		inventory = new Inventory();
		connectToServer();

		ui = new Ui(textureBase, inventory, coder);
		
		inputProcessor = new InputProcessor(ui, coder);
		Gdx.input.setInputProcessor(inputProcessor);
		
		if (coder == null)
			Gdx.app.exit();
	}
	
	private void connectToServer() {
		try {
			socket = new Socket("localhost", 1337);
			coder = new ClientProtocolCoder(name, new Connection(socket));
			new ServerListener(this).start();

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
		world.update(deltaTime);
		worldObjects.update(deltaTime);
		worldEffects.update(deltaTime);
		
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
		drawTool(xView, yView);
		worldObjects.draw(xView, yView);
		worldEffects.draw(xView, yView);
		
		world.drawTime();
		
		ui.draw(spriteBatch);
		
		spriteBatch.end();
	}
	
	private void drawTool(float xOffset, float yOffset) {
		Tool tool = ui.getBuildMenu().getSelectedTool();
		
		if (tool.getAction() instanceof BuildWallAction) {
			BuildWallAction action = (BuildWallAction) tool.getAction(); 
			int x = worldObjects.getPlayer().getServerX();
			int y = worldObjects.getPlayer().getServerY();
			spriteBatch.setColor(1, 1, 1, 0.5f);
			spriteBatch.draw(textureBase.getWallTileTexture(action.getTileToBuild().ordinal()),
					 x * World.TILE_WIDTH - xOffset, y * World.TILE_HEIGHT - yOffset);
			spriteBatch.setColor(Color.WHITE);
		}
	}
	
	@Override
	public void dispose() {
		textureBase.dispose();
		spriteBatch.dispose();
		soundBase.dispose();
		
		if (socket != null && !socket.isClosed()) {
			synchronized (this) {
				coder.sendClose();
			}
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
					int objectId = coder.getConnection().receiveInt();
					int amount = coder.getConnection().receiveInt();
					int itemId = coder.getConnection().receiveInt();
					NetworkObject object = worldObjects.getObject(objectId);
					if (object != null) {
						ItemType item = ItemType.values()[itemId];
						for(int i = 0; i < amount; i++)
							worldEffects.addEffect(new ResourceEffect(tileX, tileY, object, textureBase.getTexture(item.getTexture())));
					} else {
						System.out.println("Invalid object id: " + objectId);
					}
				} else if (type == EffectType.Projectile) {
					ProjectileType projectileType = ProjectileType.values()[coder.getConnection().receiveInt()];
					int originId = coder.getConnection().receiveInt();
					int targetId = coder.getConnection().receiveInt();
					
					NetworkObject originObject = worldObjects.getObject(originId);
					NetworkObject targetObject = worldObjects.getObject(targetId);
					if (originObject != null && targetObject != null) {
						Texture texture = textureBase.getTexture(projectileType.getTexture());
						worldEffects.addEffect(new ProjectileEffect(originObject, targetObject, projectileType.getSpeed(), texture));
					}
				}
				break;
			case SetInventory:
				inventory.receiveInventory(coder.getConnection());
				break;
			case TimeEvent:
				world.receiveTimeEvent(coder.getConnection());
				break;
			case SendWorldWallTiles:
				world.receiveWallTiles(coder.getConnection());
				break;
			case FailedToConnect:
				System.out.println("User name already in use.");
				closeSocket();
				Gdx.app.exit();
				Thread.currentThread().interrupt();
				break;
			case PlaySound:
				soundBase.playSound(coder.getConnection());
				break;
			case SendClose:
				System.out.println("Disconnected from host.");
				coder.acknowledgeClose();
				closeSocket();
				Gdx.app.exit();
				Thread.currentThread().interrupt();
				break;
			case AckClose:
				closeSocket();
				Thread.currentThread().interrupt();
				break;
			default:
				break;
			}
		}
	}

	private void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
