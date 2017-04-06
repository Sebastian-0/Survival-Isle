package isle.survival.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import server.Connection;
import world.World;

public class ClientWorld extends World {
	private TextureBase textureBase;
	
	public ClientWorld(TextureBase textureBase) {
		this.textureBase = textureBase;
	}

	public void drawTerrain(SpriteBatch spriteBatch, float xOffset, float yOffset) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				spriteBatch.draw(textureBase.getGroundTexture(ground[i][j]), i*TILE_WIDTH - xOffset, j*TILE_HEIGHT - yOffset);
			}
		}
	}
	
	public void receive(Connection connection) {
		width = connection.receiveInt();
		height = connection.receiveInt();
		ground = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				ground[x][y] = connection.receiveInt();
			}
		}
	}
	
}
