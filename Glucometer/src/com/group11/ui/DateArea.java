package com.group11.ui;

import java.util.Date;

import com.group11.util.TimeFormatter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DateArea {

	private final TextView dateText;
	private final TextView timeText;
	private final LinearLayout panel;
	
	public DateArea(TextView date, TextView time, LinearLayout panel) {
		this.dateText = date;
		this.timeText = time;
		this.panel = panel;
	}
	
	/**
	 * set the date & time text according to @param d
	 */
	public void setDateTime(Date d) {
		this.dateText.setText(TimeFormatter.formatDate(d));
	    this.timeText.setText(TimeFormatter.formatTime(d));
	}

	/**
	 * set the whole visibility according to @param visible
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			this.panel.setVisibility(View.VISIBLE);
		}
		else {
			this.panel.setVisibility(View.INVISIBLE);
		}
	}
}
