package isle.survival.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import isle.survival.client.GameProtocolCoder;
import isle.survival.ui.BuildMenu;
import isle.survival.ui.ChatBox;
import isle.survival.ui.Ui;
import isle.survival.world.SoundBase;
import world.Player;

public class InputProcessor extends InputAdapter {
	
	private static final float MOVEMENT_TIME = (float) Player.MOVEMENT_TIME;
	
	private GameProtocolCoder coder;
	private BuildMenu buildMenu;
	private ChatBox chatBox;
	private SoundBase soundBase;
	private Ui ui;
	
	private float movementCounter;

	public InputProcessor(Ui ui, GameProtocolCoder coder, SoundBase soundBase) {
		this.buildMenu = ui.getBuildMenu();
		this.chatBox = ui.getChatBox();
		this.ui = ui;
		this.coder = coder;
		this.soundBase = soundBase;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (chatBox.isEnabled())
			return true;
		
		switch (keycode) {
		case Input.Keys.W:
		case Input.Keys.UP:
			coder.sendMoveUp();
			movementCounter = MOVEMENT_TIME;
			break;
		case Input.Keys.A:
		case Input.Keys.LEFT:
			coder.sendMoveLeft();
			movementCounter = MOVEMENT_TIME;
			break;
		case Input.Keys.S:
		case Input.Keys.DOWN:
			coder.sendMoveDown();
			movementCounter = MOVEMENT_TIME;
			break;
		case Input.Keys.D:
		case Input.Keys.RIGHT:
			coder.sendMoveRight();
			movementCounter = MOVEMENT_TIME;
			break;
		case Input.Keys.Q:
			buildMenu.decrementSelection();
			break;
		case Input.Keys.E:
			buildMenu.incrementSelection();
			break;
		case Input.Keys.NUM_1:
		case Input.Keys.NUM_2:
		case Input.Keys.NUM_3:
		case Input.Keys.NUM_4:
		case Input.Keys.NUM_5:
		case Input.Keys.NUM_6:
		case Input.Keys.NUM_7:
		case Input.Keys.NUM_8:
		case Input.Keys.NUM_9:
			int toolIndex = keycode - Input.Keys.NUM_1;
			buildMenu.setSelectedIndex(toolIndex);
			break;
		case Input.Keys.M:
			soundBase.toggleMuteMusic();
			break;
		case Input.Keys.N:
			soundBase.toggleMuteSound();
			break;
		case Input.Keys.F1:
			ui.toggleUI();
			break;
		case Input.Keys.SPACE:
			coder.sendActivateTool();
			break;
		case Input.Keys.ESCAPE:
			coder.sendClose();
			soundBase.stopAll();
			break;
		default:
			return false;
		}
		return true;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if (chatBox.isEnabled())
			return true;
		
		switch (keycode) {
		case Input.Keys.SPACE:
			coder.sendDeactivateTool();
			break;
		default:
			return false;
		}
		return true;
	}
	
	@Override
	public boolean keyTyped(char character) {
		if (character == 13) { // Enter
			if (chatBox.isEnabled()) {
				String text = chatBox.getTextClearClose().trim();
				if (text != "")
					coder.sendChatMessage(text);
			} else {
				chatBox.enable();
			}
		} else if (chatBox.isEnabled()) {
			chatBox.keyTyped(character);
		} else {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean scrolled(int amount) {
		if (amount > 0)
			buildMenu.incrementSelection();
		else
			buildMenu.decrementSelection();
		return true;
	}
	
	public void update(float deltaTime) {
		if (chatBox.isEnabled())
			return;
		
		movementCounter -= deltaTime;
		
		if (movementCounter <= 0) {
			movementCounter = 0;
			if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
				movementCounter = MOVEMENT_TIME;
				coder.sendMoveUp();
			}
			if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				movementCounter = MOVEMENT_TIME;
				coder.sendMoveLeft();
			}
			if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
				movementCounter = MOVEMENT_TIME;
				coder.sendMoveDown();
			}
			if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				movementCounter = MOVEMENT_TIME;
				coder.sendMoveRight();
			}
		}
	}
}
