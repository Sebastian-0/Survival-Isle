package world;

import java.io.Serializable;

@SuppressWarnings("serial")
public class World implements Serializable {
	public static final int TILE_WIDTH = 32;
	public static final int TILE_HEIGHT = 32;
	
	public enum GroundTile {
		Water(0), 
		Beach(0), 
		Grass(1), 
		Rock(2),
		Stump(3),
		ShallowWater(4);
		
		
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
