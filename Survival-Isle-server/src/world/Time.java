package world;

import java.io.Serializable;

import server.TimeListener;

public class Time implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DAY_DURATION = 60;
	private static final int NIGHT_DURATION = 60;
	private double time;
	private int day;
	
	public void advanceTime(TimeListener timeListener, double deltaTime) {
		boolean wasDay = isDaytime();
		
		time += deltaTime;
		if (time > DAY_DURATION + NIGHT_DURATION)
		{
			time %= DAY_DURATION + NIGHT_DURATION;
			day++;
		}

		if (wasDay && !isDaytime()) {
			timeListener.nightBegun(day);
		}
		else if (!wasDay && isDaytime()) {
			timeListener.dayBegun(day);
		}
	}
	
	public boolean isDaytime() {
		return time < DAY_DURATION;
	}
	
	public int getDay() {
		return day;
	}

	public void advanceToNight(TimeListener timeListener) {
		time = DAY_DURATION - 1;
		advanceTime(timeListener, 1);
	}

	public void advanceToDay(TimeListener timeListener) {
		time = DAY_DURATION + NIGHT_DURATION - 1;
		advanceTime(timeListener, 1);
	}
}
