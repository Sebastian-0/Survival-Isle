package world;

import java.io.Serializable;

public class WallTile implements Serializable {
	private int id;
	private int health;
	private boolean isBreakable;
	
	public enum TileType {
		Forest(0, true, 2);
		

		public final int id;
		public final int health;
		public final boolean isBreakable;
		
		private TileType(int id, boolean isBreakable, int health) {
			this.id = id;
			this.health = health;
			this.isBreakable = isBreakable;
		}
	}
	
	public WallTile(TileType type) {
		id = type.id;
		isBreakable = type.isBreakable;
		health = type.health;
	}
	
	
	public int getId() {
		return id;
	}
	
	
	public boolean isBreakable() {
		return isBreakable;
	}


	public boolean damage(int amount) {
		health -= amount;
		if (health <= 0) {
			return true;
		}
		return false;
	}
}
