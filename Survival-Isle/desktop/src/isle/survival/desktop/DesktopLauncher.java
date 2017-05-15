package isle.survival.desktop;

import isle.survival.client.SurvivalIsleClient;

import java.io.IOException;

import server.Server;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) throws IOException {
		Server server = new Server(1337);
		server.newGame();
		server.start();
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 600;
		config.addIcon("icon.png", FileType.Internal);
		config.addIcon("icon1.png", FileType.Internal);
		config.addIcon("icon2.png", FileType.Internal);
		new LwjglApplication(new SurvivalIsleClient(), config);
	}
}
