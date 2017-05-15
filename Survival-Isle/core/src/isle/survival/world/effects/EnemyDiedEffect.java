package isle.survival.world.effects;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import isle.survival.world.Effect;
import isle.survival.world.NetworkObject;
import isle.survival.world.ParticleBase;
import isle.survival.world.TextureBase;
import world.World;

public class EnemyDiedEffect extends Effect {
	private Vector2 position;
	private ParticleEffect effect;
	
	public EnemyDiedEffect(NetworkObject enemy, ParticleBase effects) {
		this.position = new Vector2(enemy.getX(), enemy.getY());
		
		effect = effects.getEffect("enemy_death");
		effect.start();
	}

	@Override
	public void update(float deltaTime) {
		effect.update(deltaTime);
		if (effect.isComplete()) {
			scheduleForRemoval();
		}
	}

	@Override
	public void draw(SpriteBatch spriteBatch, TextureBase textures,
		float xView, float yView) {
		effect.setPosition(
				(position.x+0.5f)*World.TILE_WIDTH - xView, 
				(position.y+0.5f)*World.TILE_WIDTH - yView);
		effect.draw(spriteBatch);
	}
}
