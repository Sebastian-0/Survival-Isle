package isle.survival.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import isle.survival.world.TextureBase;
import util.Point;

public class TextArea extends MenuComponent {
	
	public TextArea(Point position, Point size, BitmapFont font, TextureBase textures, String defaultText, String text) {
		super(position, size, font, defaultText, text);
		background = textures.getTexture("white");
		backgroundFocus = textures.getTexture("text_field_background_focus");
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setPosition(Point position) {
		this.position = position;
	}
}
