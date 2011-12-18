package com.group11.logic;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.group11.base.ErrorCode;
import com.group11.base.Interrupt;
import com.group11.base.Mode;
import com.group11.base.TestResult;
import com.group11.hardware.Beeper;
import com.group11.hardware.CurrentStatus;
import com.group11.ui.DateArea;
import com.group11.ui.ProgressBarArea;
import com.group11.ui.ResultArea;
import com.group11.ui.StatusArea;
import com.group11.util.HistoryManager;

/**
 * the logical controller in Testing Mode, it judges what to do
 */
public class TestingModeLogic extends ModeLogic {
	
	private static final int WAIT_FOR_BLOOD_TIME = 10000;
	private static final int DISPLAY_RESULT_TIME = 10000;

	public TestingModeLogic(StatusArea status, ResultArea result,
			ProgressBarArea progressBar, DateArea date, Context context,
			SharedPreferences preferences, Handler handler) {
		super(status, result, progressBar, date, context, preferences, handler);
	}
	
	private TimerTask waitForBloodTask = null;
	private TimerTask displayResultTask = null;

	/**
	 * Mode validation process is to check whether a test strip is valid (never
	 * been used before and unstained).
	 */
	@Override
	public void validateMode() {
		super.validateMode();
		
		CurrentStatus status = new CurrentStatus(preferences);
		status.setCurrentMode(Mode.TESTING);
		status.setRefreshingTime(true);
		status.commit();

		if (status.isStripValid()) {
			//===== If valid, interrupt 6 shall be issue===== 
			Message message = Message.obtain(handler,
					Interrupt.STRIP_VALID.ordinal());
			message.sendToTarget();
		}
		else {
			// =====interrupt 5 shall be issued=====
			Message message = Message.obtain(handler,
					Interrupt.STRIP_INVALID.ordinal());
			message.sendToTarget();
		}
	}

	@Override
	public void onShortClick() {
		//=====Undefined Action, ignored=====
	}

	@Override
	public void onLongClick() {
		//=====Undefined Action, ignored=====
	}

	@Override
	public void onDoubleClick() {
		//=====Undefined Action, ignored=====
	}

	@Override
	public void onStripInserted() {
		throw new IllegalStateException("it should never enter this method.");
	}

	@Override
	public void onStripPulledOut() {
		//=====the meter should go through the Voluntary Ending procedure=====
		this.clearWaitForBloodTask();
		this.clearDisplayResultTask();
		
		Message message = Message.obtain(handler, Interrupt.POWER_OFF.ordinal());
		message.sendToTarget();
		Beeper.get().doTurnOffBeep(context);
	}

	@Override
	public void onUSBConnected() {
		//=====Undefined Action, ignored=====
	}

	@Override
	public void onUSBDisconnected() {
		//=====Undefined Action, ignored=====
	}
	
	private void initWaitForBloodTask() {
		waitForBloodTask = new TimerTask() {
			
			@Override
			public void run() {
				Message message = Message.obtain(handler,
						Interrupt.VOLUNTARY_ENDING.ordinal());
				message.sendToTarget();
			}
		};
	}
	
	private void clearWaitForBloodTask() {
		if (waitForBloodTask != null) {
			waitForBloodTask.cancel();
			waitForBloodTask = null;
		}
	}

	public void onStripValid() {
		//===== the meter enters the Testing Mode after a reminding-beep and
		// displaying symbol T=====
		Beeper.get().doRemindBeep(context);
		
		statusArea.setVisible(true);
		statusArea.setCurrentMode(Mode.TESTING);
		statusArea.setErroring(false);
		statusArea.cancelBlinking();

		resultArea.setVisible(false);
		progressBarArea.setVisible(false);

		dateArea.setVisible(true);
		dateArea.setColonBlinking(true);
		
		this.clearWaitForBloodTask();
		this.initWaitForBloodTask();
		new Timer().schedule(waitForBloodTask, WAIT_FOR_BLOOD_TIME);
	}
	
	public void onStripInvalid() {
		// the meter should blink symbol T and go through Error Ending procedure
		// with an error-beep.
		statusArea.setVisible(true);
		statusArea.setCurrentMode(Mode.TESTING);
		statusArea.setErroring(false);
		statusArea.cancelBlinking();
		statusArea.setModeBlinking(Mode.TESTING);

		resultArea.setVisible(false);
		progressBarArea.setVisible(false);

		dateArea.setVisible(true);
		dateArea.setColonBlinking(true);

		Message message = Message.obtain(handler, Interrupt.ERROR_ENDING.ordinal());
		message.arg1 = ErrorCode.INVALID_STRIP.getErrorCode();
		message.sendToTarget();
	}
	
	public void onBloodSufficient() {
		this.clearWaitForBloodTask();

		progressBarArea.setVisible(true);
		progressBarArea.cancelProgress();
		progressBarArea.startProgress(handler, preferences);
	}
	
	public void onBloodInsufficient() {
		this.clearWaitForBloodTask();

		Message message = Message.obtain(handler,
				Interrupt.ERROR_ENDING.ordinal());
		message.arg1 = ErrorCode.INSUFFICIENT_BLOOD.getErrorCode();
		message.sendToTarget();
	}
	
	public void onResultReady(TestResult result) {
		progressBarArea.setVisible(false);
		
		resultArea.setVisible(true);
		resultArea.displayResult(result.getValue(), new CurrentStatus(
				preferences).getCurrentUnit());
		
		new HistoryManager(context).addTestResult(result);
		
		this.clearDisplayResultTask();
		this.initDisplayResultTask();
		new Timer().schedule(displayResultTask, DISPLAY_RESULT_TIME);
	}
	
	public void onResultTimeout() {
		progressBarArea.setVisible(false);
		
		Message message = Message.obtain(handler,
				Interrupt.ERROR_ENDING.ordinal());
		message.arg1 = ErrorCode.TEST_TIMEOUT.getErrorCode();
		message.sendToTarget();
	}
	
	private void clearDisplayResultTask() {
		if (displayResultTask != null) {
			displayResultTask.cancel();
			displayResultTask = null;
		}
	}
	
	private void initDisplayResultTask() {
		displayResultTask = new TimerTask() {
			
			@Override
			public void run() {
				Message message = Message.obtain(handler,
						Interrupt.VOLUNTARY_ENDING.ordinal());
				message.sendToTarget();
			}
		};
	}
	
	public void startTestingMode(){
		CurrentStatus currentStatus = new CurrentStatus(preferences);
		currentStatus.setPowerOn(true);
		currentStatus.setCurrentMode(Mode.TESTING);
//		currentStatus.setStripInserted(true); do not set here!
		currentStatus.commit();
	}
	
	public void stopTestingMode(){
		 CurrentStatus currentStatus = new CurrentStatus(preferences);
		 Beeper.get().doErrorBeep(context);
//		 currentStatus.setStripInserted(false); do not set here!
//		 currentStatus.setCurrentMode(null); do not set currentMode(null) here!!!
		 currentStatus.commit();
		Message message = Message
				.obtain(handler, Interrupt.POWER_OFF.ordinal());
		message.sendToTarget();
	}
}
