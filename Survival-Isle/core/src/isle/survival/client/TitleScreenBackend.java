package isle.survival.client;

public interface TitleScreenBackend {
	public void startNewGame(String name, String ip, int port);
	public void terminateProgram();
}
