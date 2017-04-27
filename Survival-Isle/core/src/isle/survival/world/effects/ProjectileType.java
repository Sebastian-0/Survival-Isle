package isle.survival.world.effects;

public enum ProjectileType {
	bullet("bullet", 0.5f);
	
	private String texture; 
	private float speed;
	
	ProjectileType(String texture, float speed) {
		this.texture = texture;
		this.speed = speed;
	}
	
	public String getTexture() {
		return texture;
	}
	
	public float getSpeed() {
		return speed;
	}
}