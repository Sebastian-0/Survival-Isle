package isle.survival.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import util.Point;
import world.Inventory;
import world.ItemType;

public class InventoryItem {
	public static final int HEIGHT = 32;
	public static final float ANIMATION_TIME = 1f;
	private static final int PADDING = 8;
	
	private TextureRegion image;
	private String text;
	
	private Point position;
	private float scale;
	
	private GlyphLayout layout;
	private BitmapFont font;
	
	private Inventory inventory;
	private ItemType type;
	private int amount;
	
	public InventoryItem(TextureRegion image, String text, BitmapFont font, Inventory inventory, ItemType type) {
		this.image = image;
		this.text = text;
		this.font = font;
		layout = new GlyphLayout(font, text);
		
		this.inventory = inventory;
		this.type = type;
		
		position = new Point();
		scale = 1;
		
//		image.setFilter(TextureFilter.Nearest, TextureFilter.Linear);
	}
	
	public void setPosition(int x, int y) {
		position.set(x, y);
	}
	
	public void draw(SpriteBatch spriteBatch) {
		int oldAmount = amount;
		amount = inventory.getAmount(type);
		if (amount > oldAmount) {
			scale = 1.4f;
		}
		
		float x = position.x;
		spriteBatch.draw(
				image, 
				x - (scale - 1) * image.getRegionWidth()/2, 
				position.y - (scale - 1) * image.getRegionHeight()/2,
				image.getRegionWidth() * scale, 
				image.getRegionHeight() * scale);
		
		x += image.getRegionWidth() + PADDING;
		float y = position.y + image.getRegionHeight() / 2 + font.getCapHeight()/2;
		font.draw(spriteBatch, text, x, y);
		x += layout.width;
		font.draw(spriteBatch, Integer.toString(amount), x, y);
		
		scale = Math.max(1f, scale - Gdx.graphics.getDeltaTime() * 1f);
	}
}
