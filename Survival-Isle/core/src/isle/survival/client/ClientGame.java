package isle.survival.client;

import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import isle.survival.input.ClientGameInterface;
import isle.survival.input.InputProcessor;
import isle.survival.shaders.Shaders;
import isle.survival.ui.Ui;
import isle.survival.world.ClientWorld;
import isle.survival.world.NetworkObject;
import isle.survival.world.ParticleBase;
import isle.survival.world.SoundBase;
import isle.survival.world.TextureBase;
import isle.survival.world.WorldEffects;
import isle.survival.world.WorldObjects;
import isle.survival.world.effects.EnemyDiedEffect;
import isle.survival.world.effects.ProjectileEffect;
import isle.survival.world.effects.ProjectileType;
import isle.survival.world.effects.ResourceEffect;
import server.Connection;
import server.ServerProtocol;
import util.Point;
import world.EffectType;
import world.Inventory;
import world.ItemType;
import world.Tool;
import world.World;

public class ClientGame implements ClientGameInterface {
	private TextureBase textureBase;
	private ParticleBase particleBase;
	private SoundBase soundBase;
	
	private String name;
	private SpriteBatch spriteBatch;
	private InputProcessor inputProcessor;
	private GameProtocolCoder coder;
	private FrameBuffer frameBuffer;
	
	private ClientWorld world;
	private WorldObjects worldObjects;
	private WorldEffects worldEffects;
	private Inventory inventory;
	private float xTile;
	private float yTile;
	private float xView;
	private float yView;
	private boolean gameOver;
	private float gameOverFadeTimer;
	private int deathCount;

	private boolean inDebugMode;
	private boolean inPerformanceMode;
	private BitmapFont debugFont;
	
	private Ui ui;


	public ClientGame(String name, SpriteBatch spriteBatch, TextureBase textureBase, ParticleBase particleBase, SoundBase soundBase) {
		this.name = name;
		this.spriteBatch = spriteBatch;
		this.textureBase = textureBase;
		this.particleBase = particleBase;
		this.soundBase = soundBase;

		world = new ClientWorld(textureBase, spriteBatch, soundBase);
		worldObjects = new WorldObjects(textureBase, spriteBatch);
		worldEffects = new WorldEffects(textureBase, spriteBatch);
		inventory = new Inventory();
		
		debugFont = new BitmapFont(Gdx.files.internal("debug.fnt"));

		frameBuffer = new FrameBuffer(Format.RGBA4444, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
	}

	public ClientProtocolCoder connectToServer(Socket socket) {
		coder = new GameProtocolCoder(name, new Connection(socket));

		initOnceConnected();
		return coder;
	}

	private void initOnceConnected() {
		ui = new Ui(textureBase, inventory, coder, worldObjects);
		
		inputProcessor = new InputProcessor(ui, this, coder, soundBase);
		Gdx.input.setInputProcessor(inputProcessor);
	}
	
	
	public void update() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		world.update(deltaTime);
		worldObjects.update(deltaTime);
		worldEffects.update(deltaTime);
		
		NetworkObject player = worldObjects.getPlayer();
		if (player != null) {
			xTile = player.getX();
			yTile = player.getY();
			xView = xTile * World.TILE_WIDTH - Gdx.graphics.getWidth()/2;
			yView = yTile * World.TILE_HEIGHT - Gdx.graphics.getHeight()/2;
		}
		
		
		soundBase.setCameraPosition(xTile, yTile);
		
		inputProcessor.update(deltaTime);
		ui.update(deltaTime);
		
		if (inDebugMode) {
			coder.sendDebugRequest();
		}
		
		if (gameOver)
			gameOverFadeTimer = Math.min(gameOverFadeTimer + deltaTime/4, 0.8f);
		
		coder.flush();
	}
	
	public void draw() {
		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		frameBuffer.bind();
		
		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		world.drawTerrain(xView, yView);
		drawTool(xView, yView);
		worldObjects.draw(xView, yView);
		worldEffects.draw(xView, yView);
		
		if (!inPerformanceMode)
			world.drawShininess(xView, yView, inventory.getAmount(ItemType.RespawnCrystal));
		
		world.drawTime();
		
		spriteBatch.flush();
		frameBuffer.end();

		spriteBatch.setShader(Shaders.monochromeShader);
		
		float colorIntensity = inventory.getAmount(ItemType.RespawnCrystal) > 0 ? 1 : Math.max(0.9f - 0.1f*deathCount, 0.3f);
		if (gameOver)
			colorIntensity *= 1-gameOverFadeTimer;
		
		spriteBatch.getShader().setUniformf("colorIntensity", colorIntensity);
		
		spriteBatch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
		spriteBatch.setShader(Shaders.colorShader);
		
		if (inDebugMode)
			world.drawDebug(debugFont, xView, yView);
		
		ui.draw(spriteBatch);

		if (soundBase.isSoundMuted()) {
			spriteBatch.draw(textureBase.getTexture("mute_sound"), Gdx.graphics.getWidth()-32, 96);
		}

		if (soundBase.isMusicMuted()) {
			spriteBatch.draw(textureBase.getTexture("mute_music"), Gdx.graphics.getWidth()-32, 64);
		}
		
		if (gameOver) {
			TextureRegion t = textureBase.getTexture("game_over");
			int x = Gdx.graphics.getWidth()/2 - t.getRegionWidth()/2;
			int y = Math.min(Gdx.graphics.getHeight()*2/3, Gdx.graphics.getHeight() - t.getRegionHeight());
			spriteBatch.draw(t, x, y);
		}
		
		spriteBatch.end();
	}
	
	private void drawTool(float xOffset, float yOffset) {
		Tool tool = ui.getBuildMenu().getSelectedTool();
		
		if (tool != Tool.Pickaxe && worldObjects.getPlayer() != null) { 
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
				//ui.draw(spriteBatch);
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
						soundBase.playSoundAtPosition("turret_fire", new Point(originObject.getX(), originObject.getY()));
						worldEffects.addEffect(new ProjectileEffect(originObject, targetObject, projectileType, textureBase));
					}
				} else if (type == EffectType.EnemyDied) {
					int objectId = coder.getConnection().receiveInt();
					NetworkObject enemy = worldObjects.getObject(objectId);
					if (enemy != null) {
						soundBase.playSoundAtPosition("enemy_death", new Point(enemy.getX(), enemy.getY()));
						if (!inPerformanceMode)
							worldEffects.addEffect(new EnemyDiedEffect(enemy, particleBase));
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
			case SendGameOver:
				gameOver = true;
				break;
			case SendDeathCount:
				deathCount = coder.getConnection().receiveInt();
				break;
			case SendTileDamage:
				world.receiveTileDamage(coder.getConnection());
				break;
			case SendDebug:
				world.clearDebug();
				while (coder.getConnection().receiveInt() == 1) {
					int x = coder.getConnection().receiveInt();
					int y = coder.getConnection().receiveInt();
					float value = Float.parseFloat(coder.getConnection().receiveString());
					world.updateDebug(x, y, value);
				}
				break;
			default:
				break;
			}
		}
	}

	public void dispose() {
		ui.dispose();
		frameBuffer.dispose();
		debugFont.dispose();
	}

	public void resize(int width, int height) {
		frameBuffer.dispose();
		frameBuffer = new FrameBuffer(Format.RGBA4444, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
	}

	@Override
	public void toggleDebug() {
		inDebugMode = !inDebugMode;
	}

	@Override
	public void togglePerformanceMode() {
		inPerformanceMode = !inPerformanceMode;
		
	}
}
