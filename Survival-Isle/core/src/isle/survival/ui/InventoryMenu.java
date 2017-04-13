package isle.survival.ui;

import world.Inventory;
import world.ItemType;
import isle.survival.world.TextureBase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class InventoryMenu {
	private static final int OFFSET = 5;
	
	private Array<InventoryItem> items;
	
	public InventoryMenu(TextureBase textures, Inventory inventory) {
		items = new Array<>();
		
		BitmapFont font = new BitmapFont(Gdx.files.internal("font32.fnt"));
		items.add(new InventoryItem(textures.getTexture("wood_icon"), "Wood: ", font, inventory, ItemType.wood));
		items.add(new InventoryItem(textures.getTexture("stone_icon"), "Stone: ", font, inventory, ItemType.stone));
		
		positionItems();
	}
	
	private void positionItems() {
		for (int i = 0; i < items.size; i++) {
			int y = i * (InventoryItem.HEIGHT + OFFSET);
			items.get(i).setPosition(8, y);
		}
	}
	
	public int getHeight() {
		return items.size * (InventoryItem.HEIGHT + OFFSET);
	}
	
	public void draw(SpriteBatch spriteBatch) {
		for (InventoryItem item : items) {
			item.draw(spriteBatch);
		}
	}
}
