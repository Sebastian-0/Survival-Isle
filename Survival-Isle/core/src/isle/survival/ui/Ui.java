package isle.survival.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.client.GameProtocolCoder;
import isle.survival.world.TextureBase;
import isle.survival.world.WorldObjects;
import world.Inventory;

public class Ui {
	private BuildMenu buildMenu;
	private InventoryMenu inventoryMenu;
	private ChatBox chatBox;
	private ChatHistory chatHistory;
	private HealthBar healthBar;
	private boolean enabled;
	
	public Ui(TextureBase textures, Inventory inventory, GameProtocolCoder coder, WorldObjects objects) {
		buildMenu = new BuildMenu(textures, inventory, coder);
		inventoryMenu = new InventoryMenu(textures, inventory);
		chatBox = new ChatBox(textures);
		chatHistory = new ChatHistory();
		healthBar = new HealthBar(objects);
		enabled = true;
	}

	public void toggleUI() {
		enabled = !enabled;
	}
	
	public void draw(SpriteBatch spriteBatch) {
		if(enabled) {
			buildMenu.positionItems();
			int x = Gdx.graphics.getWidth()/2 - buildMenu.getWidth()/2;
			spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(x, 0, 0));
			buildMenu.draw(spriteBatch);
			spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(-x, 0, 0));
			
			int y = Gdx.graphics.getHeight() - inventoryMenu.getHeight();
			spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(0, y, 0));
			inventoryMenu.draw(spriteBatch);
			spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(0, -y, 0));
	
			if (chatBox.isEnabled() || chatHistory.hasResentMessages())
				chatHistory.draw(spriteBatch);
			
			if (chatBox.isEnabled())
				chatBox.draw(spriteBatch);
			
			healthBar.draw(spriteBatch);
		}
	}
	
	public void update(double deltaTime) {
		chatHistory.update(deltaTime);
	}
	
	public BuildMenu getBuildMenu() {
		return buildMenu;
	}

	public ChatBox getChatBox() {
		return chatBox;
	}

	public ChatHistory getChatHistory() {
		return chatHistory;
	}

	public void dispose() {
		inventoryMenu.dispose();
		buildMenu.dispose();
		chatBox.dispose();
		chatHistory.dispose();
	}
}
