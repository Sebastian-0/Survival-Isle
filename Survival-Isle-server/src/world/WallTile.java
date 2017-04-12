package world;

public class WallTile {
	private int id;
	private boolean isBreakable;
	
	public enum TileType {
		Forest(0, true);
		

		public final int id;
		public final boolean isBreakable;
		
		private TileType(int id, boolean isBreakable) {
			this.id = id;
			this.isBreakable = isBreakable;
		}
	}
	
	public WallTile(TileType type) {
		id = type.id;
		isBreakable = type.isBreakable;
	}
	
	
	public int getId() {
		return id;
	}
	
	
	public boolean isBreakable() {
		return isBreakable;
	}
}
