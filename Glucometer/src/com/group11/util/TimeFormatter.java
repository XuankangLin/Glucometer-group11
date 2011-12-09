package com.group11.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * formats date into Month/day/year
 * formats time into hour:minute 
 */
public class TimeFormatter {

	public static String formatDate(Date date) {
		if (date == null) {
			return null;
		}
		
		return new SimpleDateFormat("MM/dd/yy").format(date);
	}
	
	public static String formatTime(Date time) {
		if (time == null) {
			return null;
		}
		
		return new SimpleDateFormat("hh:mm").format(time);
	}
}
