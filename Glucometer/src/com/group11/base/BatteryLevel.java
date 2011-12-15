package com.group11.base;

public enum BatteryLevel {
	ZERO_PERCENT,
	TWENTY_FIVE_PERCENT,
	FIFTY_PERCENT,
	SEVENTY_FIVE_PERCENT,
	HUNDRED_PERCENT;
	
	/**
	 * dispatch by its real value
	 * @param value
	 * @return
	 */
	public static BatteryLevel getByValue(int value) {
		value %= 101;
		if (value >= 100) {
			return HUNDRED_PERCENT;
		}
		else if (value >= 75) {
			return SEVENTY_FIVE_PERCENT;
		}
		else if (value >= 50) {
			return FIFTY_PERCENT;
		}
		else if (value >= 25) {
			return TWENTY_FIVE_PERCENT;
		}
		else {
			return ZERO_PERCENT;
		}
	}
}
