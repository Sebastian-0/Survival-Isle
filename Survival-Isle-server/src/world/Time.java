package world;

import java.io.Serializable;

import server.TimeInterface;

public class Time implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DAY_DURATION = 60;
	private static final int NIGHT_DURATION = 60;
	private double time;
	private int day;
	
	public void advanceTime(TimeInterface timeInterface, double deltaTime) {
		boolean wasDay = isDaytime();
		
		time += deltaTime;
		if (time > DAY_DURATION + NIGHT_DURATION)
		{
			time %= DAY_DURATION + NIGHT_DURATION;
			day++;
		}

		if (wasDay && !isDaytime()) {
			timeInterface.nightBegun(day);
		}
		else if (!wasDay && isDaytime()) {
			timeInterface.dayBegun(day);
		}
	}
	
	public boolean isDaytime() {
		return time < DAY_DURATION;
	}
	
	public int getDay() {
		return day;
	}
}
