package isle.survival.world;

import com.badlogic.gdx.utils.ObjectMap;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundBase {
	ObjectMap<Integer, Sound> sounds;
	
	public SoundBase() {
		sounds = new ObjectMap<>();
		setUpSounds();
	}
	
	private void setUpSounds() {
		//sounds.put(0, Gdx.audio.newSound(Gdx.files.internal("test.wav")));
	}
	
	private void disposeSounds() {
		sounds.forEach((t) -> {t.value.dispose();}); 
	}
	
	public void playSound(int id, float volume) {
		sounds.get(id).play(volume);
	}
}
