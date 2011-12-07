package com.group11.hardware;

/**
 * Beeper only controls the beeping sound.
 * it's a singleton which is invoked by outside
 */
public class Beeper {

	private static Beeper instance = null;
	public static Beeper get() {
		if (instance == null) {
			instance = new Beeper();
		}
		return instance;
	}
	
	private Beeper() {
		
	}
	
	private void stopBeeping() {
		//TODO every time it beeps, the previous beeping shall be stopped 
	}
	
	public void doShortBeep() {
		this.stopBeeping();
		//TODO
	}
	
	public void doLongBeep() {
		this.stopBeeping();
		//TODO
	}
	
	public void doDoubleBeep() {
		this.stopBeeping();
		//TODO
	}
	
	public void doShortLongBeep() {
		this.stopBeeping();
		//TODO
	}
}
