package isle.survival.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import world.World;

public class ClientWorld extends World {
	private TextureBase textureBase;
	
	public ClientWorld(int width, int height, TextureBase textureBase) {
		super(width, height);
		this.textureBase = textureBase;
	}

	public void drawTerrain(SpriteBatch spriteBatch, float xOffset, float yOffset) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				spriteBatch.draw(textureBase.getGroundTexture(ground[i][j]), i*TILE_WIDTH - xOffset, j*TILE_HEIGHT - yOffset);
			}
		}
	}
	
}
