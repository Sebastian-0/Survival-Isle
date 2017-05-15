package isle.survival.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import server.Connection;
import util.Point;
import world.World;

@SuppressWarnings("serial")
public class ClientWorld extends World {
	private static final String DAY_MUSIC_NAME = "DayMusic";
	private static final String NIGHT_MUSIC_NAME = "NightMusic";
	private static final int DUSK_TIME = 3;
	private TextureBase textureBase;
	private SpriteBatch spriteBatch;
	private SoundBase soundBase;
	private int[][] walls;
	private int[][] damage;
	private float[][] debug;
	private int[][] shiny;
	private boolean isDaytime; 
	private Texture nightTexture;
	private double duskTimer;
	private long nightMusicId;
	
	public ClientWorld(TextureBase textureBase, SpriteBatch spriteBatch, SoundBase soundBase) {
		this.textureBase = textureBase;
		this.spriteBatch = spriteBatch;
		this.soundBase = soundBase;
		ground = new int[0][0];
		walls = new int[0][0];
		damage = new int[0][0];
		debug = new float[0][0];
		isDaytime = true;
		
		nightTexture = new Texture("night.png");
		duskTimer = 0;
		
		nightMusicId = -1;
	}

	private Point startPoint(float xOffset, float yOffset) {
		int startX = (int) (Math.max(xOffset / TILE_WIDTH, 0));
		int startY = (int) (Math.max(yOffset / TILE_HEIGHT, 0));
		return new Point(startX, startY);
	}
	private Point endPoint(float xOffset, float yOffset) {
		int endX = (int) (Math.min((xOffset + Gdx.graphics.getWidth()) / TILE_WIDTH + 1, width));
		int endY = (int) (Math.min((yOffset + Gdx.graphics.getHeight()) / TILE_HEIGHT + 1, height));
		return new Point(endX, endY);
	}

	public void drawTerrain(float xOffset, float yOffset) {
		Point start = startPoint(xOffset, yOffset);
		Point end = endPoint(xOffset, yOffset);
		
		for (int i = (int) start.x; i < end.x; i++) {
			for (int j = (int) start.y; j < end.y; j++) {
				spriteBatch.draw(textureBase.getGroundTileTexture(ground[i][j]), i*TILE_WIDTH - xOffset, j*TILE_HEIGHT - yOffset);
			}
		}
		
		for (int i = (int) start.x; i < end.x; i++) {
			for (int j = (int) start.y; j < end.y; j++) {
				if (walls[i][j] != -1)
					spriteBatch.draw(textureBase.getWallTileTexture(walls[i][j]), i*TILE_WIDTH - xOffset, j*TILE_HEIGHT - yOffset);
				if (damage[i][j] != -1)
					spriteBatch.draw(textureBase.getTexture("cracks_" + damage[i][j]), i*TILE_WIDTH - xOffset, j*TILE_HEIGHT - yOffset);
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
	
	public void drawShininess(float xOffset, float yOffset, int intensity) {
		if (intensity > 0) {
			Point start = startPoint(xOffset, yOffset);
			Point end = endPoint(xOffset, yOffset);
			
			spriteBatch.setColor(1, 1, 1, 0.01f * Math.min(intensity, 3));
	
			for (int i = (int) start.x; i < end.x; i++) {
				for (int j = (int) start.y; j < end.y; j++) {
					if (shiny[i][j] == 1) {
						spriteBatch.draw(textureBase.getTexture("shiny_tile"), i*TILE_WIDTH - xOffset, j*TILE_HEIGHT - yOffset);
					} else if (shiny[i][j] == -1) {
						spriteBatch.draw(textureBase.getTexture("unshiny_tile"), i*TILE_WIDTH - xOffset, j*TILE_HEIGHT - yOffset);
					}
				}
			}
			spriteBatch.setColor(1, 1, 1, 1);
		}
	}
	
	public void drawDebug(BitmapFont debugFont, float xOffset, float yOffset) {
		debugFont.setColor(Color.BLACK);

		Point start = startPoint(xOffset, yOffset);
		Point end = endPoint(xOffset, yOffset);
		for (int i = (int) start.x; i < end.x; i++) {
			for (int j = (int) start.y; j < end.y; j++) {
				if (debug[i][j] != 0) {
					float value = MathUtils.floor(debug[i][j]*100)/100f;
//					if ((float)((int)value) == value)
						debugFont.draw(spriteBatch, ""+(int)value, i*TILE_WIDTH - xOffset, (j+1)*TILE_HEIGHT - yOffset);
//					else
//						debugFont.draw(spriteBatch, ""+value, i*TILE_WIDTH - xOffset, (j+1)*TILE_HEIGHT - yOffset);
				}
			}
		}
	}
	
	
	public void update(double deltaTime) {
		if (!isDaytime && duskTimer < DUSK_TIME) {
			duskTimer = Math.min(DUSK_TIME, duskTimer+deltaTime);

			if (nightMusicId != -1) {
				soundBase.setVolumeOfMusic(NIGHT_MUSIC_NAME, nightMusicId, (float)(duskTimer/DUSK_TIME) *0.5f);
				System.out.println((float)(duskTimer/DUSK_TIME) *0.5f);
			}
		} else if (isDaytime && duskTimer > 0)
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
		shiny = new int[width][height];
		damage = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				walls[x][y] = connection.receiveInt();

				shiny[x][y] = Math.random() < 0.2 ? 1 : 0;
				if (shiny[x][y] == 0)
					shiny[x][y] = Math.random() < 0.4 ? -1 : 0;
				
				damage[x][y] = -1;
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
			damage[x][y] = -1;
			soundBase.playSound("break_tile");
		}
	}

	public void receiveTimeEvent(Connection connection) {
		int t = connection.receiveInt();
		isDaytime = t == 1;
		if (t == 2)
			duskTimer = DUSK_TIME;
		if (isDaytime) {
			soundBase.stopMusic(NIGHT_MUSIC_NAME);
			soundBase.stopMusic(DAY_MUSIC_NAME);
			soundBase.playMusic(DAY_MUSIC_NAME);
		} else {
			soundBase.stopMusic(NIGHT_MUSIC_NAME);
			soundBase.stopMusic(DAY_MUSIC_NAME);
			nightMusicId = soundBase.playMusic(NIGHT_MUSIC_NAME);
		}
	}
	
	public void receiveTileDamage(Connection connection) {
		int x = connection.receiveInt();
		int y = connection.receiveInt();
		int d = connection.receiveInt();
		damage[x][y] = d;
		soundBase.playSound("hit_tile");
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
