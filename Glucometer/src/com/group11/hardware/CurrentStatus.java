package com.group11.hardware;

import java.util.Date;

import com.group11.base.BatteryLevel;
import com.group11.base.Mode;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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
		editor = this.preferences.edit();
	}

	private final SharedPreferences preferences;
	private final Editor editor;


	private static final String POWER_ON = "powerOn";
	private static final String CURRENT_TIME = "currentTime";
	private static final String CURRENT_MODE = "currentMode";
	private static final String BATTERY_LEVEL = "batteryLevel";

	/**
	 * write in all the changes that have been made
	 */
	public synchronized void commit() {
		this.editor.commit();
	}
	
	public boolean isPowerOn() {
		return preferences.getBoolean(POWER_ON, false);
	}

	/**
	 * call commit() to write in the changes
	 * @param powerOn
	 */
	public synchronized void setPowerOn(boolean powerOn) {
		this.editor.putBoolean(POWER_ON, powerOn);
	}

	public Date getCurrentTime() {
		return new Date(preferences.getLong(CURRENT_TIME, 0));
	}

	/**
	 * call commit() to write in the changes
	 * @param time
	 */
	public synchronized void setCurrentTime(Date time) {
		if (time == null) {
			throw new IllegalArgumentException("@param time should not be null");
		}
		this.editor.putLong(CURRENT_TIME, time.getTime());
	}

	/**
	 * the current time is increased by one second
	 */
	public synchronized void nextSecond() {
		long time = preferences.getLong(CURRENT_TIME, 0);
		time += 1000;
		preferences.edit().putLong(CURRENT_TIME, time).commit();
	}

	public Mode getCurrentMode() {
		int ordinal = preferences.getInt(CURRENT_MODE, -1);
		return ordinal == -1 ? null : Mode.get(ordinal);
	}
	
	/**
	 * call commit() to write in the changes
	 * @param mode
	 */
	public synchronized void setCurrentMode(Mode mode) {
		this.editor.putInt(CURRENT_MODE, mode == null ? -1 : mode.ordinal());
	}
	
	public BatteryLevel getBatteryLevel() {
		return BatteryLevel.getByValue(preferences.getInt(BATTERY_LEVEL, 100));
	}
	
	/**
	 * call commit() to write in the changes
	 * @param value
	 */
	public synchronized void setBatteryLevel(int value) {
		value %= 101;
		this.editor.putInt(BATTERY_LEVEL, value);
	}
}
