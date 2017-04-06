package world;

import java.util.Random;

import server.Connection;

public class World {
	public static final int TILE_WIDTH = 32;
	public static final int TILE_HEIGHT = 32;
	
	public enum GroundTile {
		WATER(0, "water.png"),
		GRASS(1, "grass.png");
		
		
		public final int id;
		public final String textureName;
		
		private GroundTile(int id, String textureName) {
			this.id = id;
			this.textureName = textureName;
		}
	}
	
	protected int[][] ground;
	protected int width;
	protected int height;
	
	public World(int width, int height) {
		this.width = width;
		this.height = height;
		ground = new int[width][height];
	}
	
	protected World() {
	}
	
	
	public void GenerateTerrain(long seed) {
		Random random = new Random(seed);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				ground[x][y] = random.nextInt(2);
			}
		}
	}
	
	public void send(Connection connection) {
		connection.sendInt(width);
		connection.sendInt(height);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				connection.sendInt(ground[x][y]);
			}
		}
	}
}
