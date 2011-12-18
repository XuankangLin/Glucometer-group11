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
	private static final String BLOOD_FED = "bloodFed";

	//=====should be set by Logic Controller itself=====
	private static final String CURRENT_MODE = "currentMode";
	private static final String LAST_MODE_COMPLETE = "lastModeComplete";

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
	private static final String PC_READY = "pcReady";
	private static final String SOFTWARE_READY = "softwareReady";
	private static final String INITIALIZATION_ERROR = "initializationError";
	private static final String STRIP_VALID = "stripValid";
	private static final String BLOOD_SUFFICIENT = "bloodSufficient";
	private static final String RESULT_TIMEOUT = "resultTimeout";

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
		preferences.edit().putLong(TIME_INTERVAL, new GregorianCalendar().getTimeInMillis() - time).commit();
	}

	/**
	 * since the nextSecond() won't be called when the glucometer is off,
	 * we store the interval between the time set and the real time.
	 * this time when the glucometer powers on, reinstate the time according to new real time.
	 * 
	 * should be called every time the glucometer is powered on
	 */
	public synchronized void syncCurrentTime() {
		long interval = preferences.getLong(TIME_INTERVAL, 0);
		preferences.edit().putLong(CURRENT_TIME, new GregorianCalendar().getTimeInMillis() - interval).commit();
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

	public boolean isLastModeComplete() {
		return preferences.getBoolean(LAST_MODE_COMPLETE, true);
	}
	
	public synchronized void setLastModeComplete(boolean complete) {
		this.editor.putBoolean(LAST_MODE_COMPLETE, complete);
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
	
	public boolean isPCReady() {
		return preferences.getBoolean(PC_READY, true);
	}
	
	public synchronized void setPCReady(boolean ready) {
		this.editor.putBoolean(PC_READY, ready);
	}
	
	public boolean isStripValid() {
		return preferences.getBoolean(STRIP_VALID, true);
	}
	
	public synchronized void setStripValid(boolean valid) {
		this.editor.putBoolean(STRIP_VALID, valid);
	}
	
	public boolean isBloodSufficient() {
		return preferences.getBoolean(BLOOD_SUFFICIENT, true);
	}
	
	public synchronized void setBloodSufficient(boolean sufficient) {
		this.editor.putBoolean(BLOOD_SUFFICIENT, sufficient);
	}
	
	public boolean isBloodFed() {
		return preferences.getBoolean(BLOOD_FED, false);
	}
	
	public synchronized void setBloodFed(boolean fed) {
		this.editor.putBoolean(BLOOD_FED, fed);
	}
	
	public boolean isSoftwareReady() {
		return preferences.getBoolean(SOFTWARE_READY, true);
	}
	
	public void setSoftwareReady(boolean ready) {
		this.editor.putBoolean(SOFTWARE_READY, ready);
	}
	
	public boolean isResultTimeout() {
		return preferences.getBoolean(RESULT_TIMEOUT, false);
	}
	
	public synchronized void setResultTimeout(boolean timeout) {
		this.editor.putBoolean(RESULT_TIMEOUT, timeout);
	}
}
