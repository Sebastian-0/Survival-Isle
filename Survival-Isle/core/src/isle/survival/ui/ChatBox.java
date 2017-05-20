package isle.survival.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.world.TextureBase;
import util.Point;

public class ChatBox {
	
	private TextField textField;
	private Point position;
	private Point size;
	private boolean enabled;
	private BitmapFont font;
	
	public ChatBox(TextureBase textures) {
		font = new BitmapFont(Gdx.files.internal("high_tower_text24.fnt"));
		position = new Point();
		size = new Point();
		textField = new TextField(position, size, font, textures, "Enter text...", "");
		textField.setFocus(true);
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void enable() {
		enabled = true;
	}
	
	public String getTextClearClose() {
		String text = textField.getText();
		textField.text = "";
		enabled = false;
		return text;
	}

	public void draw(SpriteBatch spriteBatch) {
		position.set(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 2);
		size.set(Gdx.graphics.getWidth() / 2, 26);
		textField.draw(spriteBatch);
	}

	public void keyTyped(char character) {
		textField.keyTyped(character);
	}

	public void dispose() {
		font.dispose();
	}

}
