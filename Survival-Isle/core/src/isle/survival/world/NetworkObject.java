package isle.survival.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NetworkObject {
	private int x;
	private int y;
	private int netId;
	private int textureId;
	
	public NetworkObject(int x, int y, int netId, int textureId) {
		this.x = x;
		this.y = y;
		this.netId = netId;
		this.textureId = textureId;
	}
	
	public void draw(SpriteBatch spriteBatch, TextureBase textures, int xOffset, int yOffset) {
		spriteBatch.draw(textures.getObjectTexture(textureId),x, y);
	}
	
}
