package isle.survival.world;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WorldEffects {
	private TextureBase textureBase;
	private SpriteBatch spriteBatch;
	
	private List<Effect> objects;
	
	public WorldEffects(TextureBase textureBase, SpriteBatch spriteBatch) {
		this.textureBase = textureBase;
		this.spriteBatch = spriteBatch;
		objects = new LinkedList<>();
	}
	
	public void addEffect(Effect effect) {
		objects.add(effect);
	}
	
	public void update(float deltaTime) {
		Iterator<Effect> iter = objects.iterator();
		while (iter.hasNext()) {
			Effect effect = iter.next();
			effect.update(deltaTime);
			if (effect.shouldBeRemoved()) {
				iter.remove();
				System.out.println("remove!!!");
			}
		}
	}
	
	public void draw(float xOffset, float yOffset) {
		for (Effect object : objects) {
			object.draw(spriteBatch, textureBase, xOffset, yOffset); //TODO: add offset.
		}
	}
}
