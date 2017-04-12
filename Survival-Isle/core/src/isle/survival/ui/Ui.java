package isle.survival.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.world.TextureBase;

public class Ui {
	private BuildMenu buildMenu;
	
	public Ui(TextureBase textures) {
		buildMenu = new BuildMenu(textures);
	}

	public void draw(SpriteBatch spriteBatch) {
		int x = Gdx.graphics.getWidth()/2 - buildMenu.getWidth()/2;
		spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(x, 0, 0));
		buildMenu.draw(spriteBatch);
		spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(-x, 0, 0));
	}
	
	public BuildMenu getBuildMenu() {
		return buildMenu;
	}
}
