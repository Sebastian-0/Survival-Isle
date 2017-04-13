package isle.survival.ui;

import util.Point;
import world.Inventory;
import world.ItemType;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class InventoryItem {
	public static final int HEIGHT = 32;
	
	private Texture image;
	private String text;
	private int amount;
	
	private GlyphLayout layout;
	private BitmapFont font;
	
	private Inventory inventory;
	private ItemType type;
	
	private Point position;
	
	public InventoryItem(Texture image, String text, BitmapFont font, Inventory inventory, ItemType type) {
		this.image = image;
		this.text = text;
		this.font = font;
		layout = new GlyphLayout(font, text);
		
		this.inventory = inventory;
		this.type = type;
		
		position = new Point();
	}
	
	public void setPosition(int x, int y) {
		position.set(x, y);
	}
	
	public void draw(SpriteBatch spriteBatch) {
		amount = inventory.getAmount(type);
		
		float x = position.x;
		spriteBatch.draw(image, x, position.y);
		x += image.getWidth();
		float y = position.y + image.getHeight() / 2 + font.getCapHeight()/2;
		font.draw(spriteBatch, text, x, y);
		x += layout.width;
		font.draw(spriteBatch, Integer.toString(amount), x, y);
	}
}
