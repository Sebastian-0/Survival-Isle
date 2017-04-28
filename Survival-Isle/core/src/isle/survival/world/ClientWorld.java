package isle.survival.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import server.Connection;
import world.World;

@SuppressWarnings("serial")
public class ClientWorld extends World {
	private static final int DUSK_TIME = 3;
	private TextureBase textureBase;
	private SpriteBatch spriteBatch;
	private int[][] walls;
	private float[][] debug;
	private boolean isDaytime; 
	private Texture nightTexture;
	private double duskTimer;
	
	public ClientWorld(TextureBase textureBase, SpriteBatch spriteBatch) {
		this.textureBase = textureBase;
		this.spriteBatch = spriteBatch;
		ground = new int[0][0];
		walls = new int[0][0];
		debug = new float[0][0];
		isDaytime = true;
		
		nightTexture = new Texture("night.png");
		duskTimer = 0;
	}

	public void drawTerrain(float xOffset, float yOffset) {
		int startX = (int) (Math.max(xOffset / TILE_WIDTH, 0));
		int startY = (int) (Math.max(yOffset / TILE_HEIGHT, 0));
		int endX = (int) (Math.min((xOffset + Gdx.graphics.getWidth()) / TILE_WIDTH + 1, width));
		int endY = (int) (Math.min((yOffset + Gdx.graphics.getHeight()) / TILE_HEIGHT + 1, height));
		
		for (int i = startX; i < endX; i++) {
			for (int j = startY; j < endY; j++) {
				spriteBatch.draw(textureBase.getGroundTileTexture(ground[i][j]), i*TILE_WIDTH - xOffset, j*TILE_HEIGHT - yOffset);
			}
		}
		
		for (int i = startX; i < endX; i++) {
			for (int j = startY; j < endY; j++) {
				if (walls[i][j] != -1)
					spriteBatch.draw(textureBase.getWallTileTexture(walls[i][j]), i*TILE_WIDTH - xOffset, j*TILE_HEIGHT - yOffset);
			}
		}
		
	}
	
	public void drawTime() {
		if (duskTimer != 0) {
			spriteBatch.setColor(1, 1, 1, (float)(0.5 * duskTimer / DUSK_TIME));
			spriteBatch.draw(nightTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			spriteBatch.setColor(Color.WHITE);
		}
	}
	
	public void drawDebug(float xOffset, float yOffset) {
		int startX = (int) (Math.max(xOffset / TILE_WIDTH, 0));
		int startY = (int) (Math.max(yOffset / TILE_HEIGHT, 0));
		int endX = (int) (Math.min((xOffset + Gdx.graphics.getWidth()) / TILE_WIDTH + 1, width));
		int endY = (int) (Math.min((yOffset + Gdx.graphics.getHeight()) / TILE_HEIGHT + 1, height));
	
		for (int i = startX; i < endX; i++) {
			for (int j = startY; j < endY; j++) {
				if (debug[i][j] != 0) {
					//Draw debug
				}
			}
		}
	}
	
	
	public void update(double deltaTime) {
		if (!isDaytime && duskTimer < DUSK_TIME)
			duskTimer = Math.min(DUSK_TIME, duskTimer+deltaTime);
		else if (isDaytime && duskTimer > 0)
			duskTimer = Math.max(0, duskTimer-deltaTime);
	}
	
	public void receive(Connection connection) {
		width = connection.receiveInt();
		height = connection.receiveInt();
		ground = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				ground[x][y] = connection.receiveInt();
			}
		}

		walls = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				walls[x][y] = connection.receiveInt();
			}
		}

		debug = new float[width][height];
		clearDebug();
	}

	public void receiveWallTiles(Connection connection) {
		int amount = connection.receiveInt();
		for (int i = 0; i < amount; i++) {
			int x = connection.receiveInt();
			int y = connection.receiveInt();
			int id = connection.receiveInt();
			walls[x][y] = id;
		}
	}

	public void receiveTimeEvent(Connection connection) {
		int t = connection.receiveInt();
		isDaytime = t == 1;
		if (t == 2)
			duskTimer = DUSK_TIME;
	}

	public void clearDebug() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				debug[i][j] = 0;
			}
		}
	}

	public void updateDebug(int x, int y, float value) {
		debug[x][y] = value;
	}
}
