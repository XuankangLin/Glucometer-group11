package com.group11.logic;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
	
	private boolean BUTTONHASCLICKED = false;
	private HistoryManager historyManager = new HistoryManager(context);
	private TimerTask autoEndingTask = null;
	private static final int AUTO_ENDING_TIME = 10000;
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
				Log.i("TESTING", "auto ending in UploadingMODE");
				stopUploading();
				Message message = Message.obtain(handler,
						Interrupt.VOLUNTARY_ENDING.ordinal());
				message.sendToTarget();
			}
		};
		new Timer().schedule(autoEndingTask, AUTO_ENDING_TIME);
	}

	@Override
	public void validateMode() {
		super.validateMode();
		CurrentStatus currentStatus = new CurrentStatus(preferences);
		if (historyManager.getTestResults().size() == 0||currentStatus.isPCReady()||currentStatus.isSoftwareReady()){
			showBlinkingView();
			BUTTONHASCLICKED = true;
			this.restartAutoEnding();
		}
	}

	@Override
	public void onShortClick() {
		// TODO Auto-generated method stub
		if(!BUTTONHASCLICKED)
		{
			Beeper.get().doRemindBeep(context);
			historyManager.deleteAllTestResults();
			showBlinkingView();
			this.restartAutoEnding();
		}
		BUTTONHASCLICKED = true;
		
	}

	@Override
	public void onLongClick() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDoubleClick() {
		// TODO Auto-generated method stub

	}
	
	
	public void startUploading(){
		CurrentStatus currentStatus = new CurrentStatus(preferences);
		currentStatus.setPowerOn(true);
		currentStatus.setCurrentMode(Mode.UPLOADING);
		currentStatus.setUSBConnected(true);
		currentStatus.commit();
		statusArea.setCurrentMode(Mode.UPLOADING);
		statusArea.setVisible(true);
		dateArea.setVisible(true);
		BUTTONHASCLICKED = false;
		this.restartAutoEnding();
	}
	
	public void stopUploading(){
		 CurrentStatus currentStatus = new CurrentStatus(preferences);
		 Beeper.get().doErrorBeep(context);
		 statusArea.setVisible(false);
		 dateArea.setVisible(false);
		 currentStatus.setUSBConnected(false);
//		 currentStatus.setCurrentMode(null); do not set currentMode(null) here!!!!!
		 currentStatus.commit();
		 Message message = Message.obtain(handler, Interrupt.VOLUNTARY_ENDING.ordinal());
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
		startUploading();
		}

	@Override
	public void onUSBDisconnected() {
		autoEndingTask.cancel();
		stopUploading();
	}
	

}
