package isle.survival.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import util.Point;

public abstract class MenuComponent {
	protected String defaultText;
	protected String text;
	protected boolean hasFocus;
	protected Point position;
	protected Point size;
	protected BitmapFont font;
	protected TextureRegion background;
	protected TextureRegion backgroundFocus;
	
	public MenuComponent(Point position, Point size, BitmapFont font, String defaultText, String text) {
		this.position = position;
		this.size = size;
		this.font = font;
		this.font.setColor(Color.BLACK);
		hasFocus = false;
		this.text = text;
		this.defaultText = defaultText;
	}

	public void setFocus(boolean focus) {
		hasFocus = focus;
	}
	
	public String getText() {
		return text;
	}
	
	public void draw(SpriteBatch spriteBatch) {
		if (hasFocus)
			spriteBatch.draw(backgroundFocus, position.x, position.y, size.x, size.y);
		else
			spriteBatch.draw(background, position.x, position.y, size.x, size.y);
			
		if (text.equals("")) {
			font.setColor(Color.GRAY);
			font.draw(spriteBatch, defaultText, position.x+4, position.y + size.y/2 + font.getCapHeight()/2);
		} else {
			font.setColor(Color.BLACK);
			font.draw(spriteBatch, text, position.x+4, position.y + size.y/2 + font.getCapHeight()/2);
		}
	}
	
	public boolean keyDown(int keycode) {
		return false;
	}
	
	public boolean keyTyped(char character) {
		return false;
	}
	
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}
}
