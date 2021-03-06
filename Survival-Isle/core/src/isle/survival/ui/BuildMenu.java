package isle.survival.ui;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import isle.survival.client.GameProtocolCoder;
import isle.survival.world.TextureBase;
import world.Inventory;
import world.ItemType;
import world.Tool;

public class BuildMenu {
	private Array<BuildItem> items;
	private BuildItem selectedItem;
	private TextureRegion marker;
	private GameProtocolCoder coder;
	private TextureBase textureBase;
	private BitmapFont font;
	private Inventory inventory;
	
	public BuildMenu(TextureBase textures, Inventory inventory, GameProtocolCoder coder) {
		this.coder = coder;
		
		BuildItem.whiteTexture = textures.getTexture("white");
		items = new Array<>();
		TextureRegion pickaxe = textures.getTexture("pickaxe");
//		pickaxe.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		items.add(new BuildItem(Tool.Pickaxe, pickaxe, inventory));
		items.add(new BuildItem(Tool.WoodWall, textures.getTexture("buildwoodwall"), inventory));
		items.add(new BuildItem(Tool.StoneWall, textures.getTexture("buildstonewall"), inventory));
		items.add(new BuildItem(Tool.Turret, textures.getTexture("buildturret"), inventory));
		items.add(new BuildItem(Tool.RespawnCrystal, textures.getTexture("buildcrystal"), inventory));
		this.inventory = inventory;
		
		marker = textures.getTexture("marker");
		textureBase = textures;
	
		BitmapFont font = new BitmapFont(Gdx.files.internal("font18.fnt"));
		this.font = font;
		
		setSelectedIndex(0);
		positionItems();
	}
	
	public void dispose(){
		font.dispose();
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
	
	public TextureRegion getSelectedToolIcon() {
		return selectedItem.getIcon();
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
		
		Map<ItemType, Integer> costs = selectedItem.getTool().getResourceCost();
		if(costs != null) {
			Set<Map.Entry<ItemType, Integer>> entries = costs.entrySet();
			float scale = 0.5f;		//Scaling to get icons to 16px
			float iconSize = 16; 	//Pixels of icons
			float padding = 2;		//Padding in between icons. iconSize - fontSize
			
			spriteBatch.setColor(0.25f, 0.25f, 0.25f, 0.5f);
			spriteBatch.draw(BuildItem.whiteTexture,
							selectedItem.getPosition().x,
							selectedItem.getPosition().y + 70,
							BuildItem.WIDTH,
							2 + entries.size()*(iconSize + padding));
			
			float offset = 0;
			for(Map.Entry<ItemType, Integer> resource : entries) {
				
				float x = selectedItem.getPosition().x - 6;
				offset += (iconSize + 2);
				float y = selectedItem.getPosition().y + 46 + offset;
				TextureRegion image = textureBase.getTexture(resource.getKey().getTexture());
				spriteBatch.setColor(1.0f, 1.0f, 1.0f, 0.5f);
				spriteBatch.draw(
						image, 
						x - (scale - 1) * image.getRegionWidth()/2, 
						y - (scale - 1) * image.getRegionHeight()/2,
						image.getRegionWidth() * scale, 
						image.getRegionHeight() * scale);
				spriteBatch.setColor(Color.WHITE);
				
				if(inventory.getAmount(resource.getKey()) < resource.getValue())
					font.setColor(0.8f, 0.2f, 0.2f, 0.8f);
				else
					font.setColor(1.0f, 1.0f, 1.0f, 0.8f);
				
				String text = inventory.getAmount(resource.getKey()) + "/" + resource.getValue().toString();
				
				font.draw(spriteBatch, text, x + 26, y + iconSize + padding*2);
			}
		}
	}
}
