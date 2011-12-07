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
		
	}
	
	private boolean poweredOn = false;
}
