package com.group11.hardware;

import java.util.Date;

import com.group11.base.Mode;

import android.content.SharedPreferences;

/**
 * stores the state of this glucometer
 * &&
 * manage the history records 
 */
public class CurrentStatus {
	
	/**
	 * @param preferences, use the preferences created after onCreate()
	 */
	public CurrentStatus(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	private static final String POWER_ON = "powerOn";
	private static final String CURRENT_TIME = "currentTime";
	private static final String CURRENT_MODE = "currentMode";

	private final SharedPreferences preferences;
	
	public boolean isPowerOn() {
		return preferences.getBoolean(POWER_ON, false);
	}

	public synchronized void setPowerOn(boolean powerOn) {
		preferences.edit().putBoolean(POWER_ON, powerOn).commit();
	}

	public Date getCurrentTime() {
		return new Date(preferences.getLong(CURRENT_TIME, 0));
	}

	public synchronized void setCurrentTime(Date time) {
		if (time == null) {
			throw new IllegalArgumentException("@param time should not be null");
		}
		preferences.edit().putLong(CURRENT_TIME, time.getTime()).commit();
	}
	
	public Mode getCurrentMode() {
		int ordinal = preferences.getInt(CURRENT_MODE, -1);
		return ordinal == -1 ? null : Mode.get(ordinal);
	}
	
	public synchronized void setCurrentMode(Mode mode) {
		if (mode == null) {
			throw new IllegalArgumentException("@param mode should not be null");
		}
		preferences.edit().putInt(CURRENT_MODE, mode.ordinal()).commit();
	}
	
	/**
	 * the current time is increased by one second
	 */
	public void nextSecond() {
		//TODO
	}
}
