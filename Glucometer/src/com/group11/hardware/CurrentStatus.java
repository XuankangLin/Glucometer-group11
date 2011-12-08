package com.group11.hardware;

import java.util.Date;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * stores the state of this glucometer
 * &&
 * manage the history records 
 */
public class CurrentStatus extends Activity {

	private static CurrentStatus instance = null;
	public static CurrentStatus get() {
		if (instance == null) {
			instance = new CurrentStatus();
		}
		return instance;
	}
	
	private CurrentStatus() {}

	
	private static final String POWER_ON = "powerOn";
	private static final String CURRENT_TIME = "currentTime";

	private SharedPreferences preferences = getPreferences(MODE_PRIVATE);
	
	public boolean isPowerOn() {
		return preferences.getBoolean(POWER_ON, false);
	}

	public void setPowerOn(boolean powerOn) {
		preferences.edit().putBoolean(POWER_ON, powerOn);
	}

	public Date getCurrentTime() {
		return new Date(preferences.getLong(CURRENT_TIME, 0));
	}

	public void setCurrentTime(Date time) {
		preferences.edit().putLong(CURRENT_TIME, time.getTime());
	}
	
	/**
	 * the current time is increased by one second
	 */
	public void nextSecond() {
		//TODO
	}
}
