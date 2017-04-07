package world;

import java.util.Random;

import server.Connection;

public class ServerWorld extends World {
	public ServerWorld(int width, int height) {
		super(width, height);
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
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				connection.sendInt(-1);
			}
		}
	}
}
