package isle.survival.world;

import com.badlogic.gdx.graphics.Texture;

public class TextureBase {
	private Texture[] groundTextures;
	private Texture[] objectTextures;
	
	public void setUpTextures() {
		groundTextures = new Texture[0];
		objectTextures = new Texture[0];
	}
	
	public void dispose() {
		for (Texture t : groundTextures) {
			t.dispose();
		}

		for (Texture t : objectTextures) {
			t.dispose();
		}
	}
	
	public Texture getGroundTexture(int tileId) {
		return groundTextures[tileId];
	}
	
	public Texture getObjectTexture(int objectId) {
		return objectTextures[objectId];
	}
}
