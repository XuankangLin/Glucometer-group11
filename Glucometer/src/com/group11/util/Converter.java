package com.group11.util;

import com.group11.base.Unit;

/**
 * unit conversion between L & dL 
 */
public final class Converter {

	private static double toDL(double l) {
		return l * 18;
	}
	
	private static double toL(double dl) {
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
	
	/**
	 * @param value
	 * @return XY.Z:  [0]-X, [1]-Y, [2]-Z
	 */
	public static int[] toLNumbers(double value) {
		int[] numbers = new int[3];
		
		int v = (int) value;
		numbers[1] = v % 10;
		v /= 10;
		numbers[0] = v % 10;
		
		value *= 10;
		numbers[2] = (int) (value % 10);
		
		return numbers;
	}

	/**
	 * @param value
	 * @return XYZ:  [0]-X, [1]-Y, [2]-Z
	 */
	public static int[] toDLNumbers(double value) {
		int[] numbers = new int[3];
		
		numbers[2] = (int) (value % 10);
		value /= 10;
		numbers[1] = (int) (value % 10);
		value /= 10;
		numbers[0] = (int) (value % 10);

		return numbers;
	}
	
	/**
	 * @param code
	 * @return XYZ: [0]-X, [1]-Y, [2]-Z
	 */
	public static int[] toErrorCodeNumbers(int code) {
		int[] numbers = new int[3];
		
		numbers[2] = code % 10;
		code /= 10;
		numbers[1] = code % 10;
		code /= 10;
		numbers[0] = code % 10;

		return numbers;
	}
}
