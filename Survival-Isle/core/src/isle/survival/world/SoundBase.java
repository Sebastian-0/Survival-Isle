package isle.survival.world;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import server.Connection;
import util.Point;
import world.SoundType;

public class SoundBase {
	private static final float FALLOFF_DISTANCE = 20;
	private ObjectMap<String, Sound> sounds;
	private ObjectMap<String, Sound> music;
	private ObjectMap<Integer, String> names;
	private boolean muteMusic;
	private boolean muteSound;
	private Sound defaultSound;
	private Point cameraPos;
	
	public SoundBase() {
		sounds = new ObjectMap<>();
		music = new ObjectMap<>();
		names = new ObjectMap<>();
		cameraPos = new Point();
		setUpSounds();
	}
	
	private void setUpSounds() {
		defaultSound = Gdx.audio.newSound(Gdx.files.internal("sound/default.wav"));
		getMusic("DayMusic");
		getMusic("NightMusic");

		//TODO: add sounds played from server
		names.put(SoundType.EnemyAttack.ordinal(), "enemy_attack");
		names.put(SoundType.PlayerDeath.ordinal(), "player_death");
		names.put(SoundType.CrystalBreak.ordinal(), "crystal_break");
		names.put(SoundType.GameOver.ordinal(), "game_over");
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
		int id = coder.receiveInt();
		String name = names.get(id);
		
		if (coder.receiveInt() == 0) {
			playSound(name, 1);
		} else {
			int x = coder.receiveInt();
			int y = coder.receiveInt();
			playSoundAtPosition(name, new Point(x, y));
		}
		
	}

	public long playSound(String name, float volumeMultiplier) {
		if (!muteSound) {
			return getSound(name).play(0.3f * volumeMultiplier);
		}
		return -1;
	}

	public long playSoundAtPosition(String name, Point soundPos) {
		float dx = soundPos.x-cameraPos.x;
		float dy = soundPos.y-cameraPos.y;
		float volume = Math.max(0, 1 - (float) Math.sqrt(dx*dx+dy*dy)/FALLOFF_DISTANCE);
		
		return playSound(name, volume);
	}
	
	public void stopSound(String name) {
		getSound(name).stop();
	}

	public long playMusic(String name) {
		if (!muteMusic) {
			return getMusic(name).play(0.5f);
		}
		
		long m = getMusic(name).play(0.5f);
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

	public void setVolumeOfMusic(String name, long soundId, float volume) {
		getMusic(name).setVolume(soundId, volume);
	}

	public boolean isSoundMuted() {
		return muteSound;
	}
	
	public boolean isMusicMuted() {
		return muteMusic;
	}

	public void setCameraPosition(float cameraX, float cameraY) {
		cameraPos.x = cameraX;
		cameraPos.y = cameraY;
	}
}
