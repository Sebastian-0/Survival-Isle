package isle.survival.world;

import com.badlogic.gdx.graphics.Texture;

import world.World.GroundTile;

public class TextureBase {
	private Texture[] groundTextures;
	private Texture[] objectTextures;
	
	public TextureBase() {
		setUpTextures();
	}
	
	public void setUpTextures() {
		setUpGroundTileTextures();
		objectTextures = new Texture[0];
	}
	
	private void setUpGroundTileTextures() {
		groundTextures = new Texture[GroundTile.values().length];
		for (GroundTile t : GroundTile.values()) {
			groundTextures[t.id] = new Texture(t.textureName);
		}
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
