package com.group11.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.group11.hardware.CurrentStatus;
import com.group11.ui.DateArea;
import com.group11.ui.ProgressBarArea;
import com.group11.ui.ResultArea;
import com.group11.ui.StatusArea;

/**
 * a abstract class standardizing the interface of logical controllers
 */
public abstract class ModeLogic {
	
	private static final int INITIALIZATION_TIME = 300;
	private static final int VALIDATION_TIME = 200;

	protected final StatusArea statusArea;
	protected final ResultArea resultArea;
	protected final ProgressBarArea progressBarArea;
	protected final DateArea dateArea;

	protected final Context context;
	protected final SharedPreferences preferences;
	protected final Handler handler;

	public ModeLogic(StatusArea status, ResultArea result,
			ProgressBarArea progressBar, DateArea date, Context context,
			SharedPreferences preferences, Handler handler) {
		this.statusArea = status;
		this.resultArea = result;
		this.progressBarArea = progressBar;
		this.dateArea = date;
		this.context = context;
		this.preferences = preferences;
		this.handler = handler;
	}
	
	/**
	 * the Initialization Process
	 * @return whether successful
	 */
	public boolean initialize() {
		try {
			Thread.sleep(INITIALIZATION_TIME);
		} catch (InterruptedException e) {
		}

		return new CurrentStatus(preferences).isInitializationErrorNextTime() == false;
	}
	
	/**
	 * 	the Meter Status Check process
	 * @return whether successful
	 */
	public boolean checkMeterStatus() {
		CurrentStatus status = new CurrentStatus(preferences);
		if (status.getPreviousMode() == null) {
			return true;
		}
		
		switch (status.getPreviousMode()) {
		case BROWSING:
		case SETUP:
			return !status.isPowerOn();
		case TESTING:
			return !status.isStripInserted() && !status.isPowerOn();
		case UPLOADING:
			return !status.isUSBConnected() && !status.isPowerOn();

		default:
			return true;
		}
	}
	
	/**
	 * the Mode Validation Process
	 * @return whether successful
	 */
	public boolean validateMode() {
		try {
			Thread.sleep(VALIDATION_TIME);
		} catch (InterruptedException e) {
		}

		return true;
	}
	
	/**
	 * when short-clicked, what should be done?
	 */
	public abstract void onShortClick();

	/**
	 * when long-clicked, what should be done?
	 */
	public abstract void onLongClick();

	/**
	 * when double-clicked, what should be done?
	 */
	public abstract void onDoubleClick();
}
