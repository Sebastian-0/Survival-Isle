package isle.survival.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;

public class TextureBase {
	private ObjectMap<Integer, Texture> groundTileTextures;
	private ObjectMap<Integer, Texture> wallTileTextures;
	private ObjectMap<Integer, Texture> objectTextures;
	
	private Texture defaultTexture;

	public TextureBase() {
		setUpTextures();
	}
	
	public void setUpTextures() {
		defaultTexture = new Texture("default.png");
		setUpGroundTileTextures();
		setUpWallTileTextures();
		setUpObjectTextures();
	}

	private void setUpGroundTileTextures() {
		groundTileTextures = new ObjectMap<>();
		groundTileTextures.put(0, new Texture("water.png"));
		groundTileTextures.put(1, new Texture("grass.png"));
	}
	
	private void setUpWallTileTextures() {
		wallTileTextures = new ObjectMap<>();
		wallTileTextures.put(0, new Texture("forest.png"));
	}
	
	private void setUpObjectTextures() {
		objectTextures = new ObjectMap<>();
		objectTextures.put(0, new Texture("player.png"));
	}

	public void dispose() {
		defaultTexture.dispose();
		
		Entries<Integer, Texture> i = groundTileTextures.iterator();
		while (i.hasNext) {
			i.next().value.dispose();
		}

		i = wallTileTextures.iterator();
		while (i.hasNext) {
			i.next().value.dispose();
		}
		
		i = objectTextures.iterator();
		while (i.hasNext) {
			i.next().value.dispose();
		}
	}
	
	public Texture getGroundTileTexture(int tileId) {
		return groundTileTextures.get(tileId, defaultTexture);
	}
	
	public Texture getWallTileTexture(int tileId) {
		return wallTileTextures.get(tileId, defaultTexture);
	}

	public Texture getObjectTexture(int objectId) {
		return objectTextures.get(objectId, defaultTexture);
	}
}
