package isle.survival.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import world.World;

public class NetworkObject {
	private int id;
	private float x;
	private float y;
	private int textureId;
	
	public NetworkObject(int x, int y, int id, int textureId) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.textureId = textureId;
	}
	
	public void draw(SpriteBatch spriteBatch, TextureBase textures, float xView, float yView) {
		spriteBatch.draw(textures.getObjectTexture(textureId),x*World.TILE_WIDTH - xView, y*World.TILE_HEIGHT - yView);
	}
	

	public int getId() {
		return id;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
}
