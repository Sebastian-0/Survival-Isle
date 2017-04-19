package isle.survival.world.effects;

public enum ProjectileType {
	bullet("bullet",10);
	
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