package isle.survival.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import util.Point;

public class ChatBox {
	
	private TextField textField;
	private boolean enabled;
	
	public ChatBox() {
		BitmapFont font = new BitmapFont(Gdx.files.internal("font32.fnt"));
		int x = Gdx.graphics.getWidth() / 2;
		int y = Gdx.graphics.getHeight() / 2;
		textField = new TextField(new Point(x / 2, y), new Point(x, 26), font, "");
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
		textField.draw(spriteBatch);
	}

	public void keyTyped(char character) {
		textField.keyTyped(character);
	}

}
