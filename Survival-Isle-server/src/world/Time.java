package world;

public class Time {
	private static final int DAY_DURATION = 10;
	private static final int NIGHT_DURATION = 10; 
	private double time;
	private int day;
	
	public void advanceTime(GameInterface game, double deltaTime) {
		boolean wasDay = isDaytime();
		
		time += deltaTime;
		if (time > DAY_DURATION + NIGHT_DURATION)
		{
			time %= DAY_DURATION + NIGHT_DURATION;
			day++;
		}

		if (wasDay && !isDaytime()) {
			game.doForEachClient(c->c.sendTimeEvent(false));
			System.out.println("Dusk of day " + day);
		}
		else if (!wasDay && isDaytime()) {
			game.doForEachClient(c->c.sendTimeEvent(true));
			System.out.println("Dawn of day " + day);
		}
	}
	
	public boolean isDaytime() {
		return time < DAY_DURATION;
	}
	
	public int getDay() {
		return day;
	}
}
