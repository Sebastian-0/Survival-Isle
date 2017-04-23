package isle.survival.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TitleScreen {
	private SpriteBatch spriteBatch;
	private Texture texture;
	
	public TitleScreen(SpriteBatch spriteBatch) {
		texture = new Texture("title_screen.png");
		this.spriteBatch = spriteBatch;
	}
	
	public void draw() {
		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		
		spriteBatch.draw(texture, 0, 0);
		
		spriteBatch.end();
	}
}
