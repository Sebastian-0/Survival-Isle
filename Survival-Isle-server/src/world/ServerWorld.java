package world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import server.Connection;
import util.Point;

public class ServerWorld extends World {
	private WallTile[][] walls;
	
	public ServerWorld(int width, int height) {
		super(width, height);
		walls = new WallTile[width][height];
	}
	
	public void GenerateTerrain(long seed) {
		Random random = new Random(seed);
		List<Point> coast = generateGround(random);
		generateRivers(random, coast);
		generateForests(random);
	}

	private List<Point> generateGround(Random random) {
		List<Point> coast = new ArrayList<>();
		
		ground[width/2][height/2] = GroundTile.Grass.id;
		coast.add(new Point(width/2+1, height/2));
		coast.add(new Point(width/2-1, height/2));
		coast.add(new Point(width/2, height/2+1));
		coast.add(new Point(width/2, height/2-1));
		
		for (int i = 0; i < width*height/2; i++) {
			int index = random.nextInt(coast.size());
			int x = (int) coast.get(index).x;
			int y = (int) coast.remove(index).y;
			
			if (ground[x][y] != GroundTile.Grass.id) {
				ground[x][y] = GroundTile.Grass.id;

				if (x >= 2 && ground[x-1][y] != GroundTile.Grass.id)
					coast.add(new Point(x-1, y));
				if (x < width-2 && ground[x+1][y] != GroundTile.Grass.id)
					coast.add(new Point(x+1, y));
				if (y >= 2 && ground[x][y-1] != GroundTile.Grass.id)
					coast.add(new Point(x, y-1));
				if (y < height-2 && ground[x][y+1] != GroundTile.Grass.id)
					coast.add(new Point(x, y+1));
			}
		}
		
		return coast;
	}

	private void generateForests(Random random) {
		for (int i = 0; i < width*height/50; i++) {
			int x0 = random.nextInt(width-2)+1;
			int y0 = random.nextInt(height-2)+1;
			
			if (ground[x0][y0] == GroundTile.Grass.id && walls[x0][y0] == null) {
				walls[x0][y0] = new WallTile(WallTile.TileType.Forest.id);
				
				int size = random.nextInt(8) + 3;
				for (float j = 0; j < size; j += 0.1) {
					int x = Math.max(0, Math.min(width, x0 + random.nextInt(size) - size/2));
					int y = Math.max(0, Math.min(height, y0 + random.nextInt(size) - size/2));
					
					if (ground[x][y] == GroundTile.Grass.id && 
						isNearWallTile(x, y, WallTile.TileType.Forest.id) && 
						walls[x][y] == null) {
						walls[x][y] = new WallTile(WallTile.TileType.Forest.id);
						j += 0.9;
					}
				}
			}
		}
	}

	private boolean isNearWallTile(int x, int y, int tileId) {
		if (x >= 1 && x < width-1 && y >= 1 && y < height-1 && (
			(walls[x+1][y] != null && walls[x+1][y].getId() == tileId) ||
			(walls[x-1][y] != null && walls[x-1][y].getId() == tileId) ||
			(walls[x][y+1] != null && walls[x][y+1].getId() == tileId) ||
			(walls[x][y-1] != null && walls[x][y-1].getId() == tileId))) {
			return true;
		}
		return false;
	}

	private void generateRivers(Random random, List<Point> coast) {
		for (int i = 0; i < Math.sqrt(width*height)/20; i++) {
			int index = random.nextInt(coast.size());
			int x = (int)coast.get(index).x;
			int y = (int)coast.remove(index).y;
			int dx = 0;
			int dy = -1;
			
			System.out.println("River at (" + x + ", " + y + ")");
			
			if (ground[x+1][y] == GroundTile.Grass.id) {
				dx = 1;
				dy = 0;
			} else if (ground[x][y+1] == GroundTile.Grass.id) {
				dx = 0;
				dy = 1;
			} else if (ground[x-1][y] == GroundTile.Grass.id) {
				dx = -1;
				dy = 0;
			} 
			
			x += dx;
			y += dy;
			
			while (ground[x][y] != GroundTile.Water.id) {
				ground[x][y] = GroundTile.Water.id;
				if (random.nextInt(4) < 1) {
					if (random.nextBoolean()) {
						int t = -dy;
						dy = dx;
						dx = t;
					} else {
						int t = dy;
						dy = -dx;
						dx = t;
					}
				}
				x += dx;
				y += dy;
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

	public WallTile getWallTile(int x, int y) {
		return walls[x][y];
	}
}
