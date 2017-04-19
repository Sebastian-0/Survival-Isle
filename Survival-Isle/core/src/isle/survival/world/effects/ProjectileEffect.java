package isle.survival.world.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import isle.survival.world.Effect;
import isle.survival.world.NetworkObject;
import isle.survival.world.TextureBase;
import world.World;

import java.lang.Math.*;

public class ProjectileEffect extends Effect {
	
	private Vector2 position;
	private NetworkObject target;
	private Vector2 velocity;
	private Texture texture;
	private float angle;
	
	public ProjectileEffect(NetworkObject origin, NetworkObject target, float speed, Texture texture) {
		this.position = new Vector2(origin.getX(), origin.getY());
		this.target = target;
		this.angle = (float) Math.atan2(target.getX()-origin.getX(), target.getServerY()-origin.getY());
		this.velocity = new Vector2(speed, 0).setAngle(angle);
		this.texture = texture;
	}

	@Override
	public void update(float deltaTime) {
		position.add(velocity);
		
		if (position.epsilonEquals(target.getX(), target.getY(), 0.1f)) {
			scheduleForRemoval();
		}
	}

	@Override
	public void draw(SpriteBatch spriteBatch, TextureBase textures, float xView, float yView) {
		spriteBatch.draw(
				texture, 
				(position.x+0.5f)*World.TILE_WIDTH - texture.getWidth()/4 - xView, 
				(position.y+0.5f)*World.TILE_WIDTH - texture.getHeight()/4 - yView, 
				(float)texture.getWidth()/2, (float)texture.getHeight()/2);
	}
}
