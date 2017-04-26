package isle.survival.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.ui.Button;
import isle.survival.ui.TextField;
import util.Point;

public class TitleScreen extends InputAdapter {
	private TitleScreenBackend backend;
	private SpriteBatch spriteBatch;
	private Texture texture;
	private TextField nameField;
	private TextField ipField;
	private TextField portField;
	private Button startButton;
	private BitmapFont font;
	
	public TitleScreen(TitleScreenBackend backend, SpriteBatch spriteBatch) {
		this.backend = backend;
		texture = new Texture("title_screen.png");
		this.spriteBatch = spriteBatch;
		
		font = new BitmapFont(Gdx.files.internal("dragonslapper.fnt"));
		nameField = new TextField(new Point(160, 240), new Point(320, 26), font, "player" + (int)Math.floor(Math.random()*100));
		ipField = new TextField(new Point(160, 200), new Point(320, 26), font, "localhost");
		portField = new TextField(new Point(160, 160), new Point(320, 26), font, "1337");
		startButton = new Button(new Point(256, 100), new Point(128, 28), font, " Join Game");
	}
	
	public void draw() {
		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		
		spriteBatch.draw(texture, 0, 0);
		nameField.draw(spriteBatch);
		ipField.draw(spriteBatch);
		portField.draw(spriteBatch);
		startButton.draw(spriteBatch);
		
		spriteBatch.end();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.ESCAPE)
			backend.terminateProgram();
		return false;
	}
	
	@Override
	public boolean keyTyped(char character) {
		nameField.keyTyped(character); 
		ipField.keyTyped(character); 
		portField.keyTyped(character);
		return true;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		nameField.touchDown(screenX, screenY, pointer, button); 
		ipField.touchDown(screenX, screenY, pointer, button); 
		portField.touchDown(screenX, screenY, pointer, button);
		if (startButton.touchDown(screenX, screenY, pointer, button)) {
			try {
			backend.startNewGame(nameField.getText(), ipField.getText(), Integer.parseInt(portField.getText()));
			} catch (NumberFormatException e) {
				System.out.println("Illegal port number.");
			}
		}
		return true;
	}
	
	
	public void dispose() {
		font.dispose();
	}
}
