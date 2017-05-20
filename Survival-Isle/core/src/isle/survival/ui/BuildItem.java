package isle.survival.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import util.Point;
import world.Inventory;
import world.Tool;

public class BuildItem {
	public static final int WIDTH  = 64;
	public static final int HEIGHT = 64;
	
	public static TextureRegion whiteTexture; 
	
	private Tool tool;
	private TextureRegion texture;
	private Inventory inventory;
	
	private Point position;
	private boolean isSelected;
	
	public BuildItem(Tool tool, TextureRegion texture, Inventory inventory) {
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
	
	public TextureRegion getIcon() {
		return texture;
	}

	public boolean isVisible() {
		return tool.hasAnyResource(inventory);
	}
}
