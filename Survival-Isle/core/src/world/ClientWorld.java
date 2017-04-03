package world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import world.World;

public class ClientWorld extends World {
	private SpriteBatch batch;
	private Texture[] textures;
	
	public ClientWorld(int width, int height) {
		super(width, height);
		setUpTextures();
	}

	private void setUpTextures() {
		batch = new SpriteBatch();
		textures[0] = new Texture("water.png");
		textures[1] = new Texture("grass.png");
	}

	public void drawTerrain() {
		batch.begin();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				batch.draw(textures[ground[i][j]], i*TILE_WIDTH, j*TILE_HEIGHT);
			}
		}
		batch.end();
	}
}
