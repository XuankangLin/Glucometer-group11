package com.group11.logic;

import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.group11.base.ErrorCode;
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

	@Override
	protected void initAutoEndingTask() {
		autoEndingTask = new TimerTask() {
			
			@Override
			public void run() {
//				stopUploading();
				Message message = Message.obtain(handler,
						Interrupt.VOLUNTARY_ENDING.ordinal());
				message.sendToTarget();
			}
		};
	}

	@Override
	public void validateMode() {
		super.validateMode();

		CurrentStatus currentStatus = new CurrentStatus(preferences);
		currentStatus.setCurrentMode(Mode.UPLOADING);
		currentStatus.setRefreshingTime(true);
		currentStatus.commit();
		
		statusArea.setVisible(true);
		resultArea.setVisible(false);
		progressBarArea.setVisible(false);
		dateArea.setVisible(true);

		statusArea.setCurrentMode(Mode.UPLOADING);
		statusArea.cancelBlinking();
		dateArea.setColonBlinking(true);

		if (historyManager.getTestResults().size() == 0
				|| !currentStatus.isPCReady() || !currentStatus.isSoftwareReady()) {
			// =====the meter shall blink symbol U and go through Error Ending
			// procedure=====
			statusArea.setModeBlinking(Mode.UPLOADING);
			//showBlinkingView();

			Message message = Message.obtain(handler,
					Interrupt.ERROR_ENDING.ordinal());
			if (historyManager.getTestResults().isEmpty()) {
				message.arg1 = ErrorCode.U_NO_TEST_RESULTS.getErrorCode();
			} else if (!currentStatus.isPCReady()) {
				message.arg1 = ErrorCode.PC_NOT_READY.getErrorCode();
			} else if (!currentStatus.isSoftwareReady()) {
				message.arg1 = ErrorCode.SOFTWARE_NOT_READY.getErrorCode();
			}
			message.sendToTarget();

			BUTTONHASCLICKED = true;
		}
		else {
			// =====the meter should give a reminding beep, display symbol U,
			// and enter the Uploading Mode.=====
			Beeper.get().doRemindBeep(context);
			this.restartAutoEnding();
		}
	}

	@Override
	public void onShortClick() {
		if(!BUTTONHASCLICKED)
		{
			historyManager.deleteAllTestResults();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			showBlinkingView();
			this.restartAutoEnding();
			BUTTONHASCLICKED = true;
		}
	}

	@Override
	public void onLongClick() {
		//=====not handled=====
	}

	@Override
	public void onDoubleClick() {
		//=====not handled=====
	}
	
	
	public void startUploading(){
		CurrentStatus currentStatus = new CurrentStatus(preferences);
		currentStatus.setPowerOn(true);
		currentStatus.setCurrentMode(Mode.UPLOADING);
//		currentStatus.setUSBConnected(true); should not be set here
		currentStatus.commit();
		statusArea.setCurrentMode(Mode.UPLOADING);
		statusArea.setVisible(true);
		dateArea.setVisible(true);
		BUTTONHASCLICKED = false;
		
		this.restartAutoEnding();
	}
	
	public void stopUploading(){
		 CurrentStatus currentStatus = new CurrentStatus(preferences);
		 
		 //error beep???
//		 Beeper.get().doErrorBeep(context);
		 statusArea.setVisible(false);
		 dateArea.setVisible(false);
//		 currentStatus.setUSBConnected(false); should not be set here!
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
		startUploading();
	}

	@Override
	public void onUSBDisconnected() {
		this.clearAutoEndingTask();
		stopUploading();
	}
}
