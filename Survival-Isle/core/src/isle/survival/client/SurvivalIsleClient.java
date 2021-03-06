package isle.survival.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;

import isle.survival.shaders.Shaders;
import isle.survival.world.ParticleBase;
import isle.survival.world.SoundBase;
import isle.survival.world.TextureBase;
import server.ConnectionClosedException;
import server.ServerProtocol;

public class SurvivalIsleClient extends ApplicationAdapter implements ClientInterface, TitleScreenBackend {
	private static final boolean ENABLE_PROFILING = false;

	private int totalRenderedFrames;
	private int totalRenderedSeconds;
	private int frameCount;
	private long frameTimestamp;
	
	private SpriteBatch spriteBatch;
	private TextureBase textureBase;
	private ParticleBase particleBase;
	private SoundBase soundBase;

	private Socket socket;
	private ClientProtocolCoder coder;

	private ClientGame game;
	
	private TitleScreen titleScreen;
	
	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		textureBase = new TextureBase();
		particleBase = new ParticleBase();
		soundBase = new SoundBase();
		
		titleScreen = new TitleScreen(this, spriteBatch, textureBase);
		Gdx.input.setInputProcessor(titleScreen);
		
		Shaders.initShaders();
		spriteBatch.setShader(Shaders.colorShader);
		
		if (ENABLE_PROFILING)
			GLProfiler.enable();
	}

	@Override
	public void startNewGame(String name, String ip, int port) {
		game = new ClientGame(name, spriteBatch, textureBase, particleBase, soundBase);
		connectToServer(ip, port);
		if (coder == null) {
			showTitleScreen();
		}
	}
	
	private void connectToServer(String ip, int port) {
		try {
			socket = new Socket(ip, port);
			coder = game.connectToServer(socket);
			new ServerListener(this).start();
			
			System.out.println("Connected to host.");
		} catch (UnknownHostException e) {
			titleScreen.setErrorMessage("Error: Host not found.");
		} catch (IOException e) {
			titleScreen.setErrorMessage("Could not connect to host, connection refused.");
		}
	}
	
	@Override
	public void resize(int width, int height) {
		spriteBatch.setProjectionMatrix(spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height));
		if (game != null)
			game.resize(width, height);
		titleScreen.resize(width, height);
	}
	
	@Override
	public void render() {
		synchronized (this) {
			if (game == null) {
				titleScreen.draw();
				frameTimestamp = System.currentTimeMillis();
			} else {
				game.update();
				game.draw();
				
				if (ENABLE_PROFILING) {
					frameCount++;
					if (System.currentTimeMillis() - frameTimestamp > 1000) {
						totalRenderedFrames += frameCount;
						totalRenderedSeconds += 1;
						System.out.println(String.format("FPS: %d (avg. %.2f), draw calls: %.2f, shader switches: %.2f, texture binds: %.2f, vertex count: %.2f, ",
								frameCount,
								totalRenderedFrames / (float)totalRenderedSeconds,
								GLProfiler.drawCalls / (float)frameCount,
								GLProfiler.shaderSwitches / (float)frameCount,
								GLProfiler.textureBindings / (float)frameCount,
								GLProfiler.vertexCount.average / (float)frameCount));
						frameCount = 0;
						frameTimestamp = System.currentTimeMillis();
						GLProfiler.reset();
					}
				}
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
					titleScreen.setErrorMessage("User name already in use.");
					closeSocket();
					showTitleScreen();
					Thread.currentThread().interrupt();
					break;
				case SendClose:
					titleScreen.setErrorMessage("Disconnected from host.");
					coder.acknowledgeClose();
					closeSocket();
					showTitleScreen();
					Thread.currentThread().interrupt();
					break;
				case AckClose:
					closeSocket();
					showTitleScreen();
					Thread.currentThread().interrupt();
					break;
				default:
					if (game != null)
						game.parseServerMessage(code);
					else
						throw new ConnectionClosedException();
					break;
				}
			}
		} catch (Throwable e) {
			titleScreen.setErrorMessage("Lost connection to host.");
			closeSocket();
			showTitleScreen();
			Thread.currentThread().interrupt();
			throw e;
		}
	}

	private void showTitleScreen() {
		game = null;
		coder = null;
		Gdx.input.setInputProcessor(titleScreen);
	}

	private void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void terminateProgram() {
		Gdx.app.exit();
	}
	
	@Override
	public void dispose() {
		textureBase.dispose();
		spriteBatch.dispose();
		soundBase.dispose();
		if (game != null)
			game.dispose();
		game = null;
		titleScreen.dispose();

		if (socket != null && !socket.isClosed()) {
			synchronized (this) {
				coder.sendClose();
			}
			closeSocket();
		}
	}

}
