package com.group11.logic;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.group11.base.Interrupt;
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
	private Timer timer = new Timer();
	private TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			PowerOff();
		}
	};

	@Override
	public void validateMode() {
		// TODO Auto-generated method stub
		super.validateMode();
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
	
	
	public void StartUploading(){
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
		 Beeper.get().doErrorBeep(context);
		 statusArea.setVisible(false);
		 dateArea.setVisible(false);
		 currentStatus.setUSBConnected(false);
		 currentStatus.setCurrentMode(null);
		 currentStatus.commit();
		 Message message = Message.obtain(handler, Interrupt.POWER_OFF.ordinal());
			message.sendToTarget();
	}
	
	public void showBlinkingView(){
		statusArea.cancelBlinking();
		statusArea.setModeBlinking(Mode.UPLOADING);
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
		// TODO Auto-generated method stub
		StartUploading();
		if (historyManager.getTestResults().size() == 0) {
		} else {
			Beeper.get().doRemindBeep(context);
			// wait for shortclick
			historyManager.deleteAllTestResults();
		}
		showBlinkingView();
		//timer.schedule(timerTask, 5000);
	}

	@Override
	public void onUSBDisconnected() {
		// TODO Auto-generated method stub
		//timer.cancel();
		PowerOff();
	}


}
