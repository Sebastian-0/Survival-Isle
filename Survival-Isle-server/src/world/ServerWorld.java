package world;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import server.Connection;
import util.Point;
import world.WallTile.WallType;
import world.WorldGenerator.GenerationResult;

public class ServerWorld extends World implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final float PATH_MULTIPLIER_DECAY = 0.9f;
	
	private GameInterface game;
	
	private Random random;
	
	private float[][] temporaryPathMultipliers;
	private WallTile[][] walls;
	
	private List<Point> wallTilesToUpdate; 
	private List<Point> coast;
	private List<Point> enemySpawnPoints;
	
	
	public ServerWorld(int width, int height, GameInterface game) {
		super(width, height);
		this.game = game;
		temporaryPathMultipliers = new float[width][height];
		wallTilesToUpdate = new LinkedList<Point>();
	}
	
	public void generateTerrain() {
		random = new Random();
		long seed = random.nextLong();
		random.setSeed(seed);
		System.out.println("World seed: " + seed);
		
		GenerationResult result = new WorldGenerator().generateTerrain(width, height, random);
		ground = result.ground;
		walls = result.walls;
		coast = result.coast;
		enemySpawnPoints = result.enemySpawnPoints;
	}
	
	public WallTile getWallTileAtPosition(Point position) {
		return walls[(int) position.x][(int) position.y];
	}

	public void addWallTileAtPosition(Point position, WallType tile) {
		setWallTile(position, new WallTile(tile));
	}
	
	private void setWallTile(Point position, WallTile tile) {
		walls[(int) position.x][(int) position.y] = tile;
		wallTilesToUpdate.add(position);
	}

	public boolean attackWallTileAtPosition(Point position, int damage) {
		WallTile tile = getWallTileAtPosition(position);
		if (tile.isBreakable()) {
			if (tile.damage(damage)) {
				wallTilesToUpdate.add(position);
				setWallTile(position, null);
			}
			return true;
		}
		return false;
	}

	public boolean attackWallTileAtPosition(int x, int y, int damage, Player source) {
		WallTile tile = walls[x][y];
		if (tile.isBreakable()) {
			if (tile.damage(damage)) {
				for (Map.Entry<ItemType, Integer> itemDrop : tile.getItemDrops().entrySet()) {
					game.doForEachClient(client->client.sendCreateEffect(EffectType.TileDestroyed, x, y, source.getId(),
										itemDrop.getValue(), itemDrop.getKey().ordinal()));
				}
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
					connection.sendInt(walls[x][y].getType().ordinal());
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
				connection.sendInt(walls[(int)p.x][(int)p.y].getType().ordinal());
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
	
	public Point getRandomEnemySpawnPoint() {
		return enemySpawnPoints.get(random.nextInt(enemySpawnPoints.size()));
	}
	
	public float getPathMultiplierAt(Point position) {
		float multiplier = temporaryPathMultipliers[(int) position.x][(int) position.y];
		
		WallTile tile = getWallTileAtPosition(position);
		if (tile != null) {
			multiplier *= tile.getPathMultiplier();
		}
		
		return multiplier;
	}
	
	public void decreasePathMultipliers() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				float multiplier = temporaryPathMultipliers[x][y];
				multiplier = Math.max(1,  (multiplier - 1) * PATH_MULTIPLIER_DECAY);
				temporaryPathMultipliers[x][y] = multiplier;
			}
		}
	}
	
	public void increasePathMultiplier(Point center, int radius, float percentageIncrease) {
		if (percentageIncrease < 0)
			throw new IllegalArgumentException("The increase must be >= 0: " + percentageIncrease); // the array values should never be < 1
		
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				Point position = new Point(center.x + dx, center.y + dy);
				if (isInBounds(position)) {
					float increase = (float) (percentageIncrease / (1 + Math.sqrt(dx*dx + dy*dy)));
					temporaryPathMultipliers[(int) position.x][(int) position.y] *= (1 + increase);
				}
			}
		}
	}
	
	private boolean isInBounds(Point position) {
		return position.x >= 0 && position.y >= 0 && 
				position.x < width && position.y < height;
	}
}
