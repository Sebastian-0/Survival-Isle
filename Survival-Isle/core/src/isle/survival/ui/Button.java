package isle.survival.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import isle.survival.world.TextureBase;
import util.Point;

public class Button extends MenuComponent {
	
	public Button(Point position, Point size, BitmapFont font, TextureBase textures, String text) {
		super(position, size, font, "", text);
		background = textures.getTexture("button_background");
		backgroundFocus = textures.getTexture("button_background");
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenY = Gdx.graphics.getHeight() - screenY;
		if (position.x <= screenX && position.y <= screenY && position.x + size.x > screenX && position.y + size.y > screenY) {
			hasFocus = true;
			return true;
		}
		hasFocus = false;
		return false;
	}
}
