package isle.survival.world.effects;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import isle.survival.world.Effect;
import isle.survival.world.NetworkObject;
import isle.survival.world.ParticleBase;
import isle.survival.world.TextureBase;

public class EnemyDiedEffect extends Effect {
	private Vector2 position;
	private ParticleEffect effect;
	
	public EnemyDiedEffect(NetworkObject enemy, ParticleBase effects) {
		this.position = new Vector2(enemy.getX(), enemy.getY());
		
//		effect = new ParticleEffect();
//		effect.load(effectFile, imagesDir);
	}

	@Override
	public void update(float deltaTime) {
//		position.add(speed);
//		
//		final float accelerationRatio = 0.07f;
//		final float retardationRatio = 1 - accelerationRatio - 0.1f;
//		
//		Vector2 toTarget = new Vector2(target.getX() - position.x, target.getY() - position.y);
//		toTarget.nor().scl(accelerationRatio);
//		speed.scl(retardationRatio).add(toTarget);
//		
//		if (position.epsilonEquals(target.getX(), target.getY(), 0.1f)) {
//			scheduleForRemoval();
//		}
		scheduleForRemoval();
	}

	@Override
	public void draw(SpriteBatch spriteBatch, TextureBase textures,
		float xView, float yView) {
//		spriteBatch.draw(
//				texture, 
//				(position.x+0.5f)*World.TILE_WIDTH - texture.getWidth()/4 - xView, 
//				(position.y+0.5f)*World.TILE_WIDTH - texture.getHeight()/4 - yView, 
//				texture.getWidth()/2, texture.getHeight()/2);
	}
}
