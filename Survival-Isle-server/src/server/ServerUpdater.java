package server;

public class ServerUpdater implements Runnable {
	
	@Override
	public void run() {
		while (true) {
			long beforeUpdate = System.currentTimeMillis();
			update();
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
	
	private void update() {
	}
}