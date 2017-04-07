package isle.survival.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import util.Point;
import world.World;

public class NetworkObject {
	private static final float MOVEMENT_TIME = 0.5f;
	
	private int id;
	private int textureId;
	
	private Point previousPosition;
	private Point position;
	private float interpolation;
	
	public NetworkObject(int x, int y, int id, int textureId) {
		position = new Point(x, y);
		previousPosition = new Point(x, y);
		this.id = id;
		this.textureId = textureId;
	}
	
	public void draw(SpriteBatch spriteBatch, TextureBase textures, float xView, float yView) {
		float dt = Gdx.graphics.getDeltaTime();
		interpolation = Math.min(1, interpolation + dt/MOVEMENT_TIME);
		Point currentPosition = previousPosition.interpolateTo(position, interpolation);
		spriteBatch.draw(textures.getObjectTexture(textureId), 
				currentPosition.x*World.TILE_WIDTH - xView,
				currentPosition.y*World.TILE_HEIGHT - yView);
	}
	

	public int getId() {
		return id;
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

	public void setPosition(int x, int y) {
		previousPosition.set(previousPosition.interpolateTo(position, interpolation));
		position.set(x, y);
		interpolation = 0;
	}
}
