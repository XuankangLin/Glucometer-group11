package com.group11.hardware;

/**
 * stores the state of this glucometer
 * &&
 * manage the history records 
 */
public class CurrentStatus {

	private static CurrentStatus instance = null;
	public static CurrentStatus get() {
		if (instance == null) {
			instance = new CurrentStatus();
		}
		instance.restoreFromDB();
		return instance;
	}
	
	private CurrentStatus() {
		//TODO get status from DATASTORE
	}

	/**
	 * restore the status from Database
	 */
	private void restoreFromDB() {
		//TODO
	}
	
	private boolean powerOn = false;
	
	public boolean isPowerOn() {
		return powerOn;
	}

	public void setPowerOn(boolean powerOn) {
		this.powerOn = powerOn;
	}
}
