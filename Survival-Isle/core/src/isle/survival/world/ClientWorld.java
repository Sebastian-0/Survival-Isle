package isle.survival.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import server.Connection;
import world.World;

public class ClientWorld extends World {
	private TextureBase textureBase;
	private int[][] walls;
	
	public ClientWorld(TextureBase textureBase) {
		this.textureBase = textureBase;
		ground = new int[0][0];
		walls = new int[0][0];
	}

	public void drawTerrain(SpriteBatch spriteBatch, float xOffset, float yOffset) {
		int startX = (int) (Math.max(xOffset / TILE_WIDTH, 0));
		int startY = (int) (Math.max(yOffset / TILE_HEIGHT, 0));
		int endX = (int) (Math.min((xOffset + Gdx.graphics.getWidth()) / TILE_WIDTH, width));
		int endY = (int) (Math.min((yOffset + Gdx.graphics.getWidth()) / TILE_HEIGHT, height));
		
		for (int i = startX; i < endX; i++) {
			for (int j = startY; j < endY; j++) {
				spriteBatch.draw(textureBase.getGroundTileTexture(ground[i][j]), i*TILE_WIDTH - xOffset, j*TILE_HEIGHT - yOffset);
			}
		}
		
		for (int i = startX; i < endX; i++) {
			for (int j = startY; j < endY; j++) {
				if (walls[i][j] != -1)
					spriteBatch.draw(textureBase.getWallTileTexture(walls[i][j]), i*TILE_WIDTH - xOffset, j*TILE_HEIGHT - yOffset);
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

		walls = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				walls[x][y] = connection.receiveInt();
			}
		}
	}
	
}
