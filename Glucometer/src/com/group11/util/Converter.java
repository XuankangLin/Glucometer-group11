package com.group11.util;

import com.group11.util.TestResult.Unit;

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
	
	public static double to(double value, Unit fromUnit, Unit toUnit) {
		if (fromUnit == Unit.L && toUnit == Unit.DL) {
			return toDL(value);
		}
		
		if (fromUnit == Unit.DL && toUnit == Unit.L) {
			return toL(value);
		}
		
		return value;
	}
}
