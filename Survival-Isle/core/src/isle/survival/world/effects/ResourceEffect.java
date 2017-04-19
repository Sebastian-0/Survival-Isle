package isle.survival.world.effects;

import world.World;
import isle.survival.world.Effect;
import isle.survival.world.NetworkObject;
import isle.survival.world.TextureBase;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ResourceEffect extends Effect {
	private Vector2 position;
	private NetworkObject target;
	private Vector2 speed;
	private int textureType;
	
	public ResourceEffect(int x, int y, NetworkObject target, int textureType) {
		this.position = new Vector2(x, y);
		this.target = target;
		speed = new Vector2(0.15f, 0).setAngle(MathUtils.random() * 360);
		this.textureType = textureType;
	}

	@Override
	public void update(float deltaTime) {
		position.add(speed);
		
		final float accelerationRatio = 0.07f;
		final float retardationRatio = 1 - accelerationRatio - 0.1f;
		
		Vector2 toTarget = new Vector2(target.getX() - position.x, target.getY() - position.y);
		toTarget.nor().scl(accelerationRatio);
		speed.scl(retardationRatio).add(toTarget);
		
		if (position.epsilonEquals(target.getX(), target.getY(), 0.1f)) {
			scheduleForRemoval();
		}
	}

	@Override
	public void draw(SpriteBatch spriteBatch, TextureBase textures,
			float xView, float yView) {
		Texture texture = textures.getWallTileTexture(textureType);
		spriteBatch.draw(
				texture, 
				(position.x+0.5f)*World.TILE_WIDTH - texture.getWidth()/4 - xView, 
				(position.y+0.5f)*World.TILE_WIDTH - texture.getHeight()/4 - yView, 
				texture.getWidth()/2, texture.getHeight()/2);
	}
}
