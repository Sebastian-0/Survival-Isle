package isle.survival.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;

public class TextureBase {
	private ObjectMap<Integer, Texture> groundTileTextures;
	private ObjectMap<Integer, Texture> wallTileTextures;
	private ObjectMap<Integer, Texture> objectTextures;
	
	private ObjectMap<String, Texture> textures;
	
	private Texture defaultTexture;

	public TextureBase() {
		groundTileTextures = new ObjectMap<>();
		wallTileTextures = new ObjectMap<>();
		objectTextures = new ObjectMap<>();
		textures = new ObjectMap<>();
		setUpTextures();
	}
	
	public void setUpTextures() {
		defaultTexture = new Texture("default.png");
		setUpGroundTileTextures();
		setUpWallTileTextures();
		setUpObjectTextures();
	}

	private void setUpGroundTileTextures() {
		groundTileTextures.put(0, new Texture("water.png"));
		groundTileTextures.put(1, new Texture("grass.png"));
		groundTileTextures.put(2, new Texture("rock.png"));
	}
	
	private void setUpWallTileTextures() {
		wallTileTextures.put(0, new Texture("forest.png"));
		wallTileTextures.put(1, new Texture("mountain.png"));
		wallTileTextures.put(2, new Texture("woodwall.png"));
		wallTileTextures.put(3, new Texture("stonewall.png"));
	}
	
	private void setUpObjectTextures() {
		objectTextures.put(0, new Texture("player.png"));
	}

	public void dispose() {
		defaultTexture.dispose();

		groundTileTextures.forEach((t) -> {t.value.dispose();}); 
		wallTileTextures.forEach((t) -> {t.value.dispose();}); 
		objectTextures.forEach((t) -> {t.value.dispose();}); 
		textures.forEach((t) -> {t.value.dispose();}); 
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
	
	public Texture getTexture(String name) {
		if (textures.containsKey(name)) {
			return textures.get(name);
		}
		
		Texture texture = new Texture(name + ".png");
		textures.put(name, texture);
		return texture;
	}
}
