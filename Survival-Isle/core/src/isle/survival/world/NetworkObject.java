package isle.survival.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import util.Point;
import world.World;

public class NetworkObject {
	public static final float MOVEMENT_TIME = 0.3f;
	
	private int id;
	private int textureId;
	
	private Point previousPosition;
	private Point targetPosition;
	private Point currentPosition;
	private float interpolation;
	
	public NetworkObject(int x, int y, int id, int textureId) {
		targetPosition = new Point(x, y);
		previousPosition = new Point(x, y);
		currentPosition = new Point(x, y);
		this.id = id;
		this.textureId = textureId;
	}
	
	public void update(float deltaTime) {
		interpolation = Math.min(1, interpolation + deltaTime/MOVEMENT_TIME);
		currentPosition = previousPosition.interpolateTo(targetPosition, interpolation);
	}
	
	public void draw(SpriteBatch spriteBatch, TextureBase textures, float xView, float yView) {
		spriteBatch.draw(textures.getObjectTexture(textureId), 
				currentPosition.x*World.TILE_WIDTH - xView,
				currentPosition.y*World.TILE_HEIGHT - yView);
	}
	

	public int getId() {
		return id;
	}

	public float getX() {
		return currentPosition.x;
	}

	public float getY() {
		return currentPosition.y;
	}

	public void setPosition(int x, int y) {
		previousPosition.set(previousPosition.interpolateTo(targetPosition, interpolation));
		targetPosition.set(x, y);
		interpolation = 0;
	}
}
