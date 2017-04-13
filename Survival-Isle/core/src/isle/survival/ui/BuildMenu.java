package isle.survival.ui;

import isle.survival.world.TextureBase;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BuildMenu {
	private List<BuildItem> items;
	private int selectedItem;
	private Texture marker;
	
	
	public BuildMenu(TextureBase textures) {
		items = new ArrayList<>();
		items.add(new BuildItem(0, textures.getTexture("pickaxe")));
		items.add(new BuildItem(1, textures.getTexture("buildwoodwall")));
		items.add(new BuildItem(2, textures.getTexture("buildstonewall")));
//		items.add(new BuildItem(3, textures.getTexture("item_4")));
//		items.add(new BuildItem(4, textures.getTexture("item_5")));
		
		marker = textures.getTexture("marker");
		
		positionItems();
		setSelectedIndex(0);
	}
	
	private void positionItems() {
		final int offset = 20;
		for (int i = 0; i < items.size(); i++) {
			int x = i * (BuildItem.WIDTH + offset);
			items.get(i).setPosition(x, 8);
		}
	}
	
	
	public void incrementSelection() {
		setSelectedIndex(selectedItem + 1);
	}
	
	public void decrementSelection() {
		setSelectedIndex(selectedItem - 1);
	}
	
	public void setSelectedIndex(int index) {
		if (index >= 0 && index < items.size()) {
			items.get(selectedItem).setIsSelected(false);
			selectedItem = index;
			items.get(selectedItem).setIsSelected(true);
		}
	}
	
	
	public int getWidth() {
		return (int) (items.get(items.size()-1).getPosition().x + BuildItem.WIDTH);
	}
	
	public int getSelectedItemId() {
		return items.get(selectedItem).getItemId();
	}
	

	public void draw(SpriteBatch spriteBatch) {
		for (BuildItem buildItem : items) {
			buildItem.draw(spriteBatch);
		}
		BuildItem selected = items.get(selectedItem);
		spriteBatch.draw(
				marker, 
				selected.getPosition().x - 6, 
				selected.getPosition().y - 6, 
				BuildItem.WIDTH + 12, 
				BuildItem.HEIGHT + 12);
	}
}
