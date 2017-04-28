package isle.survival.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.world.WorldObjects;

public class HealthBar {
	
	static final float width = 20;
	static final float height = 100;
	static final float playerMaxHp = 100;
	
	float x;
	float y;
	WorldObjects objects;
	
	public HealthBar(WorldObjects objects) {
		this.x = Gdx.graphics.getWidth() - 30;
		this.y = Gdx.graphics.getHeight() - 110;
		this.objects = objects;
	}
	
	public void draw(SpriteBatch spriteBatch) {
		
		//Bar background
		// Frame
		spriteBatch.setColor(0.6f, 0.55f, 0.65f, 1f);
		spriteBatch.draw(BuildItem.whiteTexture, x, y, width, height);
		// background
		spriteBatch.setColor(0.05f, 0.05f, 0.05f, 1f);
		spriteBatch.draw(BuildItem.whiteTexture, x+2, y+2, width-4, height-4);
		if(objects.getPlayer() != null) {
			// bar
			float frac = (float)objects.getPlayer().getHp() / playerMaxHp;
			spriteBatch.setColor(0.8f, 0.1f, 0.1f, 1f);
			spriteBatch.draw(BuildItem.whiteTexture, x+2, y+2, width-4, frac*(height-4));
		}
		spriteBatch.setColor(1f, 1f, 1f, 1f);
	}

}
