package world;

import java.io.Serializable;

public class World implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int TILE_WIDTH = 32;
	public static final int TILE_HEIGHT = 32;
	
	public enum GroundType {
		Water, 
		Grass, 
		Rock,
		Stump,
		ShallowWater,
		Beach,
		Flowers;
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
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
