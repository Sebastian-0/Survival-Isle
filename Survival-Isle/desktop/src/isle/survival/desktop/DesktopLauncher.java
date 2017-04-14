package isle.survival.desktop;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import isle.survival.client.SurvivalIsleClient;
import server.Server;

public class DesktopLauncher {
	public static void main (String[] arg) throws IOException {
//		Server server = new Server();
//		server.start();
		
		String name = JOptionPane.showInputDialog("Enter name");
		if (name != null) {
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			new LwjglApplication(new SurvivalIsleClient(name), config);
		}
	}
}
