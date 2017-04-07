package world;

public class WallTile {
	private int id;
	
	public enum TileType {
		Forest(0);
		
		
		public final int id;
		
		private TileType(int id) {
			this.id = id;
		}
	}
	
	public WallTile(int id) {
		this.id = id;
	}
	
	
	public int getId() {
		return id;
	}
}
