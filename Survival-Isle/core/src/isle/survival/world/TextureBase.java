package isle.survival.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
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
		groundTileTextures.put(0, new Texture("seafloor.png"));
		groundTileTextures.put(1, new Texture("grass.png"));
		groundTileTextures.put(2, new Texture("rock.png"));
		groundTileTextures.put(3, new Texture("stump.png"));
		groundTileTextures.put(4, new Texture("shallow_water.png"));
		groundTileTextures.put(5, new Texture("beach.png"));
		groundTileTextures.put(6, new Texture("flowers.png"));
	}
	
	private void setUpWallTileTextures() {
		wallTileTextures.put(0, new Texture("water.png"));
		wallTileTextures.put(1, new Texture("forest.png"));
		wallTileTextures.put(2, new Texture("mountain.png"));
		wallTileTextures.put(3, new Texture("woodwall.png"));
		wallTileTextures.put(4, new Texture("stonewall.png"));
		wallTileTextures.put(5, new Texture("enemy_spawn.png"));
		wallTileTextures.put(6, new Texture("turret_base.png"));
	}
	
	private void setUpObjectTextures() {
		objectTextures.put(0, new Texture("player_down.png"));
		objectTextures.put(1, new Texture("player_right.png"));
		objectTextures.put(2, new Texture("player_up.png"));
		objectTextures.put(3, new Texture("player_left.png"));

		objectTextures.put(4, new Texture("enemy_down.png"));
		objectTextures.put(5, new Texture("enemy_right.png"));
		objectTextures.put(6, new Texture("enemy_up.png"));
		objectTextures.put(7, new Texture("enemy_left.png"));
		
		objectTextures.put(8, new Texture("turret_down.png"));
		objectTextures.put(9, new Texture("turret_right.png"));
		objectTextures.put(10, new Texture("turret_up.png"));
		objectTextures.put(11, new Texture("turret_left.png"));
		
		objectTextures.put(12, new Texture("bullet.png"));
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
		
		try {
			Texture texture = new Texture(name + ".png");
			textures.put(name, texture);
			return texture;
		} catch (GdxRuntimeException e) {
			return defaultTexture;
		}
	}
}
