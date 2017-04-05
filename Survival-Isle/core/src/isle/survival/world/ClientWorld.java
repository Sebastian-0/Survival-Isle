package isle.survival.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import world.World;

public class ClientWorld extends World {
	private Texture[] textures;
	
	public ClientWorld(int width, int height) {
		super(width, height);
		setUpTextures();
	}

	private void setUpTextures() {
		textures = new Texture[GroundTile.values().length];
		for (GroundTile t : GroundTile.values()) {
			textures[t.id] = new Texture(t.textureName);
		}
	}

	public void drawTerrain(SpriteBatch spriteBatch) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				spriteBatch.draw(textures[ground[i][j]], i*TILE_WIDTH, j*TILE_HEIGHT);
			}
		}
	}

	public void dispose() {
		for (Texture t : textures) {
			t.dispose();
		}
	}
}
