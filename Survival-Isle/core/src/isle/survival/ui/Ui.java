package isle.survival.ui;

import world.Inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.client.ClientProtocolCoder;
import isle.survival.world.TextureBase;

public class Ui {
	private BuildMenu buildMenu;
	private InventoryMenu inventoryMenu;
	
	public Ui(TextureBase textures, Inventory inventory, ClientProtocolCoder coder) {
		buildMenu = new BuildMenu(textures, inventory, coder);
		inventoryMenu = new InventoryMenu(textures, inventory);
	}

	public void draw(SpriteBatch spriteBatch) {
		buildMenu.positionItems();
		int x = Gdx.graphics.getWidth()/2 - buildMenu.getWidth()/2;
		spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(x, 0, 0));
		buildMenu.draw(spriteBatch);
		spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(-x, 0, 0));
		
		int y = Gdx.graphics.getHeight() - inventoryMenu.getHeight();
		spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(0, y, 0));
		inventoryMenu.draw(spriteBatch);
		spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(0, -y, 0));
	}
	
	public BuildMenu getBuildMenu() {
		return buildMenu;
	}
}
