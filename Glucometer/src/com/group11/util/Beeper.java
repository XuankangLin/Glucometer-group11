package com.group11.util;

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
}
