package com.group11.logic;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.group11.base.Mode;
import com.group11.hardware.Beeper;
import com.group11.hardware.CurrentStatus;
import com.group11.ui.DateArea;
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

	private HistoryManager historyManager = new HistoryManager(context);
	private TimerTask timerTask;

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
		 CurrentStatus currentStatus = new CurrentStatus(preferences);
		 Beeper.get().doShortLongBeep(context); 
		 statusArea.setVisible(false);
		 dateArea.setVisible(false);
		 currentStatus.setCurrentMode(null);
		 currentStatus.setUSBConnected(false);
		 currentStatus.setPowerOn(false);
		 currentStatus.commit();
	}
	
	public void showBlinkingView(){
		statusArea.setUploadingBlinking(true);
	}
	
	public void onUsbConnected() {
		Log.i("NULLLLL", "onConnected");
		
		PowerOn();
		if (historyManager.getTestResults().size() == 0) {
			// showBlinkingView();
			// new Timer().schedule(timerTask, 10000);
		} else {
			Beeper.get().doShortBeep(context);
			// wait for shortclick
			historyManager.deleteAllTestResults();
		}
		showBlinkingView();
		timerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				PowerOff();
			}
		};
		new Timer().schedule(timerTask, 10000);
	}
	
	public void onUsbDisConnected() {
		Log.i("NULLLLL", "onDisConnected");

		if (timerTask.cancel()) {
			Log.e("CANCEL", "success");
		}
		else {
			Log.e("CANCEL", "failed");			
		}
//		timer.cancel();
		PowerOff();
	}


}
