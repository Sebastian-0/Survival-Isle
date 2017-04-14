package isle.survival.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Effect {
	
	private boolean shouldBeRemoved;
	
	public void scheduleForRemoval() {
		shouldBeRemoved = true;
	}
	
	public boolean shouldBeRemoved() {
		return shouldBeRemoved;
	}
	
	public abstract void update(float deltaTime);
	public abstract void draw(SpriteBatch spriteBatch, TextureBase textures, float xView, float yView);
}
