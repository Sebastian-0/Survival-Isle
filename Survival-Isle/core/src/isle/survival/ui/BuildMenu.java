package isle.survival.ui;

import isle.survival.world.TextureBase;
import world.Inventory;
import world.Tool;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BuildMenu {
	private List<BuildItem> items;
	private int selectedItem;
	private Texture marker;
	
	public BuildMenu(TextureBase textures, Inventory inventory) {
		BuildItem.whiteTexture = textures.getTexture("white");
		items = new ArrayList<>();
		items.add(new BuildItem(Tool.Pickaxe, textures.getTexture("pickaxe"), inventory));
		items.add(new BuildItem(Tool.WoodWall, textures.getTexture("buildwoodwall"), inventory));
		items.add(new BuildItem(Tool.StoneWall, textures.getTexture("buildstonewall"), inventory));
		
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
	
	public Tool getSelectedTool() {
		return items.get(selectedItem).getTool();
	}
	

	public void draw(SpriteBatch spriteBatch) {
		for (BuildItem buildItem : items) {
			if (buildItem.isVisible())
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
