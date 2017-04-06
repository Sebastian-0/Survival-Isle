package server;

public class ServerUpdater implements Runnable {
	
	private Game game;
	
	public ServerUpdater(Game server) {
		this.game = server;
	}
	
	@Override
	public void run() {
		while (true) {
			long beforeUpdate = System.currentTimeMillis();
			game.update(0.016);
			long afterUpdate = System.currentTimeMillis();
			long sleepTime = 1000 / 60 - (afterUpdate - beforeUpdate);
			try {
				if (sleepTime > 0)
					Thread.sleep(sleepTime);
				else
					System.out.println("Frame too slow");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}