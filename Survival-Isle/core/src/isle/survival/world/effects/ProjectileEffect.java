package isle.survival.world.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import isle.survival.world.Effect;
import isle.survival.world.NetworkObject;
import isle.survival.world.TextureBase;
import world.World;

public class ProjectileEffect extends Effect {
	
	private Vector2 position;
	private Vector2 velocity;
	private String textureName;
	private float angle;
	private float speed;
	private float distanceLeft;
	private TextureBase textureBase;
	
	public ProjectileEffect(NetworkObject origin, NetworkObject target, ProjectileType type, TextureBase textureBase) {
		this.position = new Vector2(origin.getX(), origin.getY());
		this.angle = (float) Math.atan2(target.getX()-origin.getX(), target.getY()-origin.getY()) - (float) Math.PI/2;
		this.distanceLeft = (float) Math.hypot(target.getX()-origin.getX(), target.getY()-origin.getY());
		this.angle = -this.angle;
		this.speed = type.getSpeed();
		this.velocity = new Vector2(type.getSpeed(), 0).setAngleRad(angle);
		this.textureName = type.getTexture();
		this.textureBase = textureBase;
	}

	@Override
	public void update(float deltaTime) {
		position.add(velocity);
		distanceLeft -= speed;
		
		if (distanceLeft < 0) {
			scheduleForRemoval();
		}
	}

	@Override
	public void draw(SpriteBatch spriteBatch, TextureBase textures, float xView, float yView) {
		Texture texture = textureBase.getTexture(textureName);
		spriteBatch.draw(
				texture, 
				(position.x+0.5f)*World.TILE_WIDTH - texture.getWidth()/4 - xView, 
				(position.y+0.5f)*World.TILE_WIDTH - texture.getHeight()/4 - yView, 
				(float)texture.getWidth(), (float)texture.getHeight());
	}
}
