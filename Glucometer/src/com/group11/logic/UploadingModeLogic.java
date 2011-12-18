package com.group11.logic;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
	
	private boolean BUTTONHASCLICKED = false;
	private HistoryManager historyManager = new HistoryManager(context);
	private  ScheduledExecutorService scheduler =
		     Executors.newScheduledThreadPool(1);
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			stopUploading();
			scheduler.shutdown();
		}
	};
	private Timer timer = new Timer();
	private TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			stopUploading();
			timerTask.cancel();
		}
	};

	@Override
	public void validateMode() {
		super.validateMode();
		
		//TODO PC
	}

	@Override
	public void onShortClick() {
		// TODO Auto-generated method stub
		if(!BUTTONHASCLICKED && historyManager.getTestResults().size()!=0)
		{
			Beeper.get().doRemindBeep(context);
			historyManager.deleteAllTestResults();
			Beeper.get().doDoubleBeep(context);
			showBlinkingView();
			//timer.schedule(timerTask, 10000);
			//scheduler.schedule(runnable, 10, TimeUnit.SECONDS);
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
	}
	
	public void stopUploading(){
		 CurrentStatus currentStatus = new CurrentStatus(preferences);
		 Beeper.get().doErrorBeep(context);
		 statusArea.setVisible(false);
		 dateArea.setVisible(false);
		 currentStatus.setUSBConnected(false);
//		 currentStatus.setCurrentMode(null); do not set currentMode(null) here!!!!!
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
		CurrentStatus currentStatus = new CurrentStatus(preferences);
		startUploading();
		if (historyManager.getTestResults().size() == 0 || currentStatus.isPCReady()) {
			showBlinkingView();
		} 
		else {
			}
		//timer.schedule(timerTask, 5000);
		//scheduler.schedule(runnable, 10, TimeUnit.SECONDS);
		}

	@Override
	public void onUSBDisconnected() {
		// TODO Auto-generated method stub
		//timerTask.cancel();
		//scheduler.shutdownNow();
		stopUploading();
	}
	

}
