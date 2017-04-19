package server;

public class ServerUpdater extends Thread {
	
	private Game game;
	private volatile boolean running;
	
	public ServerUpdater(Game game) {
		super ("Server updater");
		this.game = game;
	}
	
	@Override
	public void run() {
		running = true;
		while (running) {
			long beforeUpdate = System.currentTimeMillis();
			game.update(0.016);
			long afterUpdate = System.currentTimeMillis();
			long sleepTime = 1000 / 60 - (afterUpdate - beforeUpdate);
			try {
				if (sleepTime > 0)
					sleep(sleepTime);
				else
					System.out.println("Frame too slow");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void close() {
		running = false;
	}
}
