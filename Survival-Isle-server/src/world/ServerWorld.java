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
		generateGround(random);
		generateForests(random);
	}

	private void generateGround(Random random) {
		ground[width/2][height/2] = GroundTile.Grass.id;
		for (int i = 0; i < width*height/2;) {
			int x = random.nextInt(width-2)+1;
			int y = random.nextInt(height-2)+1;
			if (ground[x][y] == GroundTile.Water.id && nearGroundTile(x, y, GroundTile.Grass.id)) {
				ground[x][y] = GroundTile.Grass.id; 
				i++;
			}
		}
	}
	
	private void generateForests(Random random) {
		for (int i = 0; i < width*height/50; i++) {
			int x0 = random.nextInt(width-2)+1;
			int y0 = random.nextInt(height-2)+1;
			
			if (ground[x0][y0] == GroundTile.Grass.id && walls[x0][y0] == null) {
				walls[x0][y0] = new WallTile(WallTile.TileType.Forest.id);
				
				int size = random.nextInt(3) + 3;
				for (float j = 0; j < size; j += 0.1) {
					int x = Math.max(0, Math.min(width, x0 + random.nextInt(size) - size/2));
					int y = Math.max(0, Math.min(height, y0 + random.nextInt(size) - size/2));
					
					if (ground[x][y] == GroundTile.Grass.id && 
						nearWallTile(x, y, WallTile.TileType.Forest.id) && 
						walls[x][y] == null) {
						walls[x][y] = new WallTile(WallTile.TileType.Forest.id);
						j += 0.9;
					}
				}
			}
		}
	}

	private boolean nearGroundTile(int x, int y, int tileId) {
		if (ground[x+1][y] == tileId ||
			ground[x-1][y] == tileId ||
			ground[x][y+1] == tileId ||
			ground[x][y-1] == tileId) {
			return true;
		}
		return false;
	}
	
	private boolean nearWallTile(int x, int y, int tileId) {
		if (x >= 1 && x < width-1 && y >= 1 && y < height-1 && (
			(walls[x+1][y] != null && walls[x+1][y].getId() == tileId) ||
			(walls[x-1][y] != null && walls[x-1][y].getId() == tileId) ||
			(walls[x][y+1] != null && walls[x][y+1].getId() == tileId) ||
			(walls[x][y-1] != null && walls[x][y-1].getId() == tileId))) {
			return true;
		}
		return false;
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
