package isle.survival.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import world.World;

public class NetworkObject {
	private float x;
	private float y;
	private int netId;
	private int textureId;
	
	public NetworkObject(int x, int y, int netId, int textureId) {
		this.x = x;
		this.y = y;
		this.netId = netId;
		this.textureId = textureId;
	}
	
	public void draw(SpriteBatch spriteBatch, TextureBase textures, int xOffset, int yOffset) {
		spriteBatch.draw(textures.getObjectTexture(textureId),x*World.TILE_WIDTH - xOffset, y*World.TILE_HEIGHT - yOffset);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
}
