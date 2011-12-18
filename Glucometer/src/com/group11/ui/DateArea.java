package com.group11.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.group11.logic.SetupModeLogic.SetupPosition;
import com.group11.util.TimeFormatter;

import android.app.Activity;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DateArea extends UIArea {

	/**
	 * for doing periodically operations on UI thread
	 */
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
	
	private TextView textBlinkingImage = null;
	private TimerTask textBlinkingTask = null;
	
	public DateArea(Activity activity, LinearLayout panel, TextView month1, TextView month2,
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
	
	private void initColonBlinkingTask() {
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
	
	private void clearColonBlinkingTask() {
		if (colonBlinkingTask != null) {
			colonBlinkingTask.cancel();
			colonBlinkingTask = null;
		}
	}
	
	/**
	 * set if the colon is blinking
	 * @param blinking
	 */
	public void setColonBlinking(boolean blinking) {
		this.clearColonBlinkingTask();
		if (blinking) {
			this.initColonBlinkingTask();
			new Timer().scheduleAtFixedRate(colonBlinkingTask, 0, 1000);
		}
	}
	
	public void cancelTextBlinking() {
		if (textBlinkingImage != null) {
			textBlinkingImage.setVisibility(View.VISIBLE);
			textBlinkingImage = null;
		}
		if (textBlinkingTask != null) {
			textBlinkingTask.cancel();
			textBlinkingTask = null;
		}
	}
	
	private void initTextBlinking() {
		textBlinkingTask = new TimerTask() {
			
			@Override
			public void run() {
				textBlinkingImage.post(new Runnable() {
					
					@Override
					public void run() {
						if (textBlinkingImage.getVisibility() == View.VISIBLE) {
							textBlinkingImage.setVisibility(View.INVISIBLE);
						}
						else {
							textBlinkingImage.setVisibility(View.VISIBLE);
						}
					}
				});
			}
		};
	}
	
	private void attachTextBlinkingImage(SetupPosition position) {
		switch (position) {
		case MONTH1:
			textBlinkingImage = this.monthText1;
			break;
		case MONTH2:
			textBlinkingImage = this.monthText2;
			break;
		case DAY1:
			textBlinkingImage = this.dayText1;
			break;
		case DAY2:
			textBlinkingImage = this.dayText2;
			break;
		case YEAR1:
			textBlinkingImage = this.yearText1;
			break;
		case YEAR2:
			textBlinkingImage = this.yearText2;
			break;
		case HOUR1:
			textBlinkingImage = this.hourText1;
			break;
		case HOUR2:
			textBlinkingImage = this.hourText2;
			break;
		case MINUTE1:
			textBlinkingImage = this.minuteText1;
			break;
		case MINUTE2:
			textBlinkingImage = this.minuteText2;
			break;

		default:
			textBlinkingImage = null;
			return;
		}
	}
	
	public void setTextBlinking(SetupPosition position) {
		this.cancelTextBlinking();
		
		if (position == null) {
			return;
		}
		this.attachTextBlinkingImage(position);
		this.initTextBlinking();
		new Timer().scheduleAtFixedRate(textBlinkingTask, 0, 1000);
	}

	public void setText(SetupPosition position, String value) {
		TextView text;
		switch (position) {
		case MONTH1:
			text = this.monthText1;
			break;
		case MONTH2:
			text = this.monthText2;
			break;
		case DAY1:
			text = this.dayText1;
			break;
		case DAY2:
			text = this.dayText2;
			break;
		case YEAR1:
			text = this.yearText1;
			break;
		case YEAR2:
			text = this.yearText2;
			break;
		case HOUR1:
			text = this.hourText1;
			break;
		case HOUR2:
			text = this.hourText2;
			break;
		case MINUTE1:
			text = this.minuteText1;
			break;
		case MINUTE2:
			text = this.minuteText2;
			break;

		default:
			return;
		}
		text.setText(value);
	}
}
