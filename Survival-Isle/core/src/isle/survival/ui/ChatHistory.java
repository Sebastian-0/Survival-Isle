package isle.survival.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Queue;

public class ChatHistory {
	
	private static final int PADDING_X = 20;
	private static final int PADDING_Y = 130;
	private static final int SPACING_Y = 30;
	private static final double MESSAGE_DISPLAY_TIME = 5;
	private static final int HISTORY_SIZE_LIMIT = 20;
	
	private Queue<String> messages;
	private BitmapFont font;
	private double recentMessagesTimer;
	
	public ChatHistory() {
		font = new BitmapFont(Gdx.files.internal("font32.fnt"));
		font.setColor(Color.BLACK);
		messages = new Queue<>();
		messages.ensureCapacity(HISTORY_SIZE_LIMIT);
	}
	
	public void addMessage(String sender, String message) {
		messages.addFirst(sender + ": " + message);
		if (messages.size > HISTORY_SIZE_LIMIT)
			messages.removeLast();
		recentMessagesTimer = MESSAGE_DISPLAY_TIME;
	}

	public boolean hasResentMessages() {
		return recentMessagesTimer > 0;
	}

	public void draw(SpriteBatch spriteBatch) {
		for (int i = 0; i < messages.size; i++) {
			font.draw(spriteBatch, messages.get(i), PADDING_X, PADDING_Y + i * SPACING_Y);
		}
	}

	public void update(double deltaTime) {
		if (recentMessagesTimer > 0)
			recentMessagesTimer -= deltaTime;
	}

	public void dispose() {
		font.dispose();
	}
}
