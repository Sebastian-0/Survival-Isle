package isle.survival.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import util.Point;

public class TextField extends MenuComponent {
	
	public TextField(Point position, Point size, BitmapFont font, String defaultText, String text) {
		super(position, size, font, defaultText, text);
		background = new Texture("text_field_background.png");
		backgroundFocus = new Texture("text_field_background_focus.png");
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (hasFocus) {
			if (keycode == Keys.ENTER) {
				hasFocus = false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {		
		if (hasFocus) {
			if (character == 8) { //Backspace
				if (text.length() > 0)
					text = text.substring(0,text.length()-1);
			}
			else if (character == 22) { //Ctrl+v
				text += Gdx.app.getClipboard().getContents().replaceAll("\n", "");
			}
			else if (character >= 32 && character < 127) {
				text += character;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenY = Gdx.graphics.getHeight() - screenY;
		if (position.x <= screenX && position.y <= screenY && position.x + size.x > screenX && position.y + size.y > screenY)
			hasFocus = true;
		else
			hasFocus = false;
		
		return hasFocus;
	}
}
