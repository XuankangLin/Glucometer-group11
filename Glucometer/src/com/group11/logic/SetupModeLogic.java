package com.group11.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.group11.ui.DateArea;
import com.group11.ui.ProgressBarArea;
import com.group11.ui.ResultArea;
import com.group11.ui.StatusArea;

/**
 * the logical controller in Setup Mode, it judges what to do
 */
public class SetupModeLogic extends ModeLogic {

	public SetupModeLogic(StatusArea status, ResultArea result,
			ProgressBarArea progressBar, DateArea date, Context context,
			SharedPreferences preferences, Handler handler) {
		super(status, result, progressBar, date, context, preferences, handler);
	}

	/**
	 * Setup mode doesn't have Mode Validation Process
	 */
	@Override
	public void validateMode() {
		//=====do nothing=====
		
		this.initDisplay();
	}
	
	private void initDisplay() {
		statusArea.setVisible(true);
		statusArea.setErroring(false);
		statusArea.cancelBlinking();
		statusArea.setCurrentMode(null);
		
		resultArea.setVisible(true);
		resultArea.setOnlyDisplayUnit();
		resultArea.setUnitBlinking(true);

		progressBarArea.setVisible(false);

		dateArea.setVisible(true);
		dateArea.setColonBlinking(true);
	}

	@Override
	public void onShortClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLongClick() {
		//=====Undefined Action, ignored=====
	}

	@Override
	public void onDoubleClick() {
		// TODO Auto-generated method stub

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
