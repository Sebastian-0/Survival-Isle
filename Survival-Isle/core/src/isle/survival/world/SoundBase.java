package isle.survival.world;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import server.Connection;

public class SoundBase {
	private ObjectMap<String, Sound> sounds;
	private ObjectMap<Integer, String> names;
	private boolean muteMusic;
	private boolean muteSound;
	private Sound defaultSound;
	
	public SoundBase() {
		sounds = new ObjectMap<>();
		names = new ObjectMap<>();
		setUpSounds();
	}
	
	private void setUpSounds() {
		defaultSound = Gdx.audio.newSound(Gdx.files.internal("sound/default.wav"));
		//TODO: add sounds played from server
	}
	
	public void dispose() {
		sounds.forEach((t) -> {t.value.dispose();}); 
		defaultSound.dispose();
	}
	
	private Sound getSound(String name) {
		if (sounds.containsKey(name)) {
			return sounds.get(name);
		}
		
		try {
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/" + name + ".mp3"));
			sounds.put(name, sound);
			return sound;
		} catch (GdxRuntimeException e) {
			System.out.println("Failed to load the sound: " + name + ".mp3");
			sounds.put(name, defaultSound);
			return defaultSound;
		}
	}
	
	public void playSound(Connection coder) {
		if (!muteSound) {
			int id = coder.receiveInt();
			getSound(names.get(id)).play((float)0.5);
		}
	}

	public long playSound(String name) {
		if (!muteSound) {
			return getSound(name).play((float)0.5);
		}
		return -1;
	}

	public void stopSound(String name) {
		getSound(name).stop();
	}
	
	public void stopAll() {
		Iterator<Entry<String, Sound>> i = sounds.iterator();
		while (i.hasNext()) {
			i.next().value.stop();
		}
	}
	
	public void toggleMuteSound() {
		muteSound = !muteSound;
		if (muteSound)
			stopAll();
	}

	public void toggleMuteMusic() {
		muteMusic = !muteMusic;
	}

	public void setVolumeOfSound(String name, long soundId, float volume) {
		getSound(name).setVolume(soundId, volume);
	}
}
