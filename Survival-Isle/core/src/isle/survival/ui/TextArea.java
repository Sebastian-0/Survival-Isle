package isle.survival.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import util.Point;

public class TextArea extends MenuComponent {
	
	public TextArea(Point position, Point size, BitmapFont font, String defaultText, String text) {
		super(position, size, font, defaultText, text);
		background = new Texture("text_field_background.png");
		backgroundFocus = new Texture("text_field_background_focus.png");
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
