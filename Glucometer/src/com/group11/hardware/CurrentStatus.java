package com.group11.hardware;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.group11.base.BatteryLevel;
import com.group11.base.Mode;
import com.group11.base.Unit;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * stores the state of this glucometer
 * &&
 * manage the history records 
 */
public class CurrentStatus {
	
	private final static int DEFAULT_YEAR = 2000;
	private final static int DEFAULT_MONTH = 0;
	private final static int DEFAULT_DAY = 1;
	private final static int DEFAULT_HOUR = 0;
	private final static int DEFAULT_MINUTE = 0;
	
	public static long getDefaultTime() {
		Calendar calendar = new GregorianCalendar();
		calendar.set(DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY, DEFAULT_HOUR,
				DEFAULT_MINUTE);
		return calendar.getTimeInMillis();
	}
	
	
	/**
	 * @param preferences, use the preferences created after onCreate()
	 */
	public CurrentStatus(SharedPreferences preferences) {
		this.preferences = preferences;
		editor = this.preferences.edit();
	}

	private final SharedPreferences preferences;
	private final Editor editor;

	//=====should be reset every time it power on=====
	private static final String POWER_ON = "powerOn";
	private static final String REFRESH_TIME = "refreshTime";
	private static final String ERROR_NOW = "errorNow";

	//=====should be set by Logic Controller itself=====
	private static final String CURRENT_MODE = "currentMode";
	private static final String PREVIOUS_MODE = "previousMode";

	//=====should be reset every time this program starts=====
	private static final String AC_PLUGGED = "acPlugged";
	private static final String USB_CONNECTED = "usbConnected";
	private static final String STRIP_INSERTED = "stripInserted";

	//=====should be set in Setup Mode=====
	private static final String TIME_INTERVAL = "timeInterval";
	private static final String CURRENT_TIME = "currentTime";
	private static final String CURRENT_UNIT = "currentUnit";
	
	//=====set by Setting Dialog=====
	private static final String BATTERY_LEVEL = "batteryLevel";
	private static final String INITIALIZATION_ERROR = "initializationError";

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
		return new Date(preferences.getLong(CURRENT_TIME, getDefaultTime()));
	}

	/**
	 * call commit() to write in the changes
	 * @param time
	 */
	public synchronized void setCurrentTime(Date time) {
		if (time == null) {
			throw new IllegalArgumentException("@param time should not be null");
		}
		this.setCurrentTime(time.getTime());
	}
	
	/**
	 * call commit() to write in the changes
	 * @param time
	 */
	public synchronized void setCurrentTime(long time) {
		this.editor.putLong(CURRENT_TIME, time);
		preferences.edit().putLong(TIME_INTERVAL, new Date().getTime() - time).commit();
	}

	/**
	 * since the nextSecond() won't be called when the glucometer is off,
	 * we store the interval between the time set and the real time.
	 * this time when the glucometer powers on, reinstate the time according to new real time.
	 * 
	 * TODO should be called every time the glucometer is powered on
	 */
	public synchronized void syncCurrentTime() {
		long interval = preferences.getLong(TIME_INTERVAL, 0);
		preferences.edit().putLong(CURRENT_TIME, new Date().getTime() - interval).commit();
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
		if (mode != null) {
			this.editor.putInt(PREVIOUS_MODE, preferences.getInt(CURRENT_MODE, -1));			
		}
		this.editor.putInt(CURRENT_MODE, mode == null ? -1 : mode.ordinal());
	}
	
	public Mode getPreviousMode() { 
		int ordinal = preferences.getInt(PREVIOUS_MODE, -1);
		return ordinal == -1 ? null : Mode.get(ordinal);
	}
	
	public int getBattery() {
		return preferences.getInt(BATTERY_LEVEL, 100);
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
	
	public boolean isInitializationErrorNextTime() {
		return preferences.getBoolean(INITIALIZATION_ERROR, false);
	}
	
	public synchronized void setInitializationErrorNextTime(boolean error) {
		this.editor.putBoolean(INITIALIZATION_ERROR, error);
	}
	
	/**
	 * @return whether should refresh the Time every second
	 */
	public boolean isRefreshingTime() {
		return preferences.getBoolean(REFRESH_TIME, true);
	}
	
	/**
	 * set whether should refresh the Time every second
	 * @param refresh
	 */
	public synchronized void setRefreshingTime(boolean refresh) {
		this.editor.putBoolean(REFRESH_TIME, refresh);
	}
	
	/**
	 * @return L / DL
	 */
	public Unit getCurrentUnit() {
		return Unit.get(preferences.getInt(CURRENT_UNIT, Unit.L.ordinal()));
	}
	
	public synchronized void setCurrentUnit(Unit unit) {
		if (unit == null) {
			throw new IllegalArgumentException("@param unit should not be null");
		}
		
		this.editor.putInt(CURRENT_UNIT, unit.ordinal());
	}
	
	public boolean isACPlugged() {
		return preferences.getBoolean(AC_PLUGGED, false);
	}
	
	public synchronized void setACPlugged(boolean plugged) {
		this.editor.putBoolean(AC_PLUGGED, plugged);
	}
	
	public boolean isUSBConnected() {
		return preferences.getBoolean(USB_CONNECTED, false);
	}
	
	public synchronized void setUSBConnected(boolean usb) {
		this.editor.putBoolean(USB_CONNECTED, usb);
	}
	
	public boolean isStripInserted() {
		return preferences.getBoolean(STRIP_INSERTED, false);
	}
	
	public synchronized void setStripInserted(boolean inserted) {
		this.editor.putBoolean(STRIP_INSERTED, inserted);
	}
	
	public boolean isErrorNow() {
		return preferences.getBoolean(ERROR_NOW, false);
	}
	
	public synchronized void setErrorNow(boolean error) {
		this.editor.putBoolean(ERROR_NOW, error);
	}
}
