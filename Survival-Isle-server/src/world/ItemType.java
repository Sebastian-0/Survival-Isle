package world;

public enum ItemType {
	Wood("wood_icon"),
	Stone("stone_icon"), 
	Respawn("respawn_crystal_icon");
	
	private String texture;
	
	ItemType(String texture) {
		this.texture = texture;
	}
	
	public String getTexture() {
		return texture;
	}
}