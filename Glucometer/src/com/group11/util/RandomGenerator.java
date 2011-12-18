package com.group11.util;

import java.util.Random;

import com.group11.base.Unit;

public class RandomGenerator {

	public static double getRandomResult(Unit unit) {
		if (unit == null) {
			throw new IllegalArgumentException("@param unit should not be null");
		}
		
		double value = new Random().nextInt(1000);
		if (unit == Unit.L) {
			value /= 10;
		}
		return value;
	}
}
