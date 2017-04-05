package isle.survival.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NetworkObject {
	private int x;
	private int y;
	private int netId;
	private int objectId;
	
	public NetworkObject(int x, int y, int netId, int objectId) {
		this.x = x;
		this.y = y;
		this.netId = netId;
		this.objectId = objectId;
	}
	
	public void draw(SpriteBatch spriteBatch, TextureBase textures, int xOffset, int yOffset) {
		spriteBatch.draw(textures.getObjectTexture(objectId),x, y);
	}
	
}
