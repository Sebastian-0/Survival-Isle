package isle.survival.world;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;

public class ParticleBase {
	private static final String PARTICLE_FOLDER = "particles" + File.pathSeparator;
	
	private ObjectMap<String, ParticleEffect> effects;

	private ParticleEffect defaultEffect;

	public ParticleBase() {
		effects = new ObjectMap<>();
		defaultEffect = new ParticleEffect();
	}

	public void dispose() {
		defaultEffect.dispose();
		effects.forEach((p) -> {p.value.dispose();}); 
	}
	
	public ParticleEffect getTexture(String name) {
		if (effects.containsKey(name)) {
			return new ParticleEffect(effects.get(name));
		}
		
		try {
			ParticleEffect effect = new ParticleEffect();
			effect.load(Gdx.files.internal(PARTICLE_FOLDER + name), Gdx.files.internal(PARTICLE_FOLDER));
			effects.put(name, effect);
			return effect;
		} catch (GdxRuntimeException e) {
			effects.put(name, defaultEffect);
			return defaultEffect;
		}
	}
}
