package isle.survival.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Lifted from Dungeoneers by courtesy of Mattias Gustafsson, Jakob Hjelm, Sebastian Hjelm, Markus Olsson 
 */
public class Shaders {

	public static ShaderProgram colorShader;
	public static ShaderProgram monochromeShader;
	
	public static void initShaders() {
		colorShader = initShader("shaders/default.vert", "shaders/color.frag");
		if (colorShader.isCompiled()) {
			colorShader.begin();
			colorShader.setUniformi("enabled", 0);
			colorShader.setUniformf("tint", 1, 0, 0);
			colorShader.end();
		}

		monochromeShader = initShader("shaders/default.vert", "shaders/monochrome.frag");
		if (monochromeShader.isCompiled()) {
			monochromeShader.begin();
			monochromeShader.setUniformf("colorIntensity", 0);
			monochromeShader.end();
		}
	}

	private static ShaderProgram initShader(String vertPath, String fragPath) {
		FileHandle vertexShader   = Gdx.files.internal(vertPath);
		FileHandle fragmentShader = Gdx.files.internal(fragPath);
		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (!shader.isCompiled()) {
			System.out.println("Shaders: initShaders(): Could not compile the shader, printing shader log:\n" + shader.getLog());
		}
		return shader;
	}
}
