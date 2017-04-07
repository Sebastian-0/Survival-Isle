package world;

import java.util.Random;

import server.Connection;

public class ServerWorld extends World {
	private WallTile[][] walls;
	
	public ServerWorld(int width, int height) {
		super(width, height);
		walls = new WallTile[width][height];
	}
	
	public void GenerateTerrain(long seed) {
		Random random = new Random(seed);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				ground[x][y] = random.nextInt(2);
				if (ground[x][y] != 0 && random.nextInt(2) == 1) {
					walls[x][y] = new WallTile(0);
				}
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
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (walls[x][y] != null)
					connection.sendInt(walls[x][y].getId());
				else
					connection.sendInt(-1);
			}
		}
	}
}
