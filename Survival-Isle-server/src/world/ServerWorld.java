package world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import server.Connection;
import util.Point;
import world.WallTile.TileType;

@SuppressWarnings("serial")
public class ServerWorld extends World implements Serializable {
	private WallTile[][] walls;
	private List<Point> wallTilesToUpdate; 
	private List<Point> coast;
	private Random random;
	
	private GameInterface game;
	
	public ServerWorld(int width, int height, GameInterface game) {
		super(width, height);
		walls = new WallTile[width][height];
		wallTilesToUpdate = new LinkedList<Point>();
		this.game = game;
	}
	
	public void generateTerrain() {
		random = new Random();
		long seed = random.nextLong();
		random.setSeed(seed);
		System.out.println("World seed: " + seed);
		
		coast = generateGround(random);
		generateForests(random);
		generateMountains(random);
		generateRivers(random, coast);
		generateCoastline(random, coast);
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
				if (random.nextInt(20) == 0)
					ground[x][y] = GroundTile.Flowers.id;
				else
				ground[x][y] = GroundTile.Grass.id;

				if (x >= 2 && ground[x-1][y] == GroundTile.Water.id)
					coast.add(new Point(x-1, y));
				if (x < width-2 && ground[x+1][y] == GroundTile.Water.id)
					coast.add(new Point(x+1, y));
				if (y >= 2 && ground[x][y-1] == GroundTile.Water.id)
					coast.add(new Point(x, y-1));
				if (y < height-2 && ground[x][y+1] == GroundTile.Water.id)
					coast.add(new Point(x, y+1));
			}
		}
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (ground[i][j] == GroundTile.Water.id)
					walls[i][j] = new WallTile(WallTile.TileType.Water);
			}
		}
		
		return coast;
	}

	private void generateForests(Random random) {
		int quantity = Math.max(1,width*height/200);
		int minSize = 3;
		int maxSize = 11;
		generateLocalEnvironment(random, WallTile.TileType.Forest, 
				GroundTile.Stump, quantity, minSize, maxSize);
	}

	private void generateMountains(Random random) {
		int quantity = Math.max(1,width*height/800);
		int minSize = 12;
		int maxSize = 28;
		generateLocalEnvironment(random, WallTile.TileType.Mountain, 
				GroundTile.Rock, quantity, minSize, maxSize);
	}
	
	private void generateLocalEnvironment(Random random, WallTile.TileType wallType, 
			GroundTile groundType, int quantity, int minSize, int maxSize) {
		List<Point> edge = new ArrayList<>();
		
		for (int i = 0; i < quantity;) {
			int x0 = random.nextInt(width-2)+1;
			int y0 = random.nextInt(height-2)+1;
			
			if (walls[x0][y0] == null) {
				walls[x0][y0] = new WallTile(wallType);
				ground[x0][y0] = groundType.id;
				addWallsToEdgeList(x0,y0, edge);
				
				int size = random.nextInt(maxSize-minSize) + minSize;
				for (float j = 0; j < size && !edge.isEmpty();) {
					int index = random.nextInt(edge.size());
					int x = (int) edge.get(index).x;
					int y = (int) edge.remove(index).y;
					
					if (walls[x][y] == null) {
						walls[x][y] = new WallTile(wallType);
						ground[x][y] = groundType.id;
						j++;
						
						addWallsToEdgeList(x,y, edge);
					}
				}
				i++;
			}
			edge.clear();
		}
	}

	private void addWallsToEdgeList(int x, int y, List<Point> edge) {
		if (x >= 2 && x < width-2 && y >= 2 && y < height-2) {
			if (walls[x+1][y] == null)
				edge.add(new Point(x+1, y));
			if (walls[x-1][y] == null)
				edge.add(new Point(x-1, y));
			if (walls[x][y+1] == null)
				edge.add(new Point(x, y+1));
			if (walls[x][y-1] == null)
				edge.add(new Point(x, y-1));
		}
	}

	private void generateRivers(Random random, List<Point> coast) {
		for (int i = 0; i < Math.sqrt(width*height)/20; i++) {
			int index = random.nextInt(coast.size());
			int x = (int)coast.get(index).x;
			int y = (int)coast.remove(index).y;
			
			if (ground[x][y] == GroundTile.Water.id) {
				int dx = 0;
				int dy = -1;
				
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
					walls[x][y] = new WallTile(WallTile.TileType.Water);
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
			} else
				i--;
		}
	}
	
	private void generateCoastline(Random random, List<Point> coast) {
		for (Point tile : coast) {
			int x = (int) tile.x;
			int y = (int) tile.y;
			coastifyTile(x+1, y);
			coastifyTile(x-1, y);
			coastifyTile(x, y+1);
			coastifyTile(x, y-1);
			
			if (ground[x][y] == GroundTile.Water.id) {
				ground[x][y] = GroundTile.ShallowWater.id;
				walls[x][y] = null;
			}
		}
	}

	private void coastifyTile(int x, int y) {
		if ((ground[x][y] == GroundTile.Grass.id || ground[x][y] == GroundTile.Flowers.id) 
				&& walls[x][y] == null) {
			ground[x][y] = GroundTile.Beach.id;
		} else if (ground[x][y] == GroundTile.Water.id) {
			ground[x][y] = GroundTile.ShallowWater.id;
			walls[x][y] = null;
		}
	}

	public WallTile getWallTileAtPosition(int x, int y) {
		return walls[x][y];
	}
	
	public void addWallTileAtPosition(int x, int y, TileType tile) {
		walls[x][y] = new WallTile(tile);
		wallTilesToUpdate.add(new Point(x, y));
	}

	public boolean attackWallTileAtPosition(int x, int y, int damage, Player source) {
		WallTile tile = walls[x][y];
		if (tile.isBreakable()) {
			if (tile.damage(damage)) {
				game.doForEachClient(client->client.sendCreateEffect(EffectType.TileDestroyed, x, y, source.getId(), tile.getId()));
				tile.dropItems(source.getInventory());
				wallTilesToUpdate.add(new Point(x, y));
				walls[x][y] = null;
			}
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

	public void sendWallTileUpdate(Connection connection) {
		connection.sendInt(wallTilesToUpdate.size());
		for (int i = 0; i < wallTilesToUpdate.size(); i++) {
			Point p = wallTilesToUpdate.get(i);
			connection.sendInt((int) p.x);
			connection.sendInt((int) p.y);
			if (walls[(int)p.x][(int)p.y] == null) {
				connection.sendInt(-1);
			} else {
				connection.sendInt(walls[(int)p.x][(int)p.y].getId());
			}
		}
	}
	
	public boolean shouldUpdateWallTiles() {
		return !wallTilesToUpdate.isEmpty();
	}

	public void clearWallTileUpdateList() {
		wallTilesToUpdate.clear();
	}

	public Point getNewSpawnPoint() {
		for (int i = 0; i < 10000; i++) {
			int index = random.nextInt(coast.size());
			int x = (int) coast.get(index).x;
			int y = (int) coast.get(index).y;

			if (isValidSpawnPoint(x+2,y))
				return new Point(x+2, y);
			if (isValidSpawnPoint(x,y+2))
				return new Point(x, y+2);
			if (isValidSpawnPoint(x-2,y))
				return new Point(x-2, y);
			if (isValidSpawnPoint(x,y-2))
				return new Point(x, y-2);
		}
		
		return new Point(width/2, height/2);
	}

	private boolean isValidSpawnPoint(int x, int y) {
		
		if (x >= 2 && x < width-2 && y >= 2 && y < height-2 &&
			walls[x][y] == null &&
			walls[x+1][y] == null &&
			walls[x-1][y] == null &&
			walls[x][y+1] == null &&
			walls[x][y-1] == null)
			return true;
		return false;
	}
}
