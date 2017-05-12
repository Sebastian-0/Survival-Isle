package isle.survival.world;

import com.badlogic.gdx.utils.ObjectMap;

import server.Connection;

import com.badlogic.gdx.audio.Sound;

public class SoundBase {
	private ObjectMap<Integer, Sound> sounds;
	private boolean muteMusic;
	private boolean muteSound;
	
	public SoundBase() {
		sounds = new ObjectMap<>();
		setUpSounds();
	}
	
	private void setUpSounds() {
		//sounds.put(0, Gdx.audio.newSound(Gdx.files.internal("test.wav")));
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
	
	public void toggleMuteSound() {
		muteSound = !muteSound;
	}

	public void toggleMuteMusic() {
		muteMusic = !muteMusic;
	}
}
