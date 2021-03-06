package isle.survival.ui;

import world.Inventory;
import world.ItemType;
import isle.survival.world.TextureBase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class InventoryMenu {
	private static final int OFFSET = 5;
	
	private Array<InventoryItem> items;

	private BitmapFont font;
	
	public InventoryMenu(TextureBase textures, Inventory inventory) {
		items = new Array<>();
		
		font = new BitmapFont(Gdx.files.internal("font32.fnt"));
		font.setColor(new Color(235/255f, 240/255f, 240/255f, 1));
		items.add(new InventoryItem(textures.getTexture("wood_icon"), "Wood: ", font, inventory, ItemType.Wood));
		items.add(new InventoryItem(textures.getTexture("stone_icon"), "Stone: ", font, inventory, ItemType.Stone));
		items.add(new InventoryItem(textures.getTexture("respawn_crystal_icon"), "Respawn crystal: ", font, inventory, ItemType.RespawnCrystal));
		
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

	public void dispose() {
		font.dispose();
	}
}
