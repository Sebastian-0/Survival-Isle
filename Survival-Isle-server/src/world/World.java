package world;

public class World {
	public static final int TILE_WIDTH = 32;
	public static final int TILE_HEIGHT = 32;
	
	public enum GroundTile {
		WATER(0),
		GRASS(1);
		
		
		public final int id;
		
		private GroundTile(int id) {
			this.id = id;
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
}
