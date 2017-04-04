package isle.survival;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import world.ClientWorld;

public class SurvivalIsleClient extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	ClientWorld world;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		world = new ClientWorld(20, 15);
		world.GenerateTerrain(0);
	}

	@Override
	public void render () {
		update();
		draw();
	}
	
	private void update() {
		//Sync objects
	}
	
	private void draw() {
		world.drawTerrain();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
