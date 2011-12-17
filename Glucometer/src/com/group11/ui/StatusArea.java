package com.group11.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.group11.R;
import com.group11.base.BatteryLevel;
import com.group11.base.Mode;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class StatusArea extends UIArea {
	
	/**
	 * for doing periodically operations on UI thread
	 */
	private final Activity activity;

	private final ImageView batteryImage;
	private final ImageView acImage;
	private final ImageView testingModeImage;
	private final ImageView browsingModeImage;
	private final ImageView uploadingModeImage;
	private final ImageView errorModeImage;
	
	private TimerTask batteryBlinkingTask = null;
	private TimerTask uploadingBlinkingTask = null;

	public StatusArea(Activity activity, LinearLayout panel, ImageView battery, ImageView ac,
			ImageView tMode, ImageView bMode, ImageView uMode, ImageView eMode) {
		super(panel);
		
		this.activity = activity;
		
		this.batteryImage = battery;
		this.acImage = ac;
		this.testingModeImage = tMode;
		this.browsingModeImage = bMode;
		this.uploadingModeImage = uMode;
		this.errorModeImage = eMode;
	}
	
	/**
	 * adjust the status according to current mode @param mode
	 * @param mode
	 */
	public void setCurrentMode(Mode mode) {
		switch (mode) {
		case TESTING: {
			this.testingModeImage.setVisibility(View.VISIBLE);
			this.browsingModeImage.setVisibility(View.INVISIBLE);
			this.uploadingModeImage.setVisibility(View.INVISIBLE);
			return;
		}
		case BROWSING: {
			this.testingModeImage.setVisibility(View.INVISIBLE);
			this.browsingModeImage.setVisibility(View.VISIBLE);
			this.uploadingModeImage.setVisibility(View.INVISIBLE);
			return;
		}
		case UPLOADING: {
			this.testingModeImage.setVisibility(View.INVISIBLE);
			this.browsingModeImage.setVisibility(View.INVISIBLE);
			this.uploadingModeImage.setVisibility(View.VISIBLE);
			return;
		}
		case SETUP: {
			this.testingModeImage.setVisibility(View.INVISIBLE);
			this.browsingModeImage.setVisibility(View.INVISIBLE);
			this.uploadingModeImage.setVisibility(View.INVISIBLE);
			return;
		}

		default:
			break;
		}
	}

	/**
	 * if @param error is true, the Error Image shall be visible
	 */
	public void setErroring(boolean error) {
		if (error) {
			this.errorModeImage.setVisibility(View.VISIBLE);
		}
		else {
			this.errorModeImage.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * if @param ing, the ac image shall be visible
	 */
	public void setACing(boolean ing) {
		if (ing) {
			this.acImage.setVisibility(View.VISIBLE);
		}
		else {
			this.acImage.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * according to @param level, set the battery image
	 */
	public void setBatteryLevel(BatteryLevel level) {
		switch (level) {
		case ZERO_PERCENT:
			this.batteryImage.setImageResource(R.drawable.battery_0percent);
			return;
		case TWENTY_FIVE_PERCENT:
			this.batteryImage.setImageResource(R.drawable.battery_25percent);
			return;
		case FIFTY_PERCENT:
			this.batteryImage.setImageResource(R.drawable.battery_50percent);
			return;
		case SEVENTY_FIVE_PERCENT:
			this.batteryImage.setImageResource(R.drawable.battery_75percent);
			return;
		case HUNDRED_PERCENT:
			this.batteryImage.setImageResource(R.drawable.battery_100percent);
			return;

		default:
			break;
		}
	}
	
	private void initBatteryBlinkingTask() {
		batteryBlinkingTask = new TimerTask() {
			
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if (batteryImage.getVisibility() == View.VISIBLE) {
							batteryImage.setVisibility(View.INVISIBLE);
						} 
						else {
							batteryImage.setVisibility(View.VISIBLE);
						}
					}
				});
			}
		};
	}
	
	private void clearBatteryBlinkingTask() {
		if (batteryBlinkingTask != null) {
			batteryBlinkingTask.cancel();
			batteryBlinkingTask = null;
		}		
	}
	
	public void setBatteryBlinking(boolean blinking) {
		this.clearBatteryBlinkingTask();
		if (blinking) {
			this.initBatteryBlinkingTask();
			new Timer().scheduleAtFixedRate(batteryBlinkingTask, 0, 1000);
		}
		else {
			this.batteryImage.setVisibility(View.VISIBLE);
		}
	}
	private void initUploadingBlinkingTask() {
		uploadingBlinkingTask = new TimerTask() {
			
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if (uploadingModeImage.getVisibility() == View.VISIBLE) {
							uploadingModeImage.setVisibility(View.INVISIBLE);
						} 
						else {
							uploadingModeImage.setVisibility(View.VISIBLE);
						}
					}
				});
			}
		};
	}
	
	private void clearUploadingBlinkingTask() {
		if (uploadingBlinkingTask != null) {
			uploadingBlinkingTask.cancel();
			uploadingBlinkingTask = null;
		}		
	}
	
	public void setUploadingBlinking(boolean blinking) {
		this.clearUploadingBlinkingTask();
		if (blinking) {
			this.initUploadingBlinkingTask();
			new Timer().scheduleAtFixedRate(uploadingBlinkingTask, 0, 1000);
		}
		else {
			this.uploadingModeImage.setVisibility(View.VISIBLE);
		}
	}
}
