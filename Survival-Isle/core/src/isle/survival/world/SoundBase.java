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
	private ObjectMap<String, Sound> music;
	private ObjectMap<Integer, String> names;
	private boolean muteMusic;
	private boolean muteSound;
	private Sound defaultSound;
	
	public SoundBase() {
		sounds = new ObjectMap<>();
		music = new ObjectMap<>();
		names = new ObjectMap<>();
		setUpSounds();
	}
	
	private void setUpSounds() {
		defaultSound = Gdx.audio.newSound(Gdx.files.internal("sound/default.wav"));
		//TODO: add sounds played from server
		getMusic("DayMusic");
		getMusic("NightMusic");
	}
	
	public void dispose() {
		sounds.forEach((t) -> {t.value.dispose();}); 
		music.forEach((t) -> {t.value.dispose();}); 
		defaultSound.dispose();
	}
	
	private Sound getSound(String name) {
		if (sounds.containsKey(name)) {
			return sounds.get(name);
		}
		
		try {
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/" + name + ".wav"));
			sounds.put(name, sound);
			return sound;
		} catch (GdxRuntimeException e) {
			System.out.println("Failed to load sound \""+ name + ".wav\"");
			sounds.put(name, defaultSound);
			return defaultSound;
		}
	}
	
	private Sound getMusic(String name) {
		if (music.containsKey(name)) {
			return music.get(name);
		}
		
		try {
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/" + name + ".mp3"));
			music.put(name, sound);
			return sound;
		} catch (GdxRuntimeException e) {
			System.out.println("Failed to load music \""+ name + ".mp3\"");
			music.put(name, defaultSound);
			return defaultSound;
		}
	}
	
	public void playSound(Connection coder) {
		if (!muteSound) {
			int id = coder.receiveInt();
			getSound(names.get(id)).play((float)0.3);
		}
	}

	public long playSound(String name) {
		if (!muteSound) {
			return getSound(name).play((float)0.3);
		}
		return -1;
	}
	
	public void stopSound(String name) {
		getSound(name).stop();
	}

	public long playMusic(String name) {
		if (!muteMusic) {
			return getMusic(name).play((float)0.5);
		}
		
		long m = getMusic(name).play((float)0.5);
		getMusic(name).pause();
		return m;
	}

	public void stopMusic(String name) {
		getMusic(name).stop();
	}
	
	public void stopAll() {
		Iterator<Entry<String, Sound>> i = sounds.iterator();
		while (i.hasNext()) {
			i.next().value.stop();
		}

		i = music.iterator();
		while (i.hasNext()) {
			i.next().value.stop();
		}
	}
	
	public void toggleMuteSound() {
		muteSound = !muteSound;
	}

	public void toggleMuteMusic() {
		muteMusic = !muteMusic;
		if (muteMusic)
			pauseAllMusic();
		else {
			resumeAllMusic();
		}
	}
	
	private void pauseAllMusic() {
		Iterator<Entry<String, Sound>> i = music.iterator();
		while (i.hasNext()) {
			i.next().value.pause();
		}
	}
	private void resumeAllMusic() {
		Iterator<Entry<String, Sound>> i = music.iterator();
		while (i.hasNext()) {
			i.next().value.resume();
		}
	}

	public void setVolumeOfSound(String name, long soundId, float volume) {
		getSound(name).setVolume(soundId, volume);
	}

	public boolean isSoundMuted() {
		return muteSound;
	}
	
	public boolean isMusicMuted() {
		return muteMusic;
	}
}
