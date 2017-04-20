package isle.survival.ui;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import isle.survival.client.GameProtocolCoder;
import isle.survival.world.TextureBase;
import world.Inventory;
import world.Tool;

public class BuildMenu {
	private Array<BuildItem> items;
	private BuildItem selectedItem;
	private Texture marker;
	private GameProtocolCoder coder;
	
	public BuildMenu(TextureBase textures, Inventory inventory, GameProtocolCoder coder) {
		this.coder = coder;
		
		BuildItem.whiteTexture = textures.getTexture("white");
		items = new Array<>();
		items.add(new BuildItem(Tool.Pickaxe, textures.getTexture("pickaxe"), inventory));
		items.add(new BuildItem(Tool.WoodWall, textures.getTexture("buildwoodwall"), inventory));
		items.add(new BuildItem(Tool.StoneWall, textures.getTexture("buildstonewall"), inventory));
		items.add(new BuildItem(Tool.Turret, textures.getTexture("buildturret"), inventory));
		
		marker = textures.getTexture("marker");
		
		setSelectedIndex(0);
		positionItems();
	}
	
	public void positionItems() {
		if (!selectedItem.isVisible())
			decrementSelection();
		
		final int offset = 20;
		int i = 0;
		for (BuildItem item : items) {
			if (item.isVisible()) {
				int x = i++ * (BuildItem.WIDTH + offset);
				item.setPosition(x, 8);
			}
		}
	}
	
	public void incrementSelection() {
		Iterator<BuildItem> it = items.iterator();
		while (it.hasNext() && it.next() != selectedItem)
			;
		
		while (it.hasNext()) {
			BuildItem item = it.next();
			if (item.isVisible()) {
				setSelectedItem(item);
				return;
			}
		}
	}
	
	public void decrementSelection() {
		int index = items.indexOf(selectedItem, true);
		
		while (--index >= 0) {
			if (items.get(index).isVisible()) {
				setSelectedItem(items.get(index));
				return;
			}
		}
	}
	
	public void setSelectedIndex(int index) {
		int i = 0;
		for (BuildItem item : items) {
			if (item.isVisible()) {
				if (i++ == index) {
					setSelectedItem(item);
				}
			}
		}
	}

	private void setSelectedItem(BuildItem item) {
		if (selectedItem != null)
			selectedItem.setIsSelected(false);
		selectedItem = item;
		selectedItem.setIsSelected(true);
		coder.sendSelectTool(selectedItem.getTool());
	}
	
	public int getWidth() {
		int index = items.size;
		while (--index >= 0 && !items.get(index).isVisible())
			;
		return (int) (items.get(index).getPosition().x + BuildItem.WIDTH);
	}
	
	public Tool getSelectedTool() {
		return selectedItem.getTool();
	}

	public void draw(SpriteBatch spriteBatch) {
		for (BuildItem buildItem : items) {
			if (buildItem.isVisible())
				buildItem.draw(spriteBatch);
		}
		spriteBatch.draw(
				marker, 
				selectedItem.getPosition().x - 6, 
				selectedItem.getPosition().y - 6, 
				BuildItem.WIDTH + 12, 
				BuildItem.HEIGHT + 12);
	}
}
