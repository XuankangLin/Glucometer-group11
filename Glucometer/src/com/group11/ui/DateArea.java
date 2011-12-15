package com.group11.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.group11.util.TimeFormatter;

import android.app.Activity;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DateArea extends UIArea {

	private final Activity activity;
	
	private final TextView monthText1;
	private final TextView monthText2;
	private final TextView dayText1;
	private final TextView dayText2;
	private final TextView yearText1;
	private final TextView yearText2;
	
	private final TextView hourText1;
	private final TextView hourText2;
	private final TextView colonText;
	private final TextView minuteText1;
	private final TextView minuteText2;
	
	private TimerTask colonBlinkingTask = null;
	
	public DateArea(LinearLayout panel, Activity activity, TextView month1, TextView month2,
			TextView day1, TextView day2, TextView year1, TextView year2,
			TextView hour1, TextView hour2, TextView colon, TextView minute1, TextView minute2) {
		super(panel);

		this.activity = activity;
		
		this.monthText1 = month1;
		this.monthText2 = month2;
		this.dayText1 = day1;
		this.dayText2 = day2;
		this.yearText1 = year1;
		this.yearText2 = year2;

		this.hourText1 = hour1;
		this.hourText2 = hour2;
		this.colonText = colon;
		this.minuteText1 = minute1;
		this.minuteText2 = minute2;
	}
	
	/**
	 * set the date & time text according to @param d
	 */
	public void setDateTime(Date d) {
		Pair<String, String> pair;
		
		pair = TimeFormatter.formatMonth(d);
		this.monthText1.setText(pair.first);
		this.monthText2.setText(pair.second);
		
		pair = TimeFormatter.formatDay(d);
		this.dayText1.setText(pair.first);
		this.dayText2.setText(pair.second);
		
		pair = TimeFormatter.formatYear(d);
		this.yearText1.setText(pair.first);
		this.yearText2.setText(pair.second);
		
		pair = TimeFormatter.formatHour(d);
		this.hourText1.setText(pair.first);
		this.hourText2.setText(pair.second);
		
		pair = TimeFormatter.formatMinute(d);
		this.minuteText1.setText(pair.first);
		this.minuteText2.setText(pair.second);
	}
	
	private void resetColonBlinkingTask() {
		colonBlinkingTask = new TimerTask() {

			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (colonText.getVisibility() == View.VISIBLE) {
							colonText.setVisibility(View.INVISIBLE);
						} 
						else {
							colonText.setVisibility(View.VISIBLE);
						}
					}
				});
			}
		};
	}

	/**
	 * set if the colon is blinking
	 * @param blinking
	 */
	public void setColonBlinking(boolean blinking) {
		if (blinking) {
			if (colonBlinkingTask != null) {
				colonBlinkingTask.cancel();
			}
			this.resetColonBlinkingTask();
			new Timer().schedule(colonBlinkingTask, 0, 1000);
		}
		else {
			if (colonBlinkingTask != null) {
				colonBlinkingTask.cancel();
				colonBlinkingTask = null;				
			}
		}
	}
}
