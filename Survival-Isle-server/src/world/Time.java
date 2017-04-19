package world;

import java.io.Serializable;

public class Time implements Serializable {
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
			game.doForEachClient(c->c.sendTimeEvent(0));
			System.out.println("Dusk of day " + day);
		}
		else if (!wasDay && isDaytime()) {
			game.doForEachClient(c->c.sendTimeEvent(1));
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
