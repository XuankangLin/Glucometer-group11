package com.group11.hardware;

/**
 * stores the state of this glucometer
 * &&
 * manage the history records 
 */
public class Glucometer {

	private static Glucometer instance = null;
	public static Glucometer get() {
		if (instance == null) {
			instance = new Glucometer();
		}
		return instance;
	}
	
	private Glucometer() {
		//TODO get status from DATASTORE
	}

	private ButtonClickJudger judger = new ButtonClickJudger();
	
	private boolean powerOn = false;
	
	public boolean isPowerOn() {
		return powerOn;
	}

	public void setPowerOn(boolean powerOn) {
		this.powerOn = powerOn;
	}
}
