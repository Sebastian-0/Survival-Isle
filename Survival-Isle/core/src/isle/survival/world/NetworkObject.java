package isle.survival.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import util.Point;
import world.Player.AnimationState;
import world.World;

public class NetworkObject {
	public static final float MOVEMENT_TIME = 0.3f;
	public static final float ATTACK_TIME = 0.15f;
	
	private int id;
	private int textureId;
	
	private Point previousPosition;
	private Point targetPosition;
	private Point currentPosition;
	private Point drawnPosition;
	private Point attackTarget;
	private float movementInterpolation;
	private float attackInterpolation;
	private AnimationState animation;
	private int facingDirection;

	
	public NetworkObject(int x, int y, int id, int textureId) {
		targetPosition = new Point(x, y);
		previousPosition = new Point(x, y);
		currentPosition = new Point(x, y);
		drawnPosition = new Point(x, y);
		attackTarget = new Point(0, 0);
		this.id = id;
		this.textureId = textureId;
	}
	
	public void update(float deltaTime) {
		movementInterpolation = Math.min(1, movementInterpolation + deltaTime/MOVEMENT_TIME);
		currentPosition = previousPosition.interpolateTo(targetPosition, movementInterpolation);
		drawnPosition = currentPosition;
		
		float dx = targetPosition.x - previousPosition.x;
		float dy = targetPosition.y - previousPosition.y;
		
		if (animation == AnimationState.Attacking) {
			attackInterpolation = Math.min(1, attackInterpolation + deltaTime/ATTACK_TIME);
			drawnPosition = drawnPosition.interpolateTo(
					attackTarget, -MathUtils.sin(attackInterpolation*MathUtils.PI2)/2);
			if (attackInterpolation == 1)
				animation = AnimationState.Idle;
			
			dx = attackTarget.x - previousPosition.x;
			dy = attackTarget.y - previousPosition.y;
		}
		
		if (Math.abs(dx) >= 0.00001 || Math.abs(dy) >= 0.00001) {
			facingDirection = Math.max(0,MathUtils.floor(MathUtils.atan2(dy, dx) * 2 / MathUtils.PI + 0.5f) + 1);
		}
	}
	
	public void draw(SpriteBatch spriteBatch, TextureBase textures, float xView, float yView) {
		spriteBatch.draw(textures.getObjectTexture(textureId + facingDirection), 
				drawnPosition.x*World.TILE_WIDTH - xView,
				drawnPosition.y*World.TILE_HEIGHT - yView);
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
		attackInterpolation = 0;
	}

	public void setAnimation(int animation) {
		if (animation == AnimationState.Attacking.id)
			this.animation = AnimationState.Attacking;
		else
			this.animation = AnimationState.Idle;
	}
}
