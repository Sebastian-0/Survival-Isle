package isle.survival.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import isle.survival.ui.Button;
import isle.survival.ui.TextArea;
import isle.survival.ui.TextField;
import isle.survival.world.TextureBase;
import util.Point;

public class TitleScreen extends InputAdapter {
	
	private static final String PREFERENCES_FILE = "survival_isle_preferences";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_HOST = "host";
	private static final String KEY_PORT = "port";
	
	private TitleScreenBackend backend;
	private SpriteBatch spriteBatch;
	private TextureRegion texture;
	private TextField nameField;
	private TextField ipField;
	private TextField portField;
	private Button startButton;
	private TextArea errorMessageArea;
	private TextArea creditsArea;
	private BitmapFont font;
	private BitmapFont errorFont;
	private BitmapFont creditsFont;
	
	public TitleScreen(TitleScreenBackend backend, SpriteBatch spriteBatch, TextureBase textures) {
		this.backend = backend;
		texture = textures.getTexture("title_screen");
		this.spriteBatch = spriteBatch;
		
		Preferences preferences = Gdx.app.getPreferences(PREFERENCES_FILE);

		font = new BitmapFont(Gdx.files.internal("dragonslapper.fnt"));
		nameField = new TextField(new Point(160, 240), new Point(320, 26), font, textures, "Enter username...", preferences.getString(KEY_USERNAME, "Username"));
		ipField = new TextField(new Point(160, 200), new Point(320, 26), font, textures, "Enter host IP...", preferences.getString(KEY_HOST, "localhost"));
		portField = new TextField(new Point(160, 160), new Point(320, 26), font, textures, "Enter port number...", preferences.getString(KEY_PORT, "1337"));
		startButton = new Button(new Point(256, 100), new Point(128, 28), font, textures, " Join Game");
		
		errorFont = new BitmapFont(Gdx.files.internal("font32.fnt")); 
		errorMessageArea = new TextArea(new Point(160, 30), new Point(320, 24), errorFont, textures, "", "");

		creditsFont = new BitmapFont(Gdx.files.internal("credits.fnt"));
		int x = 320-Gdx.graphics.getWidth()/2;
		
		creditsArea = new TextArea(new Point(x, -32), new Point(0, 0), creditsFont, textures, 
				"", "Game by Mattias Gustafsson, Sebastian Hjelm, Markus Olsson, Måns Åhlander\n"
				+ "Music by Grandvision, John Rawman (www.newgrounds.com)");
	}
	
	public void draw() {
		Gdx.graphics.getGL20().glClearColor(1, 1, 1, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();

		int dw = getWidthOffset();
		int dh = getHeightOffset();
		spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(dw, dh, 0));
		spriteBatch.draw(texture, 0, 0);
		nameField.draw(spriteBatch);
		ipField.draw(spriteBatch);
		portField.draw(spriteBatch);
		startButton.draw(spriteBatch);
		errorMessageArea.draw(spriteBatch);
		creditsArea.draw(spriteBatch);
		spriteBatch.setTransformMatrix(spriteBatch.getTransformMatrix().translate(-dw, -dh, 0));
		
		spriteBatch.end();
	}

	private int getHeightOffset() {
		return Gdx.graphics.getHeight()/2 - texture.getRegionHeight()/2;
	}

	private int getWidthOffset() {
		return Gdx.graphics.getWidth()/2 - texture.getRegionWidth()/2;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.ESCAPE)
			backend.terminateProgram();
		else {
			boolean inField = (nameField.keyDown(keycode) || 
							ipField.keyDown(keycode) || 
							portField.keyDown(keycode));
			if (!inField && keycode == Input.Keys.ENTER) {
				startGame();
			}
		}
		return true;
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
		int dw = getWidthOffset();
		int dh = getHeightOffset();
		
		screenX -= dw;
		screenY += dh;
		
		nameField.touchDown(screenX, screenY, pointer, button); 
		ipField.touchDown(screenX, screenY, pointer, button); 
		portField.touchDown(screenX, screenY, pointer, button);
		if (startButton.touchDown(screenX, screenY, pointer, button)) {
			startGame();
		}
		return true;
	}
	
	
	public void startGame() {
		errorMessageArea.setText("");
		startButton.setFocus(true);
		try {
			backend.startNewGame(nameField.getText(), ipField.getText(), Integer.parseInt(portField.getText()));
		} catch (NumberFormatException e) {
			System.out.println("Illegal port number.");
		}
	}
	
	public void setErrorMessage(String error) {
		errorMessageArea.setText(error);
	}

	public void dispose() {
		Preferences preferences = Gdx.app.getPreferences(PREFERENCES_FILE);
		preferences.putString(KEY_USERNAME, nameField.getText());
		preferences.putString(KEY_HOST, ipField.getText());
		preferences.putString(KEY_PORT, portField.getText());
		preferences.flush();
		
		font.dispose();
		errorFont.dispose();
	}

	public void resize(int width, int height) {
		creditsArea.setPosition(new Point(320-Gdx.graphics.getWidth()/2, 240-Gdx.graphics.getHeight()/2+32));
	}
}
