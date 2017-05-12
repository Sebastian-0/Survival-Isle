package isle.survival.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

import server.Connection;

public class SoundBase {
	private ObjectMap<Integer, Sound> sounds;
	private boolean muteMusic;
	private boolean muteSound;
	
	public SoundBase() {
		sounds = new ObjectMap<>();
		setUpSounds();
	}
	
	private void setUpSounds() {
		sounds.put(0, Gdx.audio.newSound(Gdx.files.internal("sound/372443_Rise.mp3")));
		sounds.put(1, Gdx.audio.newSound(Gdx.files.internal("sound/730287_Elves-of-the-West.mp3")));
	}
	
	public void dispose() {
		sounds.forEach((t) -> {t.value.dispose();}); 
	}
	
	public void playSound(Connection coder) {
		if (!muteSound) {
			int id = coder.receiveInt();
			sounds.get(id).play((float)0.5);
		}
	}

	public void playSound(int id) {
		if (!muteSound) {
			sounds.get(id).play((float)0.5);
		}
	}

	public void stopSound(int id) {
		sounds.get(id).stop();
	}
	
	public void toggleMuteSound() {
		muteSound = !muteSound;
	}

	public void toggleMuteMusic() {
		muteMusic = !muteMusic;
	}
}
