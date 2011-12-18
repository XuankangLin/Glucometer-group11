package com.group11.logic;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import com.group11.base.Interrupt;
import com.group11.base.Mode;
import com.group11.base.Unit;
import com.group11.hardware.CurrentStatus;
import com.group11.ui.DateArea;
import com.group11.ui.ProgressBarArea;
import com.group11.ui.ResultArea;
import com.group11.ui.StatusArea;
import com.group11.util.TimeFormatter;

/**
 * the logical controller in Setup Mode, it judges what to do
 */
public class SetupModeLogic extends ModeLogic {
	
	private static final int AUTO_ENDING_TIME = 10000;
	
	public static enum SetupPosition {
		UNIT,
		MONTH1, MONTH2,
		DAY1, DAY2,
		YEAR1, YEAR2,
		HOUR1, HOUR2,
		MINUTE1,	MINUTE2;
	}
	
	public static class SetupData {
		public Unit unit;
		public String month1, month2;
		public String day1, day2;
		public String year1, year2;
		public String hour1, hour2;
		public String minute1, minute2;
		
		public int getMonth() {
			return Integer.valueOf(month1 + month2);
		}
		
		public int getDay() {
			return Integer.valueOf(day1 + day2);
		}
		
		public int getYear() {
			int year = Integer.valueOf(year1 + year2) + 2000;
			if (year > 2012) {
				year -= 100;
			}
			return year;
		}
		
		public int getHour() {
			return Integer.valueOf(hour1 + hour2);
		}
		
		public int getMinute() {
			return Integer.valueOf(minute1 + minute2);
		}
	}

	public SetupModeLogic(StatusArea status, ResultArea result,
			ProgressBarArea progressBar, DateArea date, Context context,
			SharedPreferences preferences, Handler handler) {
		super(status, result, progressBar, date, context, preferences, handler);
	}

	private SetupPosition currentPosition = null;
	private final SetupData data = new SetupData();

	private TimerTask autoEndingTask = null;
	
	/**
	 * restart (postpone to now) the auto-ending time counting
	 */
	private void restartAutoEnding() {
		if (autoEndingTask != null) {
			autoEndingTask.cancel();
			autoEndingTask = null;
		}
		autoEndingTask = new TimerTask() {
			
			@Override
			public void run() {
				Log.i("TESTING", "auto ending in SETUP MODE");
				Message message = Message.obtain(handler,
						Interrupt.VOLUNTARY_ENDING.ordinal());
				message.sendToTarget();
			}
		};
		new Timer().schedule(autoEndingTask, AUTO_ENDING_TIME);
	}
	
	/**
	 * Setup mode doesn't have Mode Validation Process
	 */
	@Override
	public void validateMode() {
		//=====do nothing=====
	}
	
	/**
	 * the initial view of the Setup Mode
	 */
	public void initDisplay() {
		this.initStatus();
		this.initSetupData();
		this.restartAutoEnding();
		
		statusArea.setVisible(true);
		statusArea.setErroring(false);
		statusArea.cancelBlinking();
		statusArea.setCurrentMode(null);
		
		resultArea.setVisible(true);
		resultArea.setOnlyDisplayUnit();
		resultArea.setUnitBlinking(true);

		progressBarArea.setVisible(false);

		dateArea.setVisible(true);
		dateArea.setColonBlinking(false);
	}
	
	private void initStatus() {
		CurrentStatus status = new CurrentStatus(preferences);
		status.setPowerOn(true);
		status.setCurrentMode(Mode.SETUP);
		status.setErrorNow(false);
		status.setRefreshingTime(false);
		status.commit();
		
		this.currentPosition = SetupPosition.UNIT;
	}
	
	private void initSetupData() {
		CurrentStatus status = new CurrentStatus(preferences);
		data.unit = status.getCurrentUnit();

		Date currentTime = status.getCurrentTime();
		Pair<String, String> pair;
		{
			pair = TimeFormatter.formatMonth(currentTime);
			data.month1 = pair.first;
			data.month2 = pair.second;
		} {
			pair = TimeFormatter.formatDay(currentTime);
			data.day1 = pair.first;
			data.day2 = pair.second;
		} {
			pair = TimeFormatter.formatYear(currentTime);
			data.year1 = pair.first;
			data.year2 = pair.second;
		} {
			pair = TimeFormatter.formatHour(currentTime);
			data.hour1 = pair.first;
			data.hour2 = pair.second;
		} {
			pair = TimeFormatter.formatMinute(currentTime);
			data.minute1 = pair.first;
			data.minute2 = pair.second;
		}
	}
	
	/**
	 * @param number
	 * @param limit
	 * @return a number + 1 if the number is less than limit otherwise 0
	 */
	private String plusOne(String number, int limit) {
		int value = Integer.valueOf(number);
		if (value >= limit) {
			value = 0;
		}
		else {
			value++;
		}
		return Integer.toString(value);
	}
	
	private void toNextData() {
		if (currentPosition == null) {
			return;
		}
		
		switch (currentPosition) {
		case UNIT: {
			//=====L / dL=====
			if (data.unit == Unit.L) {
				data.unit = Unit.DL;
			}
			else {
				data.unit = Unit.L;
			}
			resultArea.setUnit(data.unit);				
			break;
		}
		case MONTH1: {
			//=====0 ~ 1=====
			data.month1 = this.plusOne(data.month1, 1);
			dateArea.setText(SetupPosition.MONTH1, data.month1);				
			break;
		}
		case MONTH2: {
			//=====0 ~ 9=====
			data.month2 = this.plusOne(data.month2, 9);
			dateArea.setText(SetupPosition.MONTH2, data.month2);
			break;
		}
		case DAY1: {
			//=====0 ~ 3=====
			data.day1 = this.plusOne(data.day1, 3);
			dateArea.setText(SetupPosition.DAY1, data.day1);
			break;			
		}
		case DAY2: {
			//=====0 ~ 9 for DAY1 is 0,1,2=====
			//=====0 ~ 1 for DAY1 is 3=====
			int limit = data.day1.equals("3") ? 1 : 9;
			data.day2 = this.plusOne(data.day2, limit);
			dateArea.setText(SetupPosition.DAY2, data.day2);
			break;
		}
		case YEAR1: {
			//=====0 ~ 9=====
			data.year1 = this.plusOne(data.year1, 9);
			dateArea.setText(SetupPosition.YEAR1, data.year1);
			break;
		}
		case YEAR2: {
			//=====0 ~ 9=====
			data.year2 = this.plusOne(data.year2, 9);
			dateArea.setText(SetupPosition.YEAR2, data.year2);
			break;
		}
		case HOUR1: {
			//=====0 ~ 2=====
			data.hour1 = this.plusOne(data.hour1, 2);
			dateArea.setText(SetupPosition.HOUR1, data.hour1);
			break;
		}
		case HOUR2: {
			//=====0 ~ 9=====
			data.hour2 = this.plusOne(data.hour2, 9);
			dateArea.setText(SetupPosition.HOUR2, data.hour2);
			break;
		}
		case MINUTE1: {
			//=====0 ~ 5=====
			data.minute1 = this.plusOne(data.minute1, 5);
			dateArea.setText(SetupPosition.MINUTE1, data.minute1);
			break;
		}
		case MINUTE2: {
			//=====0 ~ 9=====
			data.minute2 = this.plusOne(data.minute2, 9);
			dateArea.setText(SetupPosition.MINUTE2, data.minute2);
			break;
		}

		default:
			break;
		}
	}
	
	private boolean checkMonth() {
		try {
			int month = data.getMonth();
			return month >= 1 && month <= 12;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean checkDay() {
		try {
			int day = data.getDay();
			int month = data.getMonth();
			int year = data.getYear();
			Calendar calendar = new GregorianCalendar(year, month-1, day);
			return calendar.get(Calendar.YEAR) == year
					&& calendar.get(Calendar.MONTH) + 1 == month
					&& calendar.get(Calendar.DAY_OF_MONTH) == day;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean checkHour() {
		try {
			int hour = data.getHour();
			return hour >= 0 && hour <= 23;
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean checkMinute() {
		try {
			int minute = data.getMinute();
			return minute >= 0 && minute <= 59;
		} catch (Exception e) {
			return false;
		}
	}
	
	private void toNextItem() {
		if (currentPosition == null) {
			return;
		}

		switch (currentPosition) {
		case UNIT: {
			currentPosition = SetupPosition.MONTH1;
			resultArea.setUnitBlinking(false);
			dateArea.setTextBlinking(currentPosition);
			break;			
		}
		case MONTH1: {
			currentPosition = SetupPosition.MONTH2;
			dateArea.setTextBlinking(currentPosition);
			break;
		}
		case MONTH2: {
			currentPosition = this.checkMonth() ? SetupPosition.DAY1
					: SetupPosition.MONTH1;
			dateArea.setTextBlinking(currentPosition);				
			break;			
		}
		case DAY1: {
			currentPosition = SetupPosition.DAY2;
			dateArea.setTextBlinking(currentPosition);
			break;
		}
		case DAY2: {
			currentPosition = this.checkDay() ? SetupPosition.YEAR1
					: SetupPosition.DAY1;
			dateArea.setTextBlinking(currentPosition);				
			break;			
		}
		case YEAR1: {
			currentPosition = SetupPosition.YEAR2;
			dateArea.setTextBlinking(currentPosition);
			break;			
		}
		case YEAR2: {
			currentPosition = this.checkDay() ? SetupPosition.HOUR1
					: SetupPosition.DAY1;
			dateArea.setTextBlinking(currentPosition);
			break;			
		}
		case HOUR1: {
			currentPosition = SetupPosition.HOUR2;
			dateArea.setTextBlinking(currentPosition);
			break;			
		}
		case HOUR2: {
			currentPosition = this.checkHour() ? SetupPosition.MINUTE1
					: SetupPosition.HOUR1;
			dateArea.setTextBlinking(currentPosition);
			break;			
		}
		case MINUTE1: {
			currentPosition = SetupPosition.MINUTE2;
			dateArea.setTextBlinking(currentPosition);
			break;			
		}
		case MINUTE2: {
			if (this.checkMinute()) {
				currentPosition = null;
				dateArea.cancelTextBlinking();
				this.saveAndExit();
			}
			else {
				currentPosition = SetupPosition.MINUTE1;
				dateArea.setTextBlinking(currentPosition);
			}
			break;
		}

		default:
			break;
		}
	}
	
	private void saveAndExit() {
		//=====save changes=====
		CurrentStatus status = new CurrentStatus(preferences);
		status.setCurrentUnit(data.unit);
		Calendar calendar = new GregorianCalendar(data.getYear(),
				data.getMonth() - 1, data.getDay(), data.getHour(),
				data.getMinute());
		status.setCurrentTime(calendar.getTimeInMillis());
		status.commit();
		
		//=====voluntary ending=====
		Message message = Message.obtain(handler, Interrupt.VOLUNTARY_ENDING.ordinal());
		message.sendToTarget();
	}

	@Override
	public void onShortClick() {
		this.restartAutoEnding();
		this.toNextData();
	}

	@Override
	public void onLongClick() {
		//=====Undefined Action, ignored=====
	}

	@Override
	public void onDoubleClick() {
		this.restartAutoEnding();
		this.toNextItem();
	}

	@Override
	public void onStripInserted() {
		//=====Undefined Action, ignored=====
	}

	@Override
	public void onStripPulledOut() {
		//=====Undefined Action, ignored=====
	}

	@Override
	public void onUSBConnected() {
		//=====Undefined Action, ignored=====
	}

	@Override
	public void onUSBDisconnected() {
		//=====Undefined Action, ignored=====
	}

}
