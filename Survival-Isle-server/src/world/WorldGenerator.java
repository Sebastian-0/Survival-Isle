package world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.Point;
import world.World.GroundType;

public class WorldGenerator {
	
	public GenerationResult generateTerrain(int width, int height, Random random) {
		GenerationResult result = new GenerationResult(width, height);
		
		generateGround(result, random);
		generateForests(result, random);
		generateMountains(result, random);
		generateRivers(result, random);
		generateCoastline(result, random);
		generateEnemySpawnPoints(result, random);
		
		return result;
	}

	private void generateGround(GenerationResult r, Random random) {
		r.ground[r.width/2][r.height/2] = GroundType.Grass.ordinal();
		r.coast.add(new Point(r.width/2+1, r.height/2));
		r.coast.add(new Point(r.width/2-1, r.height/2));
		r.coast.add(new Point(r.width/2, r.height/2+1));
		r.coast.add(new Point(r.width/2, r.height/2-1));
		
		for (int i = 0; i < r.width*r.height/2; i++) {
			int index = random.nextInt(r.coast.size());
			int x = (int) r.coast.get(index).x;
			int y = (int) r.coast.remove(index).y;
			
			if (!isTileOfType(r, x, y, GroundType.Grass)) {
				if (random.nextInt(20) == 0)
					r.ground[x][y] = GroundType.Flowers.ordinal();
				else
					r.ground[x][y] = GroundType.Grass.ordinal();

				if (x >= 2 && isTileOfType(r, x-1, y, GroundType.Water))
					r.coast.add(new Point(x-1, y));
				if (x < r.width-2 && isTileOfType(r, x+1, y, GroundType.Water))
					r.coast.add(new Point(x+1, y));
				if (y >= 2 && isTileOfType(r, x, y-1, GroundType.Water))
					r.coast.add(new Point(x, y-1));
				if (y < r.height-2 && isTileOfType(r, x, y+1, GroundType.Water))
					r.coast.add(new Point(x, y+1));
			}
		}
		
		for (int i = 0; i < r.width; i++) {
			for (int j = 0; j < r.height; j++) {
				if (isTileOfType(r, i, j, GroundType.Water))
					r.walls[i][j] = new WallTile(WallTile.WallType.Water);
			}
		}
	}

	private void generateForests(GenerationResult r, Random random) {
		int quantity = Math.max(1,r.width*r.height/200);
		int minSize = 3;
		int maxSize = 11;
		generateLocalEnvironment(r, random, WallTile.WallType.Forest, 
				GroundType.Stump, quantity, minSize, maxSize);
	}

	private void generateMountains(GenerationResult r, Random random) {
		int quantity = Math.max(1,r.width*r.height/800);
		int minSize = 12;
		int maxSize = 28;
		generateLocalEnvironment(r, random, WallTile.WallType.Mountain, 
				GroundType.Rock, quantity, minSize, maxSize);
	}
	
	private void generateLocalEnvironment(GenerationResult r, Random random, WallTile.WallType wallType, 
			GroundType groundType, int quantity, int minSize, int maxSize) {
		List<Point> edges = new ArrayList<>();
		
		for (int i = 0; i < quantity;) {
			int x0 = random.nextInt(r.width-2)+1;
			int y0 = random.nextInt(r.height-2)+1;
			
			if (r.walls[x0][y0] == null) {
				r.walls[x0][y0] = new WallTile(wallType);
				r.ground[x0][y0] = groundType.ordinal();
				addWallsToEdgeList(r, x0, y0, edges);
				
				int size = random.nextInt(maxSize-minSize) + minSize;
				for (float j = 0; j < size && !edges.isEmpty();) {
					int index = random.nextInt(edges.size());
					int x = (int) edges.get(index).x;
					int y = (int) edges.remove(index).y;
					
					if (r.walls[x][y] == null) {
						r.walls[x][y] = new WallTile(wallType);
						r.ground[x][y] = groundType.ordinal();
						j++;
						
						addWallsToEdgeList(r, x, y, edges);
					}
				}
				i++;
			}
			edges.clear();
		}
	}

	private void addWallsToEdgeList(GenerationResult r, int x, int y, List<Point> edge) {
		if (x >= 2 && x < r.width-2 && y >= 2 && y < r.height-2) {
			if (r.walls[x+1][y] == null)
				edge.add(new Point(x+1, y));
			if (r.walls[x-1][y] == null)
				edge.add(new Point(x-1, y));
			if (r.walls[x][y+1] == null)
				edge.add(new Point(x, y+1));
			if (r.walls[x][y-1] == null)
				edge.add(new Point(x, y-1));
		}
	}

	private void generateRivers(GenerationResult r, Random random) {
		for (int i = 0; i < Math.sqrt(r.width*r.height)/20; i++) {
			int index = random.nextInt(r.coast.size());
			int x = (int)r.coast.get(index).x;
			int y = (int)r.coast.remove(index).y;
			
			if (isTileOfType(r, x, y, GroundType.Water)) {
				int dx = 0;
				int dy = -1;
				
				if (isTileOfType(r, x+1, y, GroundType.Grass)) {
					dx = 1;
					dy = 0;
				} else if (isTileOfType(r, x, y+1, GroundType.Grass)) {
					dx = 0;
					dy = 1;
				} else if (isTileOfType(r, x-1, y, GroundType.Grass)) {
					dx = -1;
					dy = 0;
				} 
				
				x += dx;
				y += dy;
				
				while (!isTileOfType(r, x, y, GroundType.Water)) {
					r.ground[x][y] = GroundType.Water.ordinal();
					r.walls[x][y] = new WallTile(WallTile.WallType.Water);
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
	
	private void generateCoastline(GenerationResult r, Random random) {
		for (Point tile : r.coast) {
			int x = (int) tile.x;
			int y = (int) tile.y;
			coastifyTile(r, x+1, y);
			coastifyTile(r, x-1, y);
			coastifyTile(r, x, y+1);
			coastifyTile(r, x, y-1);
			
			if (isTileOfType(r, x, y, GroundType.Water)) {
				r.ground[x][y] = GroundType.ShallowWater.ordinal();
				r.walls[x][y] = null;
			}
		}
	}

	private void coastifyTile(GenerationResult r, int x, int y) {
		if ((isTileOfType(r, x, y, GroundType.Grass) || isTileOfType(r, x, y, GroundType.Flowers)) 
				&& r.walls[x][y] == null) {
			r.ground[x][y] = GroundType.Beach.ordinal();
		} else if (isTileOfType(r, x, y, GroundType.Water)) {
			r.ground[x][y] = GroundType.ShallowWater.ordinal();
			r.walls[x][y] = null;
		}
	}
	
	private boolean isTileOfType(GenerationResult r, int x, int y, GroundType type) {
		return r.ground[x][y] == type.ordinal();
	}

	private void generateEnemySpawnPoints(GenerationResult r, Random random) {
		int quantity = Math.max(1,r.width*r.height/500);
		for (int i = 0; i < quantity; i++) {
			int x = (int) (r.width * Math.min(6,Math.max(0,random.nextGaussian()+3)) / 6);
			int y = (int) (r.height * Math.min(6,Math.max(0,random.nextGaussian()+3)) / 6);
			
			if (x < r.width && y < r.height && r.walls[x][y] == null) {
				r.walls[x][y] = new WallTile(WallTile.WallType.EnemySpawn);
				r.enemySpawnPoints.add(new Point(x,y));
			}
			else
				i--;
		}
	}
	
	
	public static class GenerationResult {
		public int width;
		public int height;
		
		public int[][] ground;
		public WallTile[][] walls;
		
		public List<Point> coast;
		public List<Point> enemySpawnPoints;
		
		public GenerationResult(int width, int height) {
			this.width = width;
			this.height = height;
			ground = new int[width][height];
			walls = new WallTile[width][height];
			enemySpawnPoints = new ArrayList<Point>();
			coast = new ArrayList<>();
		}
	}
}
