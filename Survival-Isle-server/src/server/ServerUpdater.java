package server;

public class ServerUpdater extends Thread {
	
	private static int MAXIMUM_EXCEPTIONS_PER_SECOND = 5;
	
	private Game game;
	private GameCrashListener listener;
	
	private int exceptionCount;
	private long exceptionTimer;
	private volatile boolean running;
	
	public ServerUpdater(Game game, GameCrashListener listener) {
		super ("Server updater");
		this.game = game;
		this.listener = listener;
	}
	
	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				long beforeUpdate = System.currentTimeMillis();
				game.update(0.016);
				long afterUpdate = System.currentTimeMillis();
				long deltaTime = afterUpdate - beforeUpdate;
				long sleepTime = 1000 / 60 - deltaTime;
				try {
					if (sleepTime > 0)
						sleep(sleepTime);
					else
						System.out.println("Frame too slow: " + deltaTime + "ms");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			catch (Throwable e) {
				System.err.println("An exception occurred when updating the game!"); 
				e.printStackTrace();
				exceptionCount++;
			}
			
			long time = System.currentTimeMillis();
			if (time - exceptionTimer > 1000) {
				exceptionCount = 0;
				exceptionTimer = time;
			}
			
			if (exceptionCount > MAXIMUM_EXCEPTIONS_PER_SECOND) {
				System.out.println("Too many exceptions during the last second (" + exceptionCount + "), terminating the game!");
				listener.gameCrashed();
			}
		}
	}
	
	public void close() {
		running = false;
	}
}
