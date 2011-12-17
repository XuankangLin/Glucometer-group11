package com.group11.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.group11.base.Mode;
import com.group11.hardware.Beeper;
import com.group11.hardware.CurrentStatus;
import com.group11.ui.DateArea;
import com.group11.ui.GlucometerActivity;
import com.group11.ui.ProgressBarArea;
import com.group11.ui.ResultArea;
import com.group11.ui.StatusArea;
import com.group11.util.HistoryManager;

/**
 * the logical controller in Uploading Mode, it judges what to do
 */
public class UploadingModeLogic extends ModeLogic {

	public UploadingModeLogic(StatusArea status, ResultArea result,
			ProgressBarArea progressBar, DateArea date, Context context,
			SharedPreferences preferences, Handler handler) {
		super(status, result, progressBar, date, context, preferences, handler);
	}

	@Override
	public boolean validateMode() {
		// TODO Auto-generated method stub
		return super.validateMode();
	}

	@Override
	public void onShortClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLongClick() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDoubleClick() {
		// TODO Auto-generated method stub

	}
	
	
	public void PowerOn(){
		initialize();
		checkMeterStatus();
		validateMode();
		CurrentStatus currentStatus = new CurrentStatus(preferences);
		currentStatus.setPowerOn(true);
		currentStatus.setCurrentMode(Mode.UPLOADING);
		currentStatus.setUSBConnected(true);
		currentStatus.commit();
		statusArea.setCurrentMode(Mode.UPLOADING);
		statusArea.setVisible(true);
		dateArea.setVisible(true);
	}
	
	public void PowerOff(){
		 statusArea.setVisible(false);
		 dateArea.setVisible(false);
		 CurrentStatus currentStatus = new CurrentStatus(preferences);
		 currentStatus.setCurrentMode(null);
		 currentStatus.setUSBConnected(false);
		 currentStatus.setPowerOn(false);
		 currentStatus.commit();
	}
	
	public void showBlinkingView(){
		statusArea.setUploadingBlinking(true);
		//statusArea.setVisible(false);
		dateArea.setVisible(false);
	}

}
