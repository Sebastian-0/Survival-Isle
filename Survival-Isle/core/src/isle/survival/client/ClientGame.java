package isle.survival.client;

import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
import server.Connection;
import server.ServerProtocol;
import world.EffectType;
import world.Inventory;
import world.ItemType;
import world.Tool;
import world.World;

public class ClientGame {
	private String name;
	private TextureBase textureBase;
	private SpriteBatch spriteBatch;
	private SoundBase soundBase;
	private InputProcessor inputProcessor;
	private GameProtocolCoder coder;
	
	private ClientWorld world;
	private WorldObjects worldObjects;
	private WorldEffects worldEffects;
	private Inventory inventory;
	private float xView;
	private float yView;
	
	private Ui ui;
	

	public ClientGame(String name, SpriteBatch spriteBatch, TextureBase textureBase, SoundBase soundBase) {
		this.name = name;
		this.spriteBatch = spriteBatch;
		this.textureBase = textureBase;
		this.soundBase = soundBase;

		world = new ClientWorld(textureBase, spriteBatch);
		worldObjects = new WorldObjects(textureBase, spriteBatch);
		worldEffects = new WorldEffects(textureBase, spriteBatch);
		inventory = new Inventory();
	}
	
	public ClientProtocolCoder connectToServer(Socket socket) {
		coder = new GameProtocolCoder(name, new Connection(socket));

		initOnceConnected();
		return coder;
	}

	private void initOnceConnected() {
		ui = new Ui(textureBase, inventory, coder);
		
		inputProcessor = new InputProcessor(ui, coder);
		Gdx.input.setInputProcessor(inputProcessor);
	}
	
	
	public void update() {
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
		ui.update(deltaTime);
		coder.flush();
	}
	
	public void draw() {
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
		
		if (tool != Tool.Pickaxe) { 
			int x = worldObjects.getPlayer().getServerX();
			int y = worldObjects.getPlayer().getServerY();
			spriteBatch.setColor(1, 1, 1, 0.5f);
			spriteBatch.draw(
					ui.getBuildMenu().getSelectedToolIcon(),
					x * World.TILE_WIDTH - xOffset,
					y * World.TILE_HEIGHT - yOffset, 
					World.TILE_WIDTH,
					World.TILE_HEIGHT);
			spriteBatch.setColor(Color.WHITE);
		}
	}
	
	public void parseServerMessage(ServerProtocol code) {
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
			case PlaySound:
				soundBase.playSound(coder.getConnection());
				break;
			case SendChatMessage:
				String sender = coder.getConnection().receiveString();
				String message = coder.getConnection().receiveString();
				ui.getChatHistory().addMessage(sender, message);
				break;
			default:
				break;
			}
		}
	}

	public void dispose() {
		ui.dispose();
	}
}