package isle.survival.ui;

import util.Point;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BuildItem {
	public static final int WIDTH  = 64;
	public static final int HEIGHT = 64;
	
	private int itemId;
	private Texture texture;
	
	private Point position;
	private boolean isSelected;
	
	public BuildItem(int itemId, Texture texture) {
		this.itemId = itemId;
		this.texture = texture;
		position = new Point();
	}
	
	public void draw(SpriteBatch spriteBatch) {
		float scale = 1f;
		if (isSelected)
			scale = 1.1f;
		float x = position.x - (WIDTH * (scale - 1)) / 2;
		float y = position.y - (HEIGHT * (scale - 1)) / 2;
		spriteBatch.draw(texture, x, y, WIDTH*scale, HEIGHT*scale);
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
	
	public int getItemId() {
		return itemId;
	}
}
