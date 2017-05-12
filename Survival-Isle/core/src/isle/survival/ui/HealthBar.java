package isle.survival.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.world.WorldObjects;
import world.Player;

public class HealthBar {
	
	static final float width = 20;
	static final float height = 100;
	static final float playerMaxHp = Player.MAX_HEALTH;
	
	private WorldObjects objects;
	private float x, y;
	private float scale = 1;
	private float health = 1;
	
	public HealthBar(WorldObjects objects) {
		this.objects = objects;
	}
	
	public void draw(SpriteBatch spriteBatch) {
		x = Gdx.graphics.getWidth() - 30;
		y = Gdx.graphics.getHeight() - 110;
		
		float oldHealth = health;
		
		//Bar background
		// Frame
		spriteBatch.setColor(0.6f, 0.55f, 0.65f, 1f);
		spriteBatch.draw(BuildItem.whiteTexture,
				x - (scale - 1) * width,
				y - (scale - 1) * height,
				width * scale,
				height * scale);
		// background
		spriteBatch.setColor(0.05f, 0.05f, 0.05f, 1f);
		spriteBatch.draw(BuildItem.whiteTexture,
				x+2 - (scale - 1) * (width - 4),
				y+2 - (scale - 1) * (height - 4),
				(width-4) * scale,
				(height-4) * scale);
		if(objects.getPlayer() != null) {
			// bar
			health = (float)objects.getPlayer().getHp() / playerMaxHp;
			spriteBatch.setColor(0.8f, 0.1f, 0.1f, 1f);
			spriteBatch.draw(BuildItem.whiteTexture,
					(x+2) - (scale - 1) * (width - 4),
					(y+2) - (scale - 1) * (health*(height-4)),
					(width-4) * scale,
					health*(height-4)*scale);
		}
		spriteBatch.setColor(1f, 1f, 1f, 1f);

		scale = Math.max(1f, scale - Gdx.graphics.getDeltaTime() * 1f);
		
		if (health > oldHealth) {
			scale = 1.4f;
		}
	}

}
