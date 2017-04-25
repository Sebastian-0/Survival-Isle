package isle.survival.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import util.Point;

public class TextField extends MenuComponent {
	
	public TextField(Point position, Point size, BitmapFont font, String text) {
		super(position, size, font, text);
		background = new Texture("text_field_background.png");
		backgroundFocus = new Texture("text_field_background_focus.png");
	}

	@Override
	public boolean keyTyped(char character) {		
		if (hasFocus) {
			if (character == 10 || character == 13) { //Enter
				hasFocus = false;
			} else if (character == 8) { //Backspace
				if (text.length() > 0)
					text = text.substring(0,text.length()-1);
			}
			else if (character == 22) { //Ctrl+v
				text += Gdx.app.getClipboard().getContents().replaceAll("\n", "");
			}
			else if (character >= 32 && character < 127) {
				text += character;
			}
		}
		return hasFocus;
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
