package isle.survival.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class TextureBase {
	
	private TextureAtlas textureAtlas;
	
	private ObjectMap<Integer, TextureRegion> groundTileTextures;
	private ObjectMap<Integer, TextureRegion> wallTileTextures;
	private ObjectMap<Integer, TextureRegion> objectTextures;
	
	private ObjectMap<String, TextureRegion> textures;

	private TextureRegion defaultTexture;

	public TextureBase() {
		textureAtlas = new TextureAtlas(Gdx.files.internal("game_textures.atlas"));
		
		groundTileTextures = new ObjectMap<>();
		wallTileTextures = new ObjectMap<>();
		objectTextures = new ObjectMap<>();
		textures = new ObjectMap<>();
		setUpTextures();
	}
	
	public void setUpTextures() {
		defaultTexture = textureAtlas.findRegion("default");
		if (defaultTexture == null)
			defaultTexture = new TextureRegion(new Texture(64, 64, Format.RGBA8888));
		setUpGroundTileTextures();
		setUpWallTileTextures();
		setUpObjectTextures();
	}

	private void setUpGroundTileTextures() {
		groundTileTextures.put(0, textureAtlas.findRegion("seafloor"));
		groundTileTextures.put(1, textureAtlas.findRegion("grass"));
		groundTileTextures.put(2, textureAtlas.findRegion("rock"));
		groundTileTextures.put(3, textureAtlas.findRegion("stump"));
		groundTileTextures.put(4, textureAtlas.findRegion("shallow_water"));
		groundTileTextures.put(5, textureAtlas.findRegion("beach"));
		groundTileTextures.put(6, textureAtlas.findRegion("flowers"));
	}
	
	private void setUpWallTileTextures() {
		wallTileTextures.put(0, textureAtlas.findRegion("water"));
		wallTileTextures.put(1, textureAtlas.findRegion("forest"));
		wallTileTextures.put(2, textureAtlas.findRegion("mountain"));
		wallTileTextures.put(3, textureAtlas.findRegion("woodwall"));
		wallTileTextures.put(4, textureAtlas.findRegion("stonewall"));
		wallTileTextures.put(5, textureAtlas.findRegion("enemy_spawn"));
		wallTileTextures.put(6, textureAtlas.findRegion("turret_base"));
		wallTileTextures.put(7, textureAtlas.findRegion("respawn_crystal"));
	}
	
	private void setUpObjectTextures() {
		objectTextures.put(0, textureAtlas.findRegion("friend_down"));
		objectTextures.put(1, textureAtlas.findRegion("friend_right"));
		objectTextures.put(2, textureAtlas.findRegion("friend_up"));
		objectTextures.put(3, textureAtlas.findRegion("friend_left"));
		
		objectTextures.put(4, textureAtlas.findRegion("player_down"));
		objectTextures.put(5, textureAtlas.findRegion("player_right"));
		objectTextures.put(6, textureAtlas.findRegion("player_up"));
		objectTextures.put(7, textureAtlas.findRegion("player_left"));

		objectTextures.put(8, textureAtlas.findRegion("enemy_down"));
		objectTextures.put(9, textureAtlas.findRegion("enemy_right"));
		objectTextures.put(10, textureAtlas.findRegion("enemy_up"));
		objectTextures.put(11, textureAtlas.findRegion("enemy_left"));
		
		objectTextures.put(12, textureAtlas.findRegion("turret_down"));
		objectTextures.put(13, textureAtlas.findRegion("turret_right"));
		objectTextures.put(14, textureAtlas.findRegion("turret_up"));
		objectTextures.put(15, textureAtlas.findRegion("turret_left"));

		objectTextures.put(16, textureAtlas.findRegion("empty"));
		objectTextures.put(17, textureAtlas.findRegion("empty"));
		objectTextures.put(18, textureAtlas.findRegion("empty"));
		objectTextures.put(19, textureAtlas.findRegion("empty"));
	}

	public void dispose() {
		textureAtlas.dispose();
	}
	
	public TextureRegion getGroundTileTexture(int tileId) {
		return groundTileTextures.get(tileId, defaultTexture);
	}
	
	public TextureRegion getWallTileTexture(int tileId) {
		return wallTileTextures.get(tileId, defaultTexture);
	}

	public TextureRegion getObjectTexture(int objectId) {
		return objectTextures.get(objectId, defaultTexture);
	}
	
	public TextureRegion getTexture(String name) {
		if (textures.containsKey(name)) {
			return textures.get(name);
		}
		
		TextureRegion texture = textureAtlas.findRegion(name);
		if (texture == null) {
			System.out.println("Failed to find the texture: " + name);
			texture = defaultTexture;
		}
		textures.put(name, texture);
		return texture;
	}
}
