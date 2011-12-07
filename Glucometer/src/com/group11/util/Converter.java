package com.group11.util;

/**
 * unit conversion between L & dL 
 */
public final class Converter {

	public static double toDL(double l) {
		return l * 18;
	}
	
	public static double toL(double dl) {
		return dl / 18;
	}
}
