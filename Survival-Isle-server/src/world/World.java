package world;

import java.util.Random;

public class World {
	public int TILE_WIDTH = 32;
	public int TILE_HEIGHT = 32;
	
	protected int[][] ground;
	protected int width;
	protected int height;
	
	public World(int width, int height) {
		this.width = width;
		this.height = height;
		ground = new int[width][height];
	}
	
	
	
	public void GenerateTerrain(long seed) {
		Random random = new Random(seed);
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				ground[i][j] = random.nextInt(2);
			}
		}
	}
	
	
}
