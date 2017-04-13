package isle.survival.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import util.Point;
import world.World;
import world.Player.AnimationState;

public class NetworkObject {
	public static final float MOVEMENT_TIME = 0.3f;
	
	private int id;
	private int textureId;
	
	private Point previousPosition;
	private Point targetPosition;
	private Point currentPosition;
	private Point attackTarget;
	private float movementInterpolation;
	private float attackInterpolation;
	private AnimationState animation;

	
	public NetworkObject(int x, int y, int id, int textureId) {
		targetPosition = new Point(x, y);
		previousPosition = new Point(x, y);
		currentPosition = new Point(x, y);
		attackTarget = new Point(0, 0);
		this.id = id;
		this.textureId = textureId;
	}
	
	public void update(float deltaTime) {
		movementInterpolation = Math.min(1, movementInterpolation + deltaTime/MOVEMENT_TIME);
		currentPosition = previousPosition.interpolateTo(targetPosition, movementInterpolation);
		
		//if ()
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
		previousPosition.set(previousPosition.interpolateTo(targetPosition, movementInterpolation));
		targetPosition.set(x, y);
		movementInterpolation = 0;
	}

	public void setAttackTarget(int x, int y) {
		attackTarget.set(x, y);
	}

	public void setAnimation(int animation) {
		if (animation == AnimationState.Attacking.id)
			this.animation = AnimationState.Attacking;
		else
			this.animation = AnimationState.Idle;
	}
}
