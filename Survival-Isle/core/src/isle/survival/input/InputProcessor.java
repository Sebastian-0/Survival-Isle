package isle.survival.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import isle.survival.client.GameProtocolCoder;
import isle.survival.ui.BuildMenu;
import isle.survival.ui.ChatBox;
import isle.survival.ui.Ui;
import isle.survival.world.NetworkObject;

public class InputProcessor extends InputAdapter {
	
	private static final float MOVEMENT_TIME = NetworkObject.MOVEMENT_TIME;
	
	private GameProtocolCoder coder;
	private BuildMenu buildMenu;
	private ChatBox chatBox;
	
	private float movingUpCounter;
	private float movingLeftCounter;
	private float movingDownCounter;
	private float movingRightCounter;

	public InputProcessor(Ui ui, GameProtocolCoder coder) {
		this.buildMenu = ui.getBuildMenu();
		this.chatBox = ui.getChatBox();
		this.coder = coder;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (chatBox.isEnabled())
			return true;
		
		switch (keycode) {
		case Input.Keys.W:
		case Input.Keys.UP:
			coder.sendMoveUp();
			movingUpCounter = MOVEMENT_TIME;
			break;
		case Input.Keys.A:
		case Input.Keys.LEFT:
			coder.sendMoveLeft();
			movingLeftCounter = MOVEMENT_TIME;
			break;
		case Input.Keys.S:
		case Input.Keys.DOWN:
			coder.sendMoveDown();
			movingDownCounter = MOVEMENT_TIME;
			break;
		case Input.Keys.D:
		case Input.Keys.RIGHT:
			coder.sendMoveRight();
			movingRightCounter = MOVEMENT_TIME;
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
		case Input.Keys.SPACE:
			coder.sendActivateTool();
			break;
		case Input.Keys.ESCAPE:
			Gdx.app.exit();
			Thread.currentThread().interrupt();
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
				String text = chatBox.getTextClearClose();
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
		
		if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))
			movingUpCounter -= deltaTime;
		if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
			movingLeftCounter-= deltaTime;
		if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))
			movingDownCounter -= deltaTime;
		if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			movingRightCounter -= deltaTime;
		
		if (movingUpCounter < 0) {
			movingUpCounter += MOVEMENT_TIME;
			coder.sendMoveUp();
		}

		if (movingLeftCounter < 0) {
			movingLeftCounter += MOVEMENT_TIME;
			coder.sendMoveLeft();
		}

		if (movingDownCounter < 0) {
			movingDownCounter += MOVEMENT_TIME;
			coder.sendMoveDown();
		}

		if (movingRightCounter < 0) {
			movingRightCounter += MOVEMENT_TIME;
			coder.sendMoveRight();
		}
	}
}
