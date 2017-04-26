package isle.survival.ui;

import util.Point;
import world.Inventory;
import world.ItemType;
import world.Tool;

import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BuildItem {
	public static final int WIDTH  = 64;
	public static final int HEIGHT = 64;
	
	public static Texture whiteTexture; 
	
	private Tool tool;
	private Texture texture;
	private Inventory inventory;
	
	private Point position;
	private boolean isSelected;
	
	public BuildItem(Tool tool, Texture texture, Inventory inventory) {
		this.tool = tool;
		this.texture = texture;
		this.inventory = inventory;
		position = new Point();
	}
	
	public void draw(SpriteBatch spriteBatch) {
		float scale = 1f;
		if (isSelected)
			scale = 1.1f;
		float x = position.x - (WIDTH * (scale - 1)) / 2;
		float y = position.y - (HEIGHT * (scale - 1)) / 2;
		spriteBatch.draw(texture, x, y, WIDTH * scale, HEIGHT * scale);
		if (!tool.hasAllResources(inventory)) {
			spriteBatch.setColor(0.8f, 0.8f, 0.8f, 0.5f);
			spriteBatch.draw(whiteTexture, x, y, WIDTH * scale, HEIGHT * scale);
			spriteBatch.setColor(Color.WHITE);
		}
		
		
	}
	
	public void setPosition(int x, int y) {
		position.set(x, y);
	}
	
	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public Point getPosition() {
		return position;
	}
	
	public Tool getTool() {
		return tool;
	}

	public boolean isVisible() {
		return tool.hasAnyResource(inventory);
	}
}
