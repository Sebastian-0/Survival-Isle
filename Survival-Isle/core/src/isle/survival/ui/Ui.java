package isle.survival.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.client.GameProtocolCoder;
import isle.survival.world.TextureBase;
import world.Inventory;

public class Ui {
	private BuildMenu buildMenu;
	private InventoryMenu inventoryMenu;
	private ChatBox chatBox;
	
	public Ui(TextureBase textures, Inventory inventory, GameProtocolCoder coder) {
		buildMenu = new BuildMenu(textures, inventory, coder);
		inventoryMenu = new InventoryMenu(textures, inventory);
		chatBox = new ChatBox();
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
		
		if (chatBox.isEnabled())
			chatBox.draw(spriteBatch);
	}
	
	public BuildMenu getBuildMenu() {
		return buildMenu;
	}

	public ChatBox getChatBox() {
		return chatBox;
	}

	public void dispose() {
		inventoryMenu.dispose();
	}
}
