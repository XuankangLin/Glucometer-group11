package com.group11.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * formats date into month/day/year
 * formats time into hour:minute 
 */
public class TimeFormatter {

	/**
	 * @param date
	 * @return a month/day/year string
	 */
	public static String formatDate(Date date) {
		if (date == null) {
			return null;
		}
		
		return new SimpleDateFormat("MM/dd/yy").format(date);
	}

	/**
	 * @param time
	 * @return a hour:minute
	 */
	public static String formatTime(Date time) {
		if (time == null) {
			return null;
		}
		
		return new SimpleDateFormat("hh:mm").format(time);
	}
}
