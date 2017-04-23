package isle.survival.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import isle.survival.world.SoundBase;
import isle.survival.world.TextureBase;
import server.ConnectionClosedException;
import server.ServerProtocol;

public class SurvivalIsleClient extends ApplicationAdapter implements ClientInterface {
	private SpriteBatch spriteBatch;
	private TextureBase textureBase;
	private SoundBase soundBase;

	private Socket socket;
	private ClientProtocolCoder coder;

	private ClientGame game;
	
	private String name; //TODO: move to where game should be created
	private TitleScreen titleScreen;
	
	public SurvivalIsleClient(String name) {
		this.name = name;
	}
	
	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		textureBase = new TextureBase();
		soundBase = new SoundBase();
		
		titleScreen = new TitleScreen(spriteBatch);

		game = new ClientGame(name, spriteBatch, textureBase, soundBase);	// TODO: move to where the game should be created
		connectToServer(); 			 										//
		if (coder == null)													//
			game = null;
	}
	
	private void connectToServer() {
		try {
			socket = new Socket("localhost", 1337);
			coder = game.connectToServer(socket);
			new ServerListener(this).start();
			
			System.out.println("Connected to host.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Error: Host not found.");
		} catch (IOException e) {
			System.out.println("Error: Could not connect to host. Connection refused.");
		}
	}
	
	@Override
	public void resize(int width, int height) {
		spriteBatch.setProjectionMatrix(spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height));
	}
	
	@Override
	public void render() {
		if (game == null) {
			titleScreen.draw();
		} else {
			synchronized (this) {
				game.update();
				game.draw();	
			}
		}
	}
	
	@Override
	public void parseServerMessage() {
		try {
			ServerProtocol code = coder.receiveCode();
	//		System.out.println("Client received: " + code);
			
			synchronized (this) {
				switch (code) {
				case FailedToConnect:
					System.out.println("User name already in use.");
					closeSocket();
					game = null;
					Gdx.app.exit();
					Thread.currentThread().interrupt();
					break;
				case SendClose:
					System.out.println("Disconnected from host.");
					coder.acknowledgeClose();
					closeSocket();
					game = null;
					Gdx.app.exit();
					Thread.currentThread().interrupt();
					break;
				case AckClose:
					closeSocket();
					game = null;
					Thread.currentThread().interrupt();
					break;
				default:
					game.parseServerMessage(code);
					break;
				}
			}
		} catch (ConnectionClosedException e) {
			System.out.println("Host lost.");
			closeSocket();
			game = null;
			Thread.currentThread().interrupt();
		}
	}
	
	@Override
	public void dispose() {
		textureBase.dispose();
		spriteBatch.dispose();
		soundBase.dispose();

		if (socket != null && !socket.isClosed()) {
			synchronized (this) {
				coder.sendClose();
			}
			closeSocket();
		}
	}


	private void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
