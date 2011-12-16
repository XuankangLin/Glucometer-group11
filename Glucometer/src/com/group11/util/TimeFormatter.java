package com.group11.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Pair;

/**
 * formats date into month/day/year
 * formats time into hour:minute 
 */
public class TimeFormatter {
	
	/**
	 * @param date
	 * @return MonthMonth string. e.g. 12
	 */
	public static Pair<String, String> formatMonth(Date date) {
		if (date == null) {
			return null;
		}
		
		return divide(new SimpleDateFormat("MM").format(date));
	}
	
	/**
	 * @param date
	 * @return DayDay string. e.g. 05
	 */
	public static Pair<String, String> formatDay(Date date) {
		if (date == null) {
			return null;
		}

		return divide(new SimpleDateFormat("dd").format(date));
	}

	/**
	 * @param date
	 * @return YearYear string. e.g. 11
	 */
	public static Pair<String, String> formatYear(Date date) {
		if (date == null) {
			return null;
		}
		
		return divide(new SimpleDateFormat("yy").format(date));		
	}
	
	/**
	 * @param date
	 * @return HourHour string. e.g. 23
	 */
	public static Pair<String, String> formatHour(Date date) {
		if (date == null) {
			return null;
		}
		
		return divide(new SimpleDateFormat("HH").format(date));			
	}
	
	/**
	 * @param date
	 * @return MinuteMinute string. e.g. 57
	 */
	public static Pair<String, String> formatMinute(Date date) {
		if (date == null) {
			return null;
		}
		
		return divide(new SimpleDateFormat("mm").format(date));		
	}

	/**
	 * divide a string into 2 strings
	 * containing its 1st digit & 2nd digit respectively
	 * @param string
	 * @return
	 */
	private static Pair<String, String> divide(String string) {
		return new Pair<String, String>(new String(string.substring(0, 1)),
				new String(string.substring(1, 2)));
	}
}
